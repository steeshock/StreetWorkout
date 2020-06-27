package com.example.android.streetworkout.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.streetworkout.R
import com.example.android.streetworkout.data.model.PlaceObject
import com.example.android.streetworkout.databinding.PlaceItemBinding
import com.squareup.picasso.Picasso

class PlaceAdapter(val callback: Callback) : RecyclerView.Adapter<PlaceAdapter.PlaceHolder>() {

    private var items = emptyList<PlaceObject>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PlaceHolder(PlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: PlaceHolder, position: Int) {
        holder.bind(items[position])
    }

    internal fun setPlaces(places: List<PlaceObject>) {
        this.items = places
        notifyDataSetChanged()
    }

    inner class PlaceHolder(private val binding: PlaceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.setItemClickListener {
                binding.place?.let { place ->
                    if (adapterPosition != RecyclerView.NO_POSITION) callback.onPlaceClicked(place)
                }
            }
            binding.setLikeClickListener {
                binding.place?.let { place ->
                    if (adapterPosition != RecyclerView.NO_POSITION) callback.onLikeClicked(place)
                }
            }
        }

        fun bind(item: PlaceObject) {
            binding.apply {
                place = item
                executePendingBindings()
            }
        }
    }

    interface Callback {
        fun onPlaceClicked(item: PlaceObject)
        fun onLikeClicked(item: PlaceObject)
    }
}