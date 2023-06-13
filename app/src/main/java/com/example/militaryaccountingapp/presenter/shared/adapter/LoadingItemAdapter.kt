package com.example.militaryaccountingapp.presenter.shared.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.militaryaccountingapp.databinding.ItemLoadingAdapterBinding
import com.example.militaryaccountingapp.presenter.shared.adapter.LoadingItemAdapter.ViewHolder

class LoadingItemAdapter(private val loadingMessage: String) : RecyclerView.Adapter<ViewHolder>() {
    private var isLoading = false

    init {
        setHasStableIds(true)
    }

    override fun getItemCount() = if (isLoading) 1 else 0
    override fun getItemId(position: Int) = loadingMessage.hashCode().toLong() + 10L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLoadingAdapterBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(loadingMessage)
    }

    fun startLoading() {
        if (isLoading) return
        isLoading = true
        notifyItemInserted(0)
    }

    fun stopLoading() {
        if (!isLoading) return
        isLoading = false
        notifyItemRemoved(0)
    }

    class ViewHolder(private val binding: ItemLoadingAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(loadingMessage: String) {
            binding.loadingText.text = loadingMessage
        }
    }
}