package com.example.militaryaccountingapp.presenter.utils.ui.ext

import android.view.View
fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.isVisible() = visibility == View.VISIBLE

fun View.visibleIf(predicate: Boolean) {
    this.visibility = if (predicate) View.VISIBLE else View.GONE
}