package com.example.militaryaccountingapp.presenter.utils.common.ext

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.toCapitalize() = this.replaceFirstChar { it.titlecase() }

fun String.toTitleCase() = this.split(" ").joinToString(" ") { it.toCapitalize() }

/**
 * Parse timestamp to date
 * @return date in format "dd MMM yyyy HH:mm"
 */
fun Long.asFormattedDateString(): String {
    val date = Date(this)
    val dateFormat: DateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
    return dateFormat.format(date)
}