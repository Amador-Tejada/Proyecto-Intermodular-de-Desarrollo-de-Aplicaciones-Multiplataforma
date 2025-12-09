package com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.ui.tareas.Task
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.util.Calendar

class TaskViewModel : ViewModel() {

    // Mapa para almacenar tareas. La clave es una fecha en formato "yyyy-MM-dd"
    private val _tasksMap = MutableLiveData<MutableMap<String, MutableList<Task>>>(mutableMapOf())
    val tasksMap: LiveData<MutableMap<String, MutableList<Task>>> = _tasksMap

    // LiveData para los días que tienen tareas, para decorar el calendario
    private val _daysWithTasks = MutableLiveData<HashSet<CalendarDay>>(hashSetOf())
    val daysWithTasks: LiveData<HashSet<CalendarDay>> = _daysWithTasks

    fun addTask(task: Task) {
        val currentMap = _tasksMap.value ?: mutableMapOf()
        currentMap.getOrPut(task.dateKey) { mutableListOf() }.add(task)
        _tasksMap.value = currentMap
        updateDaysWithTasks()
    }

    fun updateTask(oldTask: Task, newTitle: String, newDateKey: String) {
        val currentMap = _tasksMap.value ?: return

        // Si la fecha cambió, mover la tarea de lista
        if (oldTask.dateKey != newDateKey) {
            currentMap[oldTask.dateKey]?.remove(oldTask)
            // Limpiar lista vacía
            if (currentMap[oldTask.dateKey]?.isEmpty() == true) {
                currentMap.remove(oldTask.dateKey)
            }
            oldTask.dateKey = newDateKey
            currentMap.getOrPut(newDateKey) { mutableListOf() }.add(oldTask)
        }
        oldTask.title = newTitle

        _tasksMap.value = currentMap
        updateDaysWithTasks()
    }

    fun getAllTasks(): List<Task> {
        return _tasksMap.value?.values?.flatten()?.sortedBy { it.dateKey } ?: emptyList()
    }

    private fun updateDaysWithTasks() {
        val currentDays = hashSetOf<CalendarDay>()
        _tasksMap.value?.keys?.forEach { key ->
            val parts = key.split("-").map { it.toInt() }
            val calendar = Calendar.getInstance().apply {
                set(parts[0], parts[1] - 1, parts[2])
            }
            currentDays.add(CalendarDay.from(calendar.time))
        }
        _daysWithTasks.value = currentDays
    }
}
