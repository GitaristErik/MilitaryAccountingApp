package com.example.militaryaccountingapp.presenter.shared.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.militaryaccountingapp.presenter.model.Barcode

object BarCodeDiffCallback : DiffUtil.ItemCallback<Barcode>() {
    override fun areItemsTheSame(oldItem: Barcode, newItem: Barcode): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Barcode, newItem: Barcode): Boolean {
        return oldItem.id == newItem.id
    }
}