package com.steeshock.streetworkout.design

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import com.steeshock.streetworkout.design.R.styleable.*
import com.steeshock.streetworkout.design.databinding.InputWithIconViewBinding
import com.steeshock.streetworkout.extensions.toVisibility

class InputWithIconView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val binding = InputWithIconViewBinding.inflate(LayoutInflater.from(context), this, true)

    @DrawableRes private var iconResourceId: Int = 0
    @StringRes private var hintResourceId: Int = 0

    val textInput get() = binding.textInput
    val imageButton get() = binding.imageButton
    val progressBar get() = binding.progressBar

    init {
        context.withStyledAttributes(attrs, InputWithIconView) {
            iconResourceId = getResourceId(InputWithIconView_icon, 0)
            hintResourceId = getResourceId(InputWithIconView_hint, 0)
        }
        binding.imageButton.setImageResource(iconResourceId)
        binding.textInputLayout.hint = context.getString(hintResourceId)
    }

    fun setOnButtonClickListener(action: () -> Unit) {
        binding.imageButton.setOnClickListener {
            action()
        }
    }

    fun clearInput() {
        binding.textInput.text?.clear()
    }
}