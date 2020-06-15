package com.example.android.streetworkout.utils

import android.widget.AdapterView
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.streetworkout.data.model.PlaceObject
import com.example.android.streetworkout.ui.places.PlaceAdapter
import com.squareup.picasso.Picasso

class CustomBindingAdapter {
//    @BindingAdapter("bind:imageUrl")
//    fun loadImage(imageView: ImageView, urlImage: String?) {
//        Picasso.get().with(imageView.context).load(urlImage).into(imageView)
//    }

//    @BindingAdapter(["bind:data", "bind:clickHandler"])
//    fun configureRecyclerView(
//        recyclerView: RecyclerView,
//        projects: LiveData<PlaceObject>,
//        listener: AdapterView.OnItemClickListener
//    ) {
//        val adapter = PlaceAdapter(projects, listener)
//        recyclerView.setLayoutManager(LinearLayoutManager(recyclerView.getContext()))
//        recyclerView.setAdapter(adapter)
//    }

//    @BindingAdapter(["bind:refreshState", "bind:onRefresh"])
//    fun configureSwipeRefreshLayout(
//        layout: SwipeRefreshLayout,
//        isLoading: Boolean,
//        listener: SwipeRefreshLayout.OnRefreshListener?
//    ) {
//        layout.setOnRefreshListener(listener)
//        layout.post({ layout.setRefreshing(isLoading) })
//    }
}