package com.example.proyectointermodulardedesarrollodeaplicacionesmultiplataforma.ui.tareas

import android.graphics.Color
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.spans.DotSpan

class EventDecorator(private val color: Int, private val dates: HashSet<CalendarDay>) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        // Dibuja un punto pequeño debajo del día
        view.addSpan(DotSpan(6f, color))
    }
}
