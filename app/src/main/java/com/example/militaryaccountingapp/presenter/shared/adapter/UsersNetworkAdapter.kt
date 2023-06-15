package com.example.militaryaccountingapp.presenter.shared.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.militaryaccountingapp.databinding.ItemNetworkUserBinding
import com.example.militaryaccountingapp.presenter.model.UserNetworkUi
import com.example.militaryaccountingapp.presenter.utils.ui.TransitionUtils
import com.example.militaryaccountingapp.presenter.utils.ui.ext.load

class UsersNetworkAdapter(
    private val onClick: ((UserNetworkUi) -> Unit)?,
) : ListAdapter<UserNetworkUi, UsersNetworkAdapter.UserViewHolder>(UsersNetworkDiffCallback) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int) = getItem(position).id.hashCode().toLong() + 1000L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemNetworkUserBinding.inflate(layoutInflater, parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(
            user,
            onClick
        )
    }


    class UserViewHolder(private val binding: ItemNetworkUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            data: UserNetworkUi,
            onClick: ((UserNetworkUi) -> Unit)?
        ) = binding.run {
            title.text = data.rank
            name.text = data.fullName

            read.text = data.readCount.toString()
            readShare.text = data.readShareCount.toString()
            edit.text = data.editCount.toString()
            editShare.text = data.editShareCount.toString()

            icon.transitionName = TransitionUtils.imageTransitionName(data.id.toString())
            data.imageUrl?.let { icon.load(it) }

            root.setOnClickListener {
                onClick?.invoke(data)
            }
        }
    }
}