package com.example.militaryaccountingapp.presenter.shared.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.militaryaccountingapp.databinding.ItemSearchUserBinding
import com.example.militaryaccountingapp.presenter.model.UserSearchUi
import com.example.militaryaccountingapp.presenter.shared.adapter.UsersSearchAdapter.UserViewHolder
import com.example.militaryaccountingapp.presenter.utils.ui.TransitionUtils
import com.example.militaryaccountingapp.presenter.utils.ui.ext.load

class UsersSearchAdapter(
    private val onClick: ((UserSearchUi) -> Unit)?,
) : ListAdapter<UserSearchUi, UserViewHolder>(UsersSearchDiffCallback) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int) = getItem(position).id.hashCode().toLong() + 1000L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchUserBinding.inflate(layoutInflater, parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(
            user,
            onClick
        )
    }


    class UserViewHolder(private val binding: ItemSearchUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            data: UserSearchUi,
            onClick: ((UserSearchUi) -> Unit)?
        ) = binding.run {
            rank.text = data.rank
            name.text = data.rank
            fullName.text = data.fullName

            icon.transitionName = TransitionUtils.imageTransitionName(data.id)
            data.imageUrl?.let { if (it.isNotEmpty()) icon.load(it) }

            root.setOnClickListener {
                onClick?.invoke(data)
            }
        }
    }
}