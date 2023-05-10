package ikgroupavatar

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import ikgroupavatar.databinding.AvatarListItemBinding
import ikgroupavatar.databinding.RemainListItemBinding


class AvatarsAdapter(
    val context: Context,
    var avatars: List<Any>,
    var limit: Int,
    var remain: Boolean,
    var border: Int,
    var borderColor: Int,
    var avatarSize: Int,
    var avatarMargin: Int,
    var remainColor: Int,
    var remainTextColor: Int,
    var remainTextSize: Float,
    var avatarPlaceHolder: Int
) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (position < limit || limit <= 0)
            1
        else
            2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == 1) {
            val binding = AvatarListItemBinding.inflate(layoutInflater, parent, false)
            AvatarViewHolder(binding)
        } else {
            val binding = RemainListItemBinding.inflate(layoutInflater, parent, false)
            RemainViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is AvatarViewHolder) {
            holder.bind(position)
        } else if (holder is RemainViewHolder) {
            holder.bind()
        }
    }

    override fun getItemCount(): Int {
        return if (remain && limit > 0 && avatars.size > limit) {
            limit + 1
        } else {
            avatars.size
        }
    }

    inner class AvatarViewHolder(private val binding: AvatarListItemBinding) :
        ViewHolder(binding.root) {

        fun bind(position: Int) {
            val item = avatars[position]

            binding.run {
                logo.borderWidth = border
                logo.borderColor = borderColor
                logo.layoutParams = LinearLayout.LayoutParams(
                    avatarSize, avatarSize
                ).apply {
                    if (position > 0) setMargins(avatarMargin, 0, 0, 0)
                }

                if (item is String) Glide
                    .with(context)
                    .load(item)
                    .placeholder(avatarPlaceHolder)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
                    .into(logo)
            }
        }
    }

    inner class RemainViewHolder(private val binding: RemainListItemBinding) :
        ViewHolder(binding.root) {
        init {
            binding.run {
                remainView.layoutParams.height = avatarSize
                remainView.layoutParams.width = avatarSize
                val param = LinearLayout.LayoutParams(
                    remainView.layoutParams.width,
                    remainView.layoutParams.height
                )
                param.setMargins(avatarMargin, 0, 0, 0)
                remainView.layoutParams = param

                remainCircle.setColorFilter(remainColor)
                remainCount.textSize = remainTextSize
                remainCount.setTextColor(remainTextColor)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind() {
            binding.run {
                if (remain && avatars.size > limit) {
                    remainView.visibility = View.VISIBLE
                    remainCount.text = "${avatars.size - limit}+"
                } else {
                    remainView.visibility = View.GONE
                }
            }
        }
    }
}