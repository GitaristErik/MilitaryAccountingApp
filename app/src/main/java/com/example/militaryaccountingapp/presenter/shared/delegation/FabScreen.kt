package com.example.militaryaccountingapp.presenter.shared.delegation

import android.view.View

interface FabScreen {

    fun showFab()

    fun hideFab()

    fun setOnClickFabListener(
        onFabClick: ((View) -> Unit)
    )
}