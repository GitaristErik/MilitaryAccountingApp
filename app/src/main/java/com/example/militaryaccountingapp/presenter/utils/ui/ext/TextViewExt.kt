package com.example.militaryaccountingapp.presenter.utils.ui.ext

import android.widget.TextView
import com.example.militaryaccountingapp.domain.helper.Result
import com.google.android.material.textfield.TextInputLayout

fun TextView.renderValidate(result: Result<String>, layout: TextInputLayout? = null) {
    when (result) {
        is Result.Success -> {
            this.text = result.data
            if (layout != null) {
                layout.error = null
            } else {
                this.error = null
            }
        }

        is Result.Failure -> {
            if (layout != null) {
                layout.error = result.throwable.message
            } else {
                this.error = result.throwable.message
            }
        }

        else -> {}
    }
}