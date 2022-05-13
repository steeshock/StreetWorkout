package com.steeshock.streetworkout.design

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.steeshock.streetworkout.design.R.styleable.*
import com.steeshock.streetworkout.design.databinding.EmptyStateViewBinding

class CustomView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val binding = EmptyStateViewBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        val title: String
        val imageResource: Int

        context.theme.obtainStyledAttributes(attrs, CustomView, 0, 0).apply {
            try {
                title = getString(CustomView_title) ?: ""
                imageResource = getResourceId(CustomView_image, 0)
            } finally {
                recycle()
            }
        }

        binding.image.setImageResource(imageResource)
        binding.title.text = title
    }
}