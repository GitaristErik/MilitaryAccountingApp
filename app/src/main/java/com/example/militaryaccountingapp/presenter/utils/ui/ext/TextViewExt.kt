package com.example.militaryaccountingapp.presenter.utils.ui.ext

import android.widget.TextView
import com.example.militaryaccountingapp.domain.helper.Results
import com.google.android.material.textfield.TextInputLayout

fun TextView.renderValidate(results: Results<String>, layout: TextInputLayout? = null) {
    when (results) {
        is Results.Success -> {
            this.text = results.data
            if (layout != null) {
                layout.error = null
            } else {
                this.error = null
            }
        }

        is Results.Failure -> {
            if (layout != null) {
                layout.error = results.throwable.message
            } else {
                this.error = results.throwable.message
            }
        }

        else -> {}
    }
}