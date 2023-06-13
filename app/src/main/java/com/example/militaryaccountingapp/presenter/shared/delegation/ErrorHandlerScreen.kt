package com.example.militaryaccountingapp.presenter.shared.delegation

import android.content.Context
import android.view.View
import com.example.militaryaccountingapp.databinding.LayoutErrorFullPageBinding
import com.example.militaryaccountingapp.domain.helper.Results

interface ErrorHandlerScreen {
    fun onHandleError(
        context: Context,
        mainViewContent: View,
        layoutError: LayoutErrorFullPageBinding,
        data: Results<*>,
        page: Long,
        onRetry: (() -> Unit)?,
        onErrorNextPage: (() -> Unit)?,
    )
}