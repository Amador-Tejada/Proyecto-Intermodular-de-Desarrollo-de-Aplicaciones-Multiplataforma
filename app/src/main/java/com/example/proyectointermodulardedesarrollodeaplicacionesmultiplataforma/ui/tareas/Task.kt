package com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.ui.tareas

// Modelo simple para una tarea
data class Task(
    val id: Long = System.currentTimeMillis(),
    var title: String,
    var dateKey: String // formato "yyyy-MM-dd"
)

