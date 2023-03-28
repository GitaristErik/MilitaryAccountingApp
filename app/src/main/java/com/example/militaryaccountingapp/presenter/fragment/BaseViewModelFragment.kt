package com.example.militaryaccountingapp.presenter.fragment

import android.os.Bundle
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.militaryaccountingapp.presenter.BaseViewModel
import kotlinx.coroutines.launch

abstract class BaseViewModelFragment<VB : ViewBinding, VD, VM : BaseViewModel<VD>> :
    BaseFragment<VB>() {

    protected abstract val viewModel: VM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log.d("onViewCreated: base with ViewModel")
        observeData()
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.data
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { render(it) }
        }
    }

    protected open fun render(data: VD) = Unit

    @VisibleForTesting
    fun setInitialData(initialData: VD) {
        viewModel.setInitialData(initialData)
    }
}