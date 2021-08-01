package com.steeshock.android.streetworkout.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.steeshock.android.streetworkout.R
import com.squareup.picasso.Picasso

@BindingAdapter("bind:imageUrl")
fun loadImageFromUrl(imageView: ImageView, urlImage: String?) {
    if (!urlImage.isNullOrEmpty()) {
        Picasso
            .get()
            .load(urlImage)
            .placeholder(R.drawable.place_mock)
            .error(R.drawable.place_mock)
            .fit()
            .into(imageView)
    }
}
//
//@BindingAdapter(*["bind:refreshState", "bind:onRefresh"])
//fun configureSwipeRefreshLayout(
//    layout: SwipeRefreshLayout,
//    isLoading: Boolean,
//    listener: SwipeRefreshLayout.OnRefreshListener?
//) {
//    layout.setOnRefreshListener(listener)
//    layout.post { layout.isRefreshing = isLoading }
//}
//
//@BindingAdapter("bind:refreshState", "bind:onRefresh")
//fun configureSwipeRefreshLayout(layout: SwipeRefreshLayout?, isLoading: Boolean, listener: SwipeRefreshLayout.OnRefreshListener?) {
//    layout?.setOnRefreshListener(listener)
//    layout?.post { layout.isRefreshing = isLoading }
//}