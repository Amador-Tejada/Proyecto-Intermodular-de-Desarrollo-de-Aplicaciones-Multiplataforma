package com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.ui.tareas

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.R
import com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.ui.TaskViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private val taskViewModel: TaskViewModel by activityViewModels()
    private lateinit var adapter: TaskAdapter
    private lateinit var calendarView: MaterialCalendarView
    private val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private var fab: com.google.android.material.floatingactionbutton.FloatingActionButton? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewTasks)
        calendarView = view.findViewById(R.id.materialCalendarView)

        // El clic corto selecciona la fecha, el clic largo edita la tarea.
        adapter = TaskAdapter(mutableListOf(),
            onItemClick = { task -> selectDateForTask(task) },
            onEdit = { task -> showAddEditDialog(task) }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Seleccionar la fecha de hoy por defecto
        calendarView.selectedDate = CalendarDay.today()

        // Observadores del ViewModel
        taskViewModel.tasksMap.observe(viewLifecycleOwner) {
            // Cargar todas las tareas en la lista de abajo
            val allTasks = taskViewModel.getAllTasks()
            adapter.setList(allTasks)
        }

        taskViewModel.daysWithTasks.observe(viewLifecycleOwner) { days ->
            val decorator = EventDecorator(resources.getColor(R.color.principalAplicacion), days)
            calendarView.removeDecorators()
            calendarView.addDecorator(decorator)
        }
    }

    private fun selectDateForTask(task: Task) {
        val parts = task.dateKey.split("-").map { it.toInt() }
        val calendar = Calendar.getInstance().apply {
            set(parts[0], parts[1] - 1, parts[2])
        }
        val calendarDay = CalendarDay.from(calendar.time)

        // Mover el calendario a la fecha y seleccionarla
        calendarView.setCurrentDate(calendarDay, true)
        calendarView.setDateSelected(calendarDay, true)
    }

    override fun onResume() {
        super.onResume()
        fab = requireActivity().findViewById(R.id.fabAdd)
        fab?.apply {
            show()
            setOnClickListener { showAddEditDialog(null) }
        }
    }

    override fun onPause() {
        super.onPause()
        fab?.setOnClickListener(null)
        fab?.hide()
    }

    private fun showAddEditDialog(taskToEdit: Task?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_task, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.editTextTitle)
        val tvDate = dialogView.findViewById<TextView>(R.id.textDate)
        val btnPick = dialogView.findViewById<Button>(R.id.buttonPickDate)
        val btnCancel = dialogView.findViewById<Button>(R.id.buttonCancel)
        val btnAdd = dialogView.findViewById<Button>(R.id.buttonAdd)

        // Usa la fecha de la tarea a editar, o la fecha seleccionada en el calendario, o la de hoy
        val initialDate = taskToEdit?.dateKey ?: calendarView.selectedDate?.let { fmt.format(it.date) } ?: fmt.format(Date())
        var dialogDate = initialDate

        tvDate.text = dialogDate
        etTitle.setText(taskToEdit?.title ?: "")

        btnAdd.text = if (taskToEdit == null) getString(R.string.add) else "Guardar"

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnPick.setOnClickListener {
            val c = Calendar.getInstance()
            val parts = dialogDate.split("-").map { it.toInt() }
            c.set(parts[0], parts[1] - 1, parts[2])
            DatePickerDialog(requireContext(), { _, y, m, d ->
                c.set(y, m, d)
                dialogDate = fmt.format(c.time)
                tvDate.text = dialogDate
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnAdd.setOnClickListener {
            val title = etTitle.text.toString().trim()
            if (title.isNotEmpty()) {
                if (taskToEdit == null) {
                    taskViewModel.addTask(Task(title = title, dateKey = dialogDate))
                } else {
                    taskViewModel.updateTask(taskToEdit, title, dialogDate)
                }
            }
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
