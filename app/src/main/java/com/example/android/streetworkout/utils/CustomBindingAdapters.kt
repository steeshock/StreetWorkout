package com.example.android.streetworkout.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.android.streetworkout.R
import com.squareup.picasso.Picasso

@BindingAdapter("bind:imageUrl")
fun loadImageFromUrl(imageView: ImageView, urlImage: String?) {
    if(!urlImage.isNullOrEmpty()){
            Picasso
                .get()
                .load(urlImage)
                .placeholder(R.drawable.place)
                .error(R.drawable.place)
                .fit()
                .into(imageView)
    }
}
