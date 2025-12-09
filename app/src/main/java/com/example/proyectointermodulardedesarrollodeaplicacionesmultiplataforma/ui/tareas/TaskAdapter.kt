package com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.ui.tareas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.R

/**
 * Adaptador para el RecyclerView que muestra la lista de tareas.
 * Es el puente entre los datos (la lista de tareas) y la interfaz de usuario (el RecyclerView).
 * Se encarga de crear y gestionar las vistas para cada elemento de la lista.
 *
 * @param items La lista mutable de tareas que el adaptador mostrará.
 * @param onItemClick Una función lambda que se ejecutará cuando se haga un clic corto en un elemento.
 * @param onEdit Una función lambda que se ejecutará cuando se haga un clic largo en un elemento.
 */
class TaskAdapter(
    private val items: MutableList<Task>,
    private val onItemClick: (Task) -> Unit,
    private val onEdit: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.VH>() {

    /**
     * ViewHolder (VH): Representa una única fila en la lista.
     * Contiene las referencias a las vistas (TextViews) dentro del layout de esa fila.
     * Esto evita tener que buscar las vistas con `findViewById` cada vez que se recicla una fila, mejorando el rendimiento.
     */
    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.textViewTaskTitle)
        val date: TextView = view.findViewById(R.id.textViewTaskDate)
    }

    /**
     * Se llama cuando el RecyclerView necesita crear una nueva fila (un nuevo ViewHolder).
     * Esto solo ocurre las primeras veces, hasta que hay suficientes filas para reciclar.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        // Inflamos (cargamos) el layout XML `item_task_with_date` que hemos diseñado para cada fila.
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task_with_date, parent, false)
        // Creamos y devolvemos una nueva instancia de nuestro ViewHolder con la vista de la fila.
        return VH(v)
    }

    /**
     * Se llama para vincular los datos de una tarea específica con una vista (un ViewHolder).
     * Esto ocurre cada vez que una fila se va a mostrar en pantalla, ya sea nueva o reciclada.
     * @param holder El ViewHolder que debe ser actualizado con los datos.
     * @param position La posición del elemento en la lista de datos.
     */
    override fun onBindViewHolder(holder: VH, position: Int) {
        // Obtenemos la tarea correspondiente a esta posición.
        val t = items[position]
        // Asignamos el título y la fecha de la tarea a los TextViews del ViewHolder.
        holder.title.text = t.title
        holder.date.text = t.dateKey

        // Asignamos los listeners para las interacciones del usuario.
        // Clic corto: Llama a la función `onItemClick` que nos pasaron en el constructor.
        holder.itemView.setOnClickListener { onItemClick(t) }
        // Clic largo: Llama a la función `onEdit`.
        holder.itemView.setOnLongClickListener {
            onEdit(t)
            true // Devolvemos `true` para indicar que hemos consumido el evento y no se propague más.
        }
    }

    /**
     * Devuelve el número total de elementos en la lista.
     * El RecyclerView lo usa para saber cuántas filas necesita mostrar en total.
     */
    override fun getItemCount(): Int = items.size

    /**
     * Función pública para actualizar la lista de tareas que muestra el adaptador.
     * @param newList La nueva lista de tareas a mostrar.
     */
    fun setList(newList: List<Task>) {
        // Limpiamos la lista interna actual.
        items.clear()
        // Añadimos todos los elementos de la nueva lista.
        items.addAll(newList)
        // Notificamos al RecyclerView que los datos han cambiado para que se repinte por completo.
        notifyDataSetChanged()
    }
}
