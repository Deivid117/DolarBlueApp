package com.varqulabs.dolarblue.core.presentation.utils.date

import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

fun getCurrentLocalDateTime(): LocalDateTime {
    val now = Clock.System.now()
    val tz = TimeZone.currentSystemDefault()
    return now.toLocalDateTime(tz)
}

// TODO: Poner los strings en minúscula y usar uppercase
fun compareDates(date: LocalDateTime): String {
    val currentDateTime = getCurrentLocalDateTime()
    val currentDateMinus1Day = currentDateTime.date.minus(DatePeriod(days = 1))

    val isTheDateYesterday = date.date.compareTo(currentDateMinus1Day)
    val isTheDateToday = date.date.compareTo(currentDateTime.date)

    return when {
        isTheDateToday == 0 -> "Hoy"
        isTheDateYesterday == 0 -> "Ayer"
        else -> formatDateString(date.date.toString())
    }
}

fun formatDateString(date: String): String {
    // Primero dividimos la cadena en partes
    val datePart = date.substringBefore('T')
    val (year, month, day) = datePart.split('-').map { it.toInt() }

    // Obtenemos el nombre del mes en español
    val months = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )
    val monthName = months[month - 1]

    // Formateamos la cadena de salida
    return "$day $monthName $year"
}
