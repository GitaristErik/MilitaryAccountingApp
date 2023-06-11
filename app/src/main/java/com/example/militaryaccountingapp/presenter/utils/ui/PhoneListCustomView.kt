package com.example.militaryaccountingapp.presenter.utils.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.updateMargins
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.ItemPhoneAddBinding
import com.example.militaryaccountingapp.domain.helper.Result
import com.example.militaryaccountingapp.presenter.utils.ui.ext.renderValidate
import com.google.android.material.textfield.TextInputEditText

class PhoneListCustomView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var binding: ItemPhoneAddBinding? = null

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.item_phone_add, this, true)
        binding = ItemPhoneAddBinding.bind(this)
//        setupPhone()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupPhone()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        binding = null
    }

    fun setPhones(phones: List<String>) {
        // change text of existing views and remove another views if phones.size < childCount
        foreachPhoneView { view, i ->
            if (i < phones.size) {
                view.setText(phones[i])
            } else {
                removeView(view)
            }
        }
        // if phones.size > childCount, add new views
        for (i in size until phones.size) {
            makePhoneEditText(phones[i]) {
                removeView(it)
            }.also {
                addView(it)
            }
        }
    }

    fun getPhones(): List<String> {
        val phones = mutableListOf<String>()
        foreachPhoneView { view, _ ->
            phones.add(view.text.toString())
        }
        return phones
    }

    fun renderValidate(resultList: List<Result<String>>) {
        foreachPhoneView { view, i ->
            view.renderValidate(resultList[i])
        }
    }

    private fun setupPhone() {
        binding?.run {
            addPhone.setAutofillHints(View.AUTOFILL_HINT_PHONE)
            addPhoneLayout.setAutofillHints(View.AUTOFILL_HINT_PHONE)

            addPhoneLayout.setEndIconOnClickListener {
                if (addPhone.text.isNullOrEmpty()) return@setEndIconOnClickListener
                makePhoneEditText(addPhone.text.toString()) {
                    removeView(it)
                }.also {
                    addView(it)
                    addPhone.setText("")
                }
            }
        }
    }

    private fun foreachPhoneView(action: (TextInputEditText, Int) -> Unit) {
        for (i in 1 until childCount) {
            val view = getChildAt(i)
            if (view is TextInputEditText && view.id != R.id.add_phone) {
                action(view, i-1)
            }
        }
    }

    private val size: Int get() = childCount - 1

    private fun makePhoneEditText(
        phone: String,
        onRemoveListener: OnClickListener
    ): TextInputEditText =
        TextInputEditText(context).apply {
            val id = generateId()
            setId(id)
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                val margin = resources.getDimension(R.dimen.margin_small_extra).toInt()
                updateMargins(top = margin, bottom = margin)
            }
            isElegantTextHeight = true
            hint = context.getString(R.string.profile_phone_hint)
            inputType = android.text.InputType.TYPE_CLASS_PHONE
            setText(phone)
            setAutofillHints(View.AUTOFILL_HINT_PHONE)

            // set drawable and listener
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                0, 0, R.drawable.ic_remove_24dp, 0
            )
            compoundDrawablePadding = resources.getDimension(R.dimen.padding_small).toInt()
            setOnTouchListener { v, event ->
                if (event.action == android.view.MotionEvent.ACTION_UP) {
                    if (event.rawX >= (right - compoundDrawables[2].bounds.width())) {
                        onRemoveListener.onClick(this)
                        return@setOnTouchListener true
                    } else {
                        v?.performClick()
                    }
                }
                return@setOnTouchListener false
            }
        }

    companion object {
        @JvmStatic
        private fun generateId(): Int {
            // Get the current time in milliseconds.
            val time = System.currentTimeMillis()

            // Get the current thread ID.
            val threadId = Thread.currentThread().id

            // Return a unique ID that is based on the current time and thread ID.
            return (time * 10000 + threadId).toInt()
        }
    }
}