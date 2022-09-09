package com.steeshock.streetworkout.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.steeshock.streetworkout.R
import com.steeshock.streetworkout.data.model.PlaceDto
import com.steeshock.streetworkout.databinding.PlaceItemBinding
import com.steeshock.streetworkout.extensions.toVisibility
import com.steeshock.streetworkout.presentation.adapters.PlacePayloadType.*

class PlaceAdapter(val callback: Callback) : RecyclerView.Adapter<PlaceAdapter.PlaceHolder>() {

    private var items = emptyList<PlaceDto>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PlaceHolder(PlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: PlaceHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onBindViewHolder(holder: PlaceHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val payloadList = payloads[0] as? MutableList<*>
            payloadList?.let {
                for (payload in it) {
                    when (payload) {
                        FAVORITE_PAYLOAD -> {
                            holder.bindFavoriteState(items[position])
                        }
                        ADDRESS_PAYLOAD -> {
                            holder.bindAddress(items[position])
                        }
                        PLACE_OWNER_PAYLOAD -> {
                            holder.bindPlaceOwnerState(items[position])
                        }
                        IMAGES_PAYLOAD,
                        TITLE_PAYLOAD,
                        -> {
                            holder.bindImageSlider(items[position])
                        }
                    }
                }
            }
        }
    }

    internal fun setPlaces(placeDtos: List<PlaceDto>) {
        val result = DiffUtil.calculateDiff(PlacesDiffUtilCallback(this.items, placeDtos), false)
        this.items = placeDtos
        result.dispatchUpdatesTo(this)
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
            binding.locationImageView.setOnClickListener {
                callback.onPlaceLocationClicked(items[layoutPosition])
            }
            binding.deletePlaceButton.setOnClickListener {
                callback.onPlaceDeleteClicked(items[layoutPosition])
            }
        }

        fun bind(item: PlaceDto) {
            binding.apply {
                bindAddress(item)
                bindImageSlider(item)
                bindFavoriteState(item)
                bindPlaceOwnerState(item)
            }
        }

        fun bindAddress(item: PlaceDto) {
            binding.placeAddressTextView.text = item.address
        }

        fun bindImageSlider(item: PlaceDto) {

            val imageList = ArrayList<SlideModel>()

            for ((index, image) in item.images!!.withIndex()) {

                if (index == 0) {
                    imageList.add(SlideModel(image, item.title, ScaleTypes.FIT))
                } else {
                    imageList.add(SlideModel(image, null, ScaleTypes.FIT))
                }
            }

            if (imageList.isEmpty()) {
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

        fun bindFavoriteState(item: PlaceDto) {
            if (item.isFavorite) {
                binding.likeImageView.setImageResource(R.drawable.ic_heart_red_36dp)
            } else {
                binding.likeImageView.setImageResource(R.drawable.ic_heart_gray_36dp)
            }
        }

        fun bindPlaceOwnerState(item: PlaceDto) {
            binding.deletePlaceButton.visibility = item.isUserPlaceOwner.toVisibility()
        }
    }

    interface Callback {
        fun onPlaceClicked(placeDto: PlaceDto)
        fun onLikeClicked(placeDto: PlaceDto)
        fun onPlaceLocationClicked(placeDto: PlaceDto)
        fun onPlaceDeleteClicked(placeDto: PlaceDto)
    }
}