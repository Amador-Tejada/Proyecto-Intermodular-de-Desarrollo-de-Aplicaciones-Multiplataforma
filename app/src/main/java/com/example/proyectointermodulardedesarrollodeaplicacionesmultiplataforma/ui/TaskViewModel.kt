package com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.ui.tareas.Task
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.util.Calendar

/**
 * ViewModel compartido que actúa como la única fuente de verdad para los datos de las tareas.
 * Sobrevive a los cambios de configuración (como giros de pantalla) y permite que los datos
 * se compartan de forma segura y consistente entre diferentes fragmentos.
 */
class TaskViewModel : ViewModel() {

    // _tasksMap es la versión mutable y privada de los datos. Solo el ViewModel puede modificarla.
    // La clave del mapa es un String con la fecha ("yyyy-MM-dd") y el valor es la lista de tareas de ese día.
    private val _tasksMap = MutableLiveData<MutableMap<String, MutableList<Task>>>(mutableMapOf())

    // tasksMap es la versión pública e inmutable. Los fragmentos la observan para detectar cambios,
    // pero no pueden modificarla directamente, asegurando que toda la lógica de negocio esté aquí.
    val tasksMap: LiveData<MutableMap<String, MutableList<Task>>> = _tasksMap

    // LiveData que contiene un conjunto de los días que tienen al menos una tarea.
    // El calendario lo observa para saber en qué días debe dibujar un punto (decorador).
    private val _daysWithTasks = MutableLiveData<HashSet<CalendarDay>>(hashSetOf())
    val daysWithTasks: LiveData<HashSet<CalendarDay>> = _daysWithTasks

    /**
     * Añade una nueva tarea al mapa.
     * @param task La tarea a añadir.
     */
    fun addTask(task: Task) {
        // Obtenemos el mapa actual o creamos uno nuevo si no existe.
        val currentMap = _tasksMap.value ?: mutableMapOf()
        // Usamos getOrPut para obtener la lista de tareas de esa fecha, o crear una nueva si es la primera tarea del día.
        currentMap.getOrPut(task.dateKey) { mutableListOf() }.add(task)
        // Actualizamos el LiveData con el nuevo mapa, lo que notificará a los observadores.
        _tasksMap.value = currentMap
        // Refrescamos la lista de días que tienen tareas para actualizar los puntos del calendario.
        updateDaysWithTasks()
    }

    /**
     * Actualiza una tarea existente. Puede cambiar su título y/o su fecha.
     * @param oldTask La tarea original antes de la edición.
     * @param newTitle El nuevo título de la tarea.
     * @param newDateKey La nueva fecha en formato "yyyy-MM-dd".
     */
    fun updateTask(oldTask: Task, newTitle: String, newDateKey: String) {
        val currentMap = _tasksMap.value ?: return

        // Comprobamos si la fecha ha cambiado, ya que requiere mover la tarea de una lista a otra.
        if (oldTask.dateKey != newDateKey) {
            // Eliminamos la tarea de la lista de su fecha antigua.
            currentMap[oldTask.dateKey]?.remove(oldTask)
            // Si la lista de la fecha antigua se ha quedado vacía, eliminamos esa fecha del mapa para no guardar claves inútiles.
            if (currentMap[oldTask.dateKey]?.isEmpty() == true) {
                currentMap.remove(oldTask.dateKey)
            }
            // Actualizamos la fecha de la tarea y la añadimos a la lista de su nueva fecha.
            oldTask.dateKey = newDateKey
            currentMap.getOrPut(newDateKey) { mutableListOf() }.add(oldTask)
        }
        // Actualizamos el título de la tarea.
        oldTask.title = newTitle

        // Notificamos a los observadores con el mapa actualizado.
        _tasksMap.value = currentMap
        // Refrescamos los puntos del calendario.
        updateDaysWithTasks()
    }

    /**
     * Devuelve una única lista con todas las tareas de todos los días, ordenadas por fecha.
     * @return Una lista aplanada y ordenada de tareas.
     */
    fun getAllTasks(): List<Task> {
        return _tasksMap.value?.values?.flatten()?.sortedBy { it.dateKey } ?: emptyList()
    }

    /**
     * Función privada que recalcula el conjunto de días que tienen tareas.
     * Se llama cada vez que se añade o se modifica una tarea.
     */
    private fun updateDaysWithTasks() {
        val currentDays = hashSetOf<CalendarDay>()
        _tasksMap.value?.keys?.forEach { key ->
            val parts = key.split("-").map { it.toInt() }
            val calendar = Calendar.getInstance().apply {
                set(parts[0], parts[1] - 1, parts[2]) // Meses en Calendar son 0-11
            }
            currentDays.add(CalendarDay.from(calendar.time))
        }
        // Actualiza el LiveData, lo que hará que el calendario repinte sus decoradores.
        _daysWithTasks.value = currentDays
    }
}
