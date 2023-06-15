package com.example.militaryaccountingapp.presenter.shared.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.militaryaccountingapp.presenter.model.UserSearchUi

object UsersSearchDiffCallback : DiffUtil.ItemCallback<UserSearchUi>() {
    override fun areItemsTheSame(oldItem: UserSearchUi, newItem: UserSearchUi): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: UserSearchUi, newItem: UserSearchUi): Boolean {
        return oldItem.id == newItem.id
    }
}