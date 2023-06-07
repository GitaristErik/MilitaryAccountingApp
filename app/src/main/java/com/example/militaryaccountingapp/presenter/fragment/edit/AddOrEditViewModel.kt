package com.example.militaryaccountingapp.presenter.fragment.edit

import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.edit.AddOrEditViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.Barcode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddOrEditViewModel @Inject constructor(
) : BaseViewModel<ViewData>(ViewData()) {

    data class ViewData(
        val codes: Set<Barcode> = emptySet()
    )

    init {
        Timber.d("init")
    }

    companion object {
        private const val DELAY = 400L
    }

    fun addCode(code: String) {
        log.d("addCode $code")

        val barcode = Barcode(
            id = _data.value.codes.size,
            code = code,
            timestamp = Date().time
        )

        _data.update {
            it.copy(codes = it.codes.plus(barcode))
        }
    }

    fun deleteCode(id: Int) {
        log.d("deleteCode $id")
        deleteCode(getCode(id) ?: return)
    }

    fun deleteCode(code: Barcode) = _data.update { viewData ->
        viewData.copy(codes = viewData.codes.minus(code))
    }

    fun getCode(id: Int) = _data.value.codes.find { it.id == id }

    // TODO push barcodes to database
}