package com.steeshock.android.streetworkout.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.steeshock.android.streetworkout.R
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.databinding.PlaceItemBinding
import java.util.*

class PlaceAdapter(val callback: Callback) : RecyclerView.Adapter<PlaceAdapter.PlaceHolder>() {

    private var items = emptyList<Place>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PlaceHolder(PlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: PlaceHolder, position: Int) {
        holder.bind(items[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    internal fun setPlaces(places: List<Place>) {
        this.items = places
        notifyDataSetChanged()
    }

    inner class PlaceHolder(private val binding: PlaceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.cardView.setOnClickListener {
                callback.onPlaceClicked(items[layoutPosition])
            }
            binding.likeImageView.setOnClickListener {
                callback.onLikeClicked(items[layoutPosition])
            }
            binding.likeImageView.setOnClickListener {
                callback.onLikeClicked(items[layoutPosition])
            }
        }

        fun bind(item: Place) {
            binding.apply {
                placeAddressTextView.text = item.address
                setupImagesSlider(this,item)
                setupLikeImage(this,item)
            }
        }
    }

    private fun setupImagesSlider(binding: PlaceItemBinding, item: Place) {

        val imageList = ArrayList<SlideModel>()

        for ((index, image) in item.images!!.withIndex()){

            if (index == 0){
                imageList.add(SlideModel(image, item.title, ScaleTypes.FIT))
            }
            else{
                imageList.add(SlideModel(image, null, ScaleTypes.FIT))
            }
        }

        if (imageList.isEmpty()){
            imageList.add(SlideModel(R.drawable.place_mock, item.title, ScaleTypes.FIT))
        }

        binding.imageSlider.setImageList(imageList)
        binding.imageSlider.stopSliding()
        binding.imageSlider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {

                // ToDo Здесь можно заложить логику на нажатие на конкретное изображение
                item.let { place ->
                    callback.onPlaceClicked(place)
                }
            }
        })
    }

    private fun setupLikeImage(binding: PlaceItemBinding, item: Place) {
        if (item.isFavorite == true) {
            binding.likeImageView.setImageResource(R.drawable.ic_heart_red_36dp)
        } else {
            binding.likeImageView.setImageResource(R.drawable.ic_heart_gray_36dp)
        }
    }

    interface Callback {
        fun onPlaceClicked(place: Place)
        fun onLikeClicked(place: Place)
        fun onPlaceLocationClicked(place: Place)
    }
}