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

/**
 * Fragment principal que actúa como el centro de control para la gestión de tareas.
 * Combina una vista de calendario (`MaterialCalendarView`) con una lista (`RecyclerView`)
 * de todas las tareas existentes. Permite añadir, editar y visualizar tareas de forma interactiva.
 */
class CalendarFragment : Fragment() {

    // Inyecta el ViewModel compartido a nivel de actividad. Esto asegura que los datos
    // de las tareas sean los mismos en todos los fragmentos que lo usen (el "cerebro" compartido).
    private val taskViewModel: TaskViewModel by activityViewModels()

    // Adaptador para el RecyclerView. Se encargará de "dibujar" cada tarea en la lista.
    private lateinit var adapter: TaskAdapter

    // Referencia a la vista del calendario del layout.
    private lateinit var calendarView: MaterialCalendarView

    // Herramienta para formatear fechas. Convierte objetos Date a un String con formato "año-mes-día" y viceversa.
    private val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Referencia al Botón de Acción Flotante (FAB) que se usa para añadir nuevas tareas.
    private var fab: com.google.android.material.floatingactionbutton.FloatingActionButton? = null

    /**
     * Se llama para crear la vista del fragmento. Aquí simplemente "inflamos" (cargamos)
     * el archivo de layout XML y lo devolvemos.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    /**
     * Se llama justo después de que la vista del fragmento ha sido creada.
     * Aquí es donde configuramos todas las vistas y la lógica principal.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtenemos las referencias a las vistas desde el layout.
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewTasks)
        calendarView = view.findViewById(R.id.materialCalendarView)

        // 1. CONFIGURACIÓN DEL ADAPTADOR DE LA LISTA
        // Se inicializa el adaptador. Le pasamos dos funciones lambda (código que se ejecuta más tarde):
        // - onItemClick: Se ejecutará cuando el usuario haga un clic corto en una tarea. Llama a `selectDateForTask`.
        // - onEdit: Se ejecutará cuando el usuario haga un clic largo. Llama a `showAddEditDialog`.
        adapter = TaskAdapter(mutableListOf(),
            onItemClick = { task -> selectDateForTask(task) },
            onEdit = { task -> showAddEditDialog(task) }
        )
        // Configuramos el RecyclerView para que muestre los elementos en una lista vertical.
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // 2. CONFIGURACIÓN INICIAL DEL CALENDARIO
        // Hacemos que el día de hoy aparezca seleccionado por defecto al abrir la pantalla.
        calendarView.selectedDate = CalendarDay.today()

        // 3. OBSERVADORES DEL VIEWMODEL (LA MAGIA DE LA SINCRONIZACIÓN)
        // Este bloque se ejecutará automáticamente cada vez que los datos de las tareas en el ViewModel cambien.
        taskViewModel.tasksMap.observe(viewLifecycleOwner) {
            // Cuando hay cambios, pedimos al ViewModel la lista completa y actualizada de tareas.
            val allTasks = taskViewModel.getAllTasks()
            // Le pasamos la nueva lista al adaptador para que repinte el RecyclerView.
            adapter.setList(allTasks)
        }

        // Este otro observador se fija en la lista de días que tienen tareas.
        taskViewModel.daysWithTasks.observe(viewLifecycleOwner) { days ->
            // Cuando esta lista cambia, creamos un "decorador" que dibujará los puntitos en el calendario.
            val decorator = EventDecorator(resources.getColor(R.color.principalAplicacion), days)
            // Limpiamos decoradores antiguos y añadimos el nuevo para refrescar los puntos.
            calendarView.removeDecorators()
            calendarView.addDecorator(decorator)
        }
    }

    /**
     * Función que se llama con el clic corto en una tarea de la lista.
     * Su objetivo es mover el calendario y seleccionar la fecha de la tarea pulsada.
     */
    private fun selectDateForTask(task: Task) {
        // Coge la fecha de la tarea (que es un String "yyyy-MM-dd") y la convierte a un objeto CalendarDay.
        val parts = task.dateKey.split("-").map { it.toInt() }
        val calendar = Calendar.getInstance().apply {
            set(parts[0], parts[1] - 1, parts[2]) // Meses en Calendar son 0-11
        }
        val calendarDay = CalendarDay.from(calendar.time)

        // ANTES de marcar el nuevo día, borramos cualquier selección anterior para que no se queden dos marcados.
        calendarView.clearSelection()

        // Le damos la orden al calendario para que se mueva a la página del mes correcto.
        calendarView.setCurrentDate(calendarDay, true)
        // Y finalmente, le decimos que seleccione (resalte en azul) el día de la tarea.
        calendarView.setDateSelected(calendarDay, true)
    }

    /**
     * Se llama cuando el fragmento se vuelve visible para el usuario.
     * Es el lugar ideal para configurar elementos de la UI que deben estar activos en esta pantalla.
     */
    override fun onResume() {
        super.onResume()
        // Buscamos el FAB en la actividad principal.
        fab = requireActivity().findViewById(R.id.fabAdd)
        fab?.apply {
            // Nos aseguramos de que el botón sea visible.
            show()
            // Le asignamos la acción: al pulsarlo, se abrirá el diálogo para añadir una nueva tarea.
            setOnClickListener { showAddEditDialog(null) }
        }
    }

    /**
     * Se llama cuando el fragmento deja de ser visible.
     * Es importante "limpiar" lo que hemos configurado en onResume.
     */
    override fun onPause() {
        super.onPause()
        // Quitamos el listener del FAB para evitar que se active desde otra pantalla.
        fab?.setOnClickListener(null)
        // Ocultamos el botón.
        fab?.hide()
    }

    /**
     * Muestra un diálogo para añadir una nueva tarea o editar una existente.
     * @param taskToEdit Si es `null`, el diálogo estará en modo "Añadir". Si contiene una tarea,
     * estará en modo "Editar" y rellenará los campos con los datos de esa tarea.
     */
    private fun showAddEditDialog(taskToEdit: Task?) {
        // Inflamos el layout personalizado para el diálogo.
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_task, null)
        // Obtenemos referencias a todas las vistas dentro del diálogo.
        val etTitle = dialogView.findViewById<EditText>(R.id.editTextTitle)
        val tvDate = dialogView.findViewById<TextView>(R.id.textDate)
        val btnPick = dialogView.findViewById<Button>(R.id.buttonPickDate)
        val btnCancel = dialogView.findViewById<Button>(R.id.buttonCancel)
        val btnAdd = dialogView.findViewById<Button>(R.id.buttonAdd)

        // Lógica inteligente para decidir la fecha inicial del diálogo:
        // 1. Si estamos editando una tarea, usa la fecha de esa tarea.
        // 2. Si no, si hay un día seleccionado en el calendario, usa ese día.
        // 3. Si no, como última opción, usa la fecha de hoy.
        val initialDate = taskToEdit?.dateKey ?: calendarView.selectedDate?.let { fmt.format(it.date) } ?: fmt.format(Date())
        var dialogDate = initialDate

        // Rellenamos los campos del diálogo con los datos iniciales.
        tvDate.text = dialogDate
        etTitle.setText(taskToEdit?.title ?: "") // Si es una nueva tarea, el título estará vacío.

        // Cambiamos el texto del botón principal: "Añadir" o "Guardar" según el modo.
        btnAdd.text = if (taskToEdit == null) getString(R.string.add) else "Guardar"

        // Creamos el AlertDialog usando un constructor (Builder).
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView) // Le asignamos nuestra vista personalizada.
            .create()

        // Acción del botón "Cambiar fecha".
        btnPick.setOnClickListener {
            // Abre el selector de fechas de Android.
            val c = Calendar.getInstance()
            val parts = dialogDate.split("-").map { it.toInt() }
            c.set(parts[0], parts[1] - 1, parts[2])
            DatePickerDialog(requireContext(), { _, y, m, d ->
                c.set(y, m, d)
                // Cuando el usuario selecciona una fecha, la actualizamos en la variable y en el TextView.
                dialogDate = fmt.format(c.time)
                tvDate.text = dialogDate
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Acción del botón "Añadir/Guardar".
        btnAdd.setOnClickListener {
            val title = etTitle.text.toString().trim() // Obtenemos el título y quitamos espacios en blanco.
            if (title.isNotEmpty()) { // Solo guardamos si el título no está vacío.
                if (taskToEdit == null) {
                    // MODO AÑADIR: Llamamos al ViewModel para que añada la nueva tarea.
                    taskViewModel.addTask(Task(title = title, dateKey = dialogDate))
                } else {
                    // MODO EDITAR: Llamamos al ViewModel para que actualice la tarea existente.
                    taskViewModel.updateTask(taskToEdit, title, dialogDate)
                }
            }
            // Cerramos el diálogo.
            dialog.dismiss()
        }

        // Acción del botón "Cancelar". Simplemente cierra el diálogo sin hacer nada.
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        // Finalmente, mostramos el diálogo en pantalla.
        dialog.show()
    }
}