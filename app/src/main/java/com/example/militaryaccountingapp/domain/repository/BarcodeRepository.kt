package com.example.militaryaccountingapp.domain.repository

import com.example.militaryaccountingapp.domain.entity.data.Barcode
import com.example.militaryaccountingapp.domain.helper.Results

interface BarcodeRepository {

    suspend fun deleteCode(codeId: String): Results<Void?>

    suspend fun getCode(id: String): Results<Barcode>

    suspend fun getCodes(ids: List<String>): Results<List<Barcode>>

    suspend fun updateCodes(barcodes: Set<Barcode>): Results<List<Barcode>>
    fun deleteCodes(ids: Set<String>): Results<Void?>

}