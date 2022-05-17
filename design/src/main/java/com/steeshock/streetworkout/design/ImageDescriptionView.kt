package com.steeshock.streetworkout.design

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import com.steeshock.streetworkout.design.R.styleable.*
import com.steeshock.streetworkout.design.databinding.ImageDescritionViewBinding

class ImageDescriptionView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private val binding = ImageDescritionViewBinding.inflate(LayoutInflater.from(context), this, true)

    private var title: String = ""
    private var imageResource: Int = 0

    init {
        context.withStyledAttributes(attrs, ImageDescriptionView) {
            title = getString(ImageDescriptionView_title) ?: ""
            imageResource = getResourceId(ImageDescriptionView_image, 0)
        }

        binding.image.setImageResource(imageResource)
        binding.title.text = title
    }
}