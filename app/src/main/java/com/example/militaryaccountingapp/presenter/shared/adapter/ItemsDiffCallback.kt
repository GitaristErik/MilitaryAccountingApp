package com.example.militaryaccountingapp.presenter.shared.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.militaryaccountingapp.presenter.model.ItemUi

object ItemsDiffCallback : DiffUtil.ItemCallback<ItemUi>() {
    override fun areItemsTheSame(oldItem: ItemUi, newItem: ItemUi): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ItemUi, newItem: ItemUi): Boolean {
        return oldItem.id == newItem.id
    }
}