package com.example.militaryaccountingapp.presenter.shared.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.militaryaccountingapp.databinding.ItemCodeListBinding
import com.example.militaryaccountingapp.presenter.model.Barcode
import com.example.militaryaccountingapp.presenter.utils.common.ext.asFormattedDateString
import com.example.militaryaccountingapp.presenter.utils.ui.ext.initAsQrMini

class BarCodeAdapter(
    private val onClick: ((Barcode) -> Unit)?,
) : ListAdapter<Barcode, BarCodeAdapter.BarCodeViewHolder>(BarCodeDiffCallback) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int) = getItem(position).code.hashCode().toLong() + 1000L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarCodeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCodeListBinding.inflate(layoutInflater, parent, false)
        return BarCodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BarCodeViewHolder, position: Int) {
        holder.bind(
            getItem(position),
            onClick
        )
    }


    class BarCodeViewHolder(private val binding: ItemCodeListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            data: Barcode,
            onClick: ((Barcode) -> Unit)?,
        ) = binding.run {

            codeImage.initAsQrMini(data.code)

            date.text = data.timestamp.asFormattedDateString()

            content.text = data.code

            listOf(root, detailed).forEach {
                it.setOnClickListener {
                    onClick?.invoke(data)
                }
            }
        }
    }
}