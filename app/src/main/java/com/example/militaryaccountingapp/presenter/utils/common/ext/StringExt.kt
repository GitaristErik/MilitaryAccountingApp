package com.example.militaryaccountingapp.presenter.utils.common.ext

fun String.toCapitalize() = this.replaceFirstChar { it.titlecase() }
fun String.toTitleCase() = this.split(" ").joinToString(" ") { it.toCapitalize() }