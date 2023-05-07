package com.example.militaryaccountingapp.presenter.shared.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.militaryaccountingapp.presenter.model.filter.UserFilterUi

object UsersFilterDiffCallback : DiffUtil.ItemCallback<UserFilterUi>() {
    override fun areItemsTheSame(oldItem: UserFilterUi, newItem: UserFilterUi): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: UserFilterUi, newItem: UserFilterUi): Boolean {
        return oldItem.id == newItem.id
    }
}