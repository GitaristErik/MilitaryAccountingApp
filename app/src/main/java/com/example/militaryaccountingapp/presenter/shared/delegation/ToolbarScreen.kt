package com.example.militaryaccountingapp.presenter.shared.delegation

import android.view.View

interface ToolbarScreen {

    fun setOnSettingsClickListener(
        onSettingsClick: ((View) -> Unit)
    )
}