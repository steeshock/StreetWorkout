package com.steeshock.streetworkout.design

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import com.steeshock.streetworkout.design.R.styleable.*
import com.steeshock.streetworkout.design.databinding.InputWithIconViewBinding
import com.steeshock.streetworkout.extensions.toVisibility

class InputWithIconView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val binding = InputWithIconViewBinding.inflate(LayoutInflater.from(context), this, true)

    private var imageResource: Int = 0
    private var isProgressVisible: Boolean = false

    init {
        context.withStyledAttributes(attrs, InputWithIconView) {
            imageResource = getResourceId(InputWithIconView_image, 0)
            isProgressVisible = getBoolean(InputWithIconView_isProgressVisible, false)
        }
        binding.imageButton.setImageResource(imageResource)
        binding.progressBar.visibility = isProgressVisible.toVisibility()
    }
}