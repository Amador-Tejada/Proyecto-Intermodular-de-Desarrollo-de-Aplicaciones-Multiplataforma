package com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.R
import com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.ui.TaskViewModel
import com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.ui.tareas.TaskAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Fragment que muestra una lista de solo lectura de todas las tareas existentes.
 * Actúa como la pantalla "Lista de Tareas" en la navegación.
 * Su único propósito es visualizar un resumen de todas las tareas, ordenadas por fecha.
 */
class HomeFragment : Fragment() {

    // Inyecta el mismo ViewModel compartido que usa CalendarFragment.
    // Esto es crucial para que ambos fragmentos accedan y muestren los mismos datos.
    private val taskViewModel: TaskViewModel by activityViewModels()

    // Adaptador para el RecyclerView, el mismo tipo que en CalendarFragment.
    private lateinit var adapter: TaskAdapter

    /**
     * Carga el layout XML para la vista del fragmento.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    /**
     * Se llama después de que la vista ha sido creada. Aquí se configura el RecyclerView y el observador.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewAllTasks)

        // Se inicializa el adaptador. A diferencia de CalendarFragment, las funciones para los clics
        // se dejan vacías. Esto convierte a la lista en una de "solo lectura".
        adapter = TaskAdapter(mutableListOf(),
            onItemClick = { /* No hacer nada al hacer clic corto */ },
            onEdit = { /* No hacer nada al hacer clic largo */ }
        )

        // Se configura el RecyclerView para que sea una lista vertical.
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Se establece un observador en los datos del ViewModel.
        // Este código se ejecutará automáticamente cada vez que la lista de tareas cambie.
        taskViewModel.tasksMap.observe(viewLifecycleOwner) {
            // Cuando hay un cambio, se pide al ViewModel la lista completa de tareas.
            val allTasks = taskViewModel.getAllTasks()
            // Se actualiza la lista en el adaptador para que se repinte en la pantalla.
            adapter.setList(allTasks)
        }
    }

    /**
     * Se llama cuando el fragmento se vuelve visible.
     */
    override fun onResume() {
        super.onResume()
        // Buscamos el botón flotante (FAB) en la actividad y lo ocultamos.
        // Esto asegura que el botón de "Añadir" solo sea visible en la pantalla del calendario.
        requireActivity().findViewById<FloatingActionButton>(R.id.fabAdd)?.hide()
    }
}
