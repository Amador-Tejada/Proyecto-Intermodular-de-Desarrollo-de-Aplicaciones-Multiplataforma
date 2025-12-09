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

class HomeFragment : Fragment() {

    private val taskViewModel: TaskViewModel by activityViewModels()
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewAllTasks)

        // En esta lista, un clic no hará nada. Solo es para visualización.
        adapter = TaskAdapter(mutableListOf(),
            onItemClick = { /* No action */ },
            onEdit = { /* No action */ }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Observar el mapa de tareas del ViewModel
        taskViewModel.tasksMap.observe(viewLifecycleOwner) {
            val allTasks = taskViewModel.getAllTasks()
            adapter.setList(allTasks)
        }
    }

    override fun onResume() {
        super.onResume()
        // Ocultar el botón flotante en esta pantalla
        requireActivity().findViewById<FloatingActionButton>(R.id.fabAdd)?.hide()
    }
}
