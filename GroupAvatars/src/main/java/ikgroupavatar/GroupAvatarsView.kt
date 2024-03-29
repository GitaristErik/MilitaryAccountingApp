package ikgroupavatar

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class GroupAvatarsView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {

    private var mLayoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        set(value) {
            layoutManager = value
            field = value
        }
    private var limit: Int = 0
    private var remainingItems: Boolean = false
    private var avatarBorder: Int = 0
    private var borderColor: Int = Color.BLACK
    private var avatarSize: Int = 38
//        get() = (field * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
    private var remainColor: Int = Color.BLACK
    private var remainTextColor: Int = Color.LTGRAY
    private var remainTextSize: Float = 18f
    private var avatarPlaceHolder: Int = R.drawable.avatar_placeholder
    private var avatarMargin: Int = -16
//        get() = (field * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
    var dataSource: MutableList<Any> = mutableListOf()
        set(value) {
            field = value
            adapter = AvatarsAdapter(
                context,
                dataSource,
                limit,
                if (limit == 0) false else remainingItems,
                avatarBorder,
                borderColor,
                avatarSize,
                avatarMargin,
                remainColor,
                remainTextColor,
                remainTextSize,
                avatarPlaceHolder
            )
        }

    init {
        layoutManager = mLayoutManager

        val a = context.obtainStyledAttributes(attrs, R.styleable.GroupAvatarsView, 0, 0)

        limit = a.getInteger(R.styleable.GroupAvatarsView_item_limit, 0)
        remainingItems = a.getBoolean(R.styleable.GroupAvatarsView_display_remaining_items, false)
        avatarBorder = a.getDimensionPixelSize(R.styleable.GroupAvatarsView_avatar_border_size, 0)
        borderColor = a.getColor(R.styleable.GroupAvatarsView_avatar_border_color, Color.BLACK)
        avatarSize = a.getDimensionPixelSize(R.styleable.GroupAvatarsView_avatar_size, 38)
        avatarMargin = a.getInt(R.styleable.GroupAvatarsView_avatar_margin, -16)
        remainColor = a.getColor(R.styleable.GroupAvatarsView_remain_color, Color.LTGRAY)
        remainTextColor =
            a.getColor(R.styleable.GroupAvatarsView_remain_count_text_color, Color.BLACK)
        remainTextSize = a.getFloat(R.styleable.GroupAvatarsView_remain_count_text_size, 18f)
        avatarPlaceHolder = a.getResourceId(
            R.styleable.GroupAvatarsView_avatar_placeholder,
            R.drawable.avatar_placeholder
        )

        a.recycle()
    }
}