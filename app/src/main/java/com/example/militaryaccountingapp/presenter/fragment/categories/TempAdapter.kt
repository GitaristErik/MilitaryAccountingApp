package com.example.militaryaccountingapp.presenter.fragment.categories

import android.R
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TempAdapter(private val items: MutableList<String>) :
    RecyclerView.Adapter<TempAdapter.Companion.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val listItem: View = LayoutInflater.from(parent.context).inflate(
            R.layout.simple_list_item_2,
            parent,
            false,
        )
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(items: List<String>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    companion object {
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(item: String) {
                itemView.findViewById<TextView>(R.id.text1).text = item
            }
        }
    }
}