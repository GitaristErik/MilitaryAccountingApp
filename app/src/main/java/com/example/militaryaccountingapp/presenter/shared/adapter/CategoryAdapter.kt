package com.example.militaryaccountingapp.presenter.shared.adapter

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.RequestBuilder
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.ItemCategoryGridBinding
import com.example.militaryaccountingapp.databinding.ItemCategoryListBinding
import com.example.militaryaccountingapp.presenter.model.CategoryUi
import com.example.militaryaccountingapp.presenter.utils.common.constant.ViewType
import com.example.militaryaccountingapp.presenter.utils.image.GlideUtils
import com.example.militaryaccountingapp.presenter.utils.ui.TransitionUtils
import com.example.militaryaccountingapp.presenter.utils.ui.ext.initAsQrMini
import com.example.militaryaccountingapp.presenter.utils.ui.ext.load

class CategoryAdapter(
    private var viewType: ViewType,
    private val onClickListener: (CategoryUi, View) -> Unit,
) : ListAdapter<CategoryUi, RecyclerView.ViewHolder>(CategoryDiffCallback) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int) = getItem(position).id.hashCode().toLong() + 1000L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (this.viewType) {
            ViewType.GRID -> {
                val binding = ItemCategoryGridBinding.inflate(layoutInflater, parent, false)
                GridViewHolder(binding)
            }

            ViewType.LIST -> {
                val binding = ItemCategoryListBinding.inflate(layoutInflater, parent, false)
                ListViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val category = getItem(position)
        val loadingDrawable = GradientDrawable().apply {
            setColor(Color.parseColor(category.color))
            alpha = 125
        }
        val thumbnailImage = GlideUtils.thumbnailBuilder(
            holder.itemView.context,
            category.imageUrl
        )
        (holder as? CategoryViewHolder)?.bind(
            category,
            loadingDrawable,
            thumbnailImage,
            onClickListener
        )
        (holder as? GridViewHolder)?.updateSpacing(position)
    }

    fun updateViewType(viewType: ViewType) {
        if (this.viewType == viewType) return

        this.viewType = viewType
        notifyItemRangeChanged(0, itemCount)
    }

    class ListViewHolder(override val binding: ItemCategoryListBinding) :
        CategoryViewHolder(binding) {

        override fun bind(
            data: CategoryUi,
            loadingDrawable: Drawable,
            thumbnailImage: RequestBuilder<Drawable>,
            onCLickListener: (CategoryUi, View) -> Unit,
        ) = binding.run {

            categoryTitle.text = data.name
            allCount.text = data.allCount.toString()
            itemCount.text = data.itemsCount.toString()
            nestedCount.text = data.nestedCount.toString()

            categoryImage.transitionName = TransitionUtils.imageTransitionName(data.id.toString())
            categoryImage.load(data.imageUrl) {
                thumbnail = thumbnailImage
                imageOnLoadingDrawable = loadingDrawable
            }

            avatarGroup.dataSource = data.usersAvatars.toMutableList()

            data.qrCode?.let { qrImage.initAsQrMini(it) }

            root.setOnClickListener { onCLickListener.invoke(data, categoryImage) }
        }
    }

    class GridViewHolder(override val binding: ItemCategoryGridBinding) :
        CategoryViewHolder(binding) {

        override fun bind(
            data: CategoryUi,
            loadingDrawable: Drawable,
            thumbnailImage: RequestBuilder<Drawable>,
            onCLickListener: (CategoryUi, View) -> Unit
        ) = Unit

        fun updateSpacing(position: Int) {
            val spacing16 = binding.root.resources.getDimension(R.dimen.margin_standard).toInt()
            val spacing4 = binding.root.resources.getDimension(R.dimen.margin_small_extra).toInt()
            binding.root.updateLayoutParams<MarginLayoutParams> {
                if (position % 2 != 0) {
                    marginEnd = spacing16
                    marginStart = spacing4
                } else {
                    marginStart = spacing16
                    marginEnd = spacing4
                }
            }
        }
    }

    abstract class CategoryViewHolder(protected open val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        abstract fun bind(
            data: CategoryUi,
            loadingDrawable: Drawable,
            thumbnailImage: RequestBuilder<Drawable>,
            onCLickListener: (CategoryUi, View) -> Unit,
        )
    }
}