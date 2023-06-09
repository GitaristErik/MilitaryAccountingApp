package com.example.militaryaccountingapp.presenter.shared.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.militaryaccountingapp.presenter.model.TimelineUi

object TimelineDiffCallback : DiffUtil.ItemCallback<TimelineUi>() {
    override fun areItemsTheSame(oldItem: TimelineUi, newItem: TimelineUi): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: TimelineUi, newItem: TimelineUi): Boolean {
        return oldItem.title == newItem.title
    }
}