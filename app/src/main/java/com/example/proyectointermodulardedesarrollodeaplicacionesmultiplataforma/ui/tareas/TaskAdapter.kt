package com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.ui.tareas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.R

class TaskAdapter(
    private val items: MutableList<Task>,
    private val onItemClick: (Task) -> Unit,
    private val onEdit: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.textViewTaskTitle)
        val date: TextView = view.findViewById(R.id.textViewTaskDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task_with_date, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val t = items[position]
        holder.title.text = t.title
        holder.date.text = t.dateKey
        holder.itemView.setOnClickListener { onItemClick(t) }
        holder.itemView.setOnLongClickListener {
            onEdit(t)
            true
        }
    }

    override fun getItemCount(): Int = items.size

    fun setList(newList: List<Task>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}
