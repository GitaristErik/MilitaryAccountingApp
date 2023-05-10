package com.example.militaryaccountingapp.presenter.shared.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.militaryaccountingapp.presenter.model.CategoryUi

object CategoryDiffCallback : DiffUtil.ItemCallback<CategoryUi>() {
    override fun areItemsTheSame(oldItem: CategoryUi, newItem: CategoryUi): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: CategoryUi, newItem: CategoryUi): Boolean {
        return oldItem.id == newItem.id
    }
}