package com.example.militaryaccountingapp.presenter.shared.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.militaryaccountingapp.presenter.model.UserNetworkUi

object UsersNetworkDiffCallback : DiffUtil.ItemCallback<UserNetworkUi>() {
    override fun areItemsTheSame(oldItem: UserNetworkUi, newItem: UserNetworkUi): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: UserNetworkUi, newItem: UserNetworkUi): Boolean {
        return oldItem.id == newItem.id &&
                oldItem.readCount == newItem.readCount &&
                oldItem.readShareCount == newItem.readShareCount &&
                oldItem.editCount == newItem.editCount &&
                oldItem.editShareCount == newItem.editShareCount
    }
}