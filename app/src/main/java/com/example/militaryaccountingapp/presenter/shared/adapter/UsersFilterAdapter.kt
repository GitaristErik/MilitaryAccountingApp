package com.example.militaryaccountingapp.presenter.shared.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.militaryaccountingapp.databinding.ItemFilterUserBinding
import com.example.militaryaccountingapp.presenter.model.filter.UserFilterUi
import com.example.militaryaccountingapp.presenter.shared.adapter.UsersFilterAdapter.UserViewHolder
import com.example.militaryaccountingapp.presenter.utils.TransitionUtils
import com.example.militaryaccountingapp.presenter.utils.ui.ext.load
import com.google.android.material.checkbox.MaterialCheckBox

class UsersFilterAdapter(
    private val onChange: ((Int, Boolean) -> Unit)?,
) : ListAdapter<UserFilterUi, UserViewHolder>(UsersFilterDiffCallback) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int) = getItem(position).id.hashCode().toLong() + 1000L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemFilterUserBinding.inflate(layoutInflater, parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(
            category,
            onChange
        )
    }


    class UserViewHolder(private val binding: ItemFilterUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            data: UserFilterUi,
            onChange: ((Int, Boolean) -> Unit)?,
        ) = binding.run {
            checkbox.isChecked = data.checked
            title.text = data.name
            count.text = data.count.toString()

            icon.transitionName = TransitionUtils.imageTransitionName(data.id.toString())
            icon.load(data.imageUrl)

            root.setOnClickListener {
                checkbox.isChecked = !checkbox.isChecked
            }

            checkbox.addOnCheckedStateChangedListener { _, state ->
                if (state == MaterialCheckBox.STATE_UNCHECKED) {
                    onChange?.invoke(data.id, false)
                } else if (state == MaterialCheckBox.STATE_CHECKED) {
                    onChange?.invoke(data.id, true)
                }
            }
        }
    }
}