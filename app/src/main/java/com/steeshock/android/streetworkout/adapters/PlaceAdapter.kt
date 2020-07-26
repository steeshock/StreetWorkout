package com.steeshock.android.streetworkout.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.databinding.PlaceItemBinding

class PlaceAdapter(val callback: Callback) : RecyclerView.Adapter<PlaceAdapter.PlaceHolder>() {

    private var items = emptyList<Place>()
    private var tempItems = emptyList<Place>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PlaceHolder(PlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: PlaceHolder, position: Int) {
        holder.bind(items[position])
    }

    internal fun setPlaces(places: List<Place>) {
        this.items = places
        this.tempItems = places.toList()
        for(item in tempItems) item.categories?.forEach { it.isSelected = true }
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

        fun bind(item: Place) {
            binding.apply {
                place = item
                executePendingBindings()
            }
        }
    }

    fun filterItems(filterList: MutableList<Category>) {

        if (filterList.isNullOrEmpty())
            this.items = tempItems
        else {
            this.items = tempItems.filter { it.categories!!.containsAll(filterList)}
        }

        notifyDataSetChanged()
    }

    interface Callback {
        fun onPlaceClicked(item: Place)
        fun onLikeClicked(item: Place)
    }
}