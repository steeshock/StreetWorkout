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
            binding.setItemClickListener {
                binding.place?.let { place ->
                    if (absoluteAdapterPosition != RecyclerView.NO_POSITION) callback.onPlaceClicked(place)
                }
            }
            binding.setLikeClickListener {
                binding.place?.let { place ->
                    if (absoluteAdapterPosition != RecyclerView.NO_POSITION) callback.onLikeClicked(place)
                }
            }

            binding.setPlaceLocationClickListener {
                binding.place?.let { place ->
                    if (absoluteAdapterPosition != RecyclerView.NO_POSITION) callback.onPlaceLocationClicked(place)
                }
            }
        }

        fun bind(item: Place) {
            binding.apply {
                place = item
                setupImagesSlider(this)
                executePendingBindings()
            }
        }
    }

    private fun setupImagesSlider(binding: PlaceItemBinding) {

        val imageList = ArrayList<SlideModel>()

        for ((index, image) in binding.place?.images!!.withIndex()){

            if (index == 0){
                imageList.add(SlideModel(image, binding.place?.title, ScaleTypes.FIT))
            }
            else{
                imageList.add(SlideModel(image, null, ScaleTypes.FIT))
            }
        }

        if (imageList.isEmpty()){
            imageList.add(SlideModel(R.drawable.place_mock, binding.place?.title, ScaleTypes.FIT))
        }

        binding.imageSlider.setImageList(imageList)
        binding.imageSlider.stopSliding()
        binding.imageSlider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {

                // ToDo Здесь можно заложить логику на нажатие на конкретное изображение
                binding.place?.let { place ->
                    callback.onPlaceClicked(place)
                }
            }
        })
    }

    interface Callback {
        fun onPlaceClicked(place: Place)
        fun onLikeClicked(place: Place)
        fun onPlaceLocationClicked(place: Place)
    }
}