package com.steeshock.android.streetworkout.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.databinding.PlaceItemBinding
import java.util.*

class PlaceAdapter(val callback: Callback) : RecyclerView.Adapter<PlaceAdapter.PlaceHolder>() {

    private var items = emptyList<Place>()
    private var allItems = emptyList<Place>()
    private var filteredItems = emptyList<Place>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PlaceHolder(PlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: PlaceHolder, position: Int) {
        holder.bind(items[position])
    }

    internal fun setPlaces(places: List<Place>) {
        items = places
        allItems = places.toList()
        filteredItems = places.toList()
        
        for(item in allItems) item.categories?.forEach { it.isSelected = true }
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

    fun filterItemsByFilterList(filterList: MutableList<Category>) {

        items = if (filterList.isNullOrEmpty())
            allItems
        else {
            allItems.filter { it.categories!!.containsAll(filterList)}
        }

        filteredItems = items

        notifyDataSetChanged()
    }

    fun filterItemsBySearchString(searchString: String?) {

        items = if (searchString.isNullOrEmpty())
            filteredItems
        else {
            filteredItems.filter { it.title.toLowerCase(Locale.ROOT).contains(searchString)}
        }

        notifyDataSetChanged()
    }

    interface Callback {
        fun onPlaceClicked(item: Place)
        fun onLikeClicked(item: Place)
    }
}