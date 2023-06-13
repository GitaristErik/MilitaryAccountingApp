package com.example.militaryaccountingapp.presenter.shared.delegation.implementation

import android.content.Context
import android.view.View
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.LayoutErrorFullPageBinding
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.helper.Results.FailureType.EMPTY
import com.example.militaryaccountingapp.domain.helper.Results.FailureType.NETWORK
import com.example.militaryaccountingapp.domain.helper.Results.FailureType.SERVER
import com.example.militaryaccountingapp.presenter.shared.delegation.ErrorHandlerScreen
import com.example.militaryaccountingapp.presenter.utils.ui.ext.gone
import com.example.militaryaccountingapp.presenter.utils.ui.ext.visible
import com.example.militaryaccountingapp.presenter.utils.ui.ext.visibleIf

class ErrorHandlerScreenImpl : ErrorHandlerScreen {
    override fun onHandleError(
        context: Context,
        mainViewContent: View,
        layoutError: LayoutErrorFullPageBinding,
        data: Results<*>,
        page: Long,
        onRetry: (() -> Unit)?,
        onErrorNextPage: (() -> Unit)?,
    ) {
        if (data is Results.Failure) {
            if (page == 1L) {
                mainViewContent.gone()
                layoutError.root.visible()
                val errorType = data.type
                val errorMessage = when (errorType) {
                    EMPTY -> context.getString(R.string.error_message_empty)
                    SERVER -> context.getString(R.string.error_message_server)
                    NETWORK -> context.getString(R.string.error_message_network)
                    else -> context.getString(R.string.error_message_other)
                }
                layoutError.errorMessage.text = errorMessage
                layoutError.retryButton.visibleIf(errorType != EMPTY)
                layoutError.retryButton.setOnClickListener { onRetry?.invoke() }
            } else {
                onErrorNextPage?.invoke()
            }
        } else {
            layoutError.root.gone()
        }
    }
}