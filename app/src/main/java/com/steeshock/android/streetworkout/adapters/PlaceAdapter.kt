package com.steeshock.android.streetworkout.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.steeshock.android.streetworkout.R
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

        notifyDataSetChanged()

        setupEmptyListState()
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

    fun filterItemsByCategory(filterList: MutableList<Category>) {

        if (allItems.isNotEmpty()) {

            items = if (filterList.isNullOrEmpty())
                allItems
            else {
                allItems.filter { it.categories!!.containsAll(filterList.map { i -> i.category_id })}
            }

            filteredItems = items

            notifyDataSetChanged()

            setupEmptyFilterResultsState()
        }
    }

    fun filterItemsBySearchString(searchString: String?) {

        items = if (searchString.isNullOrEmpty())
            filteredItems
        else {
            filteredItems.filter { it.title.toLowerCase(Locale.ROOT).contains(searchString)}
        }

        notifyDataSetChanged()

        setupEmptyFilterResultsState()

        if (searchString.isNullOrEmpty() && allItems.isEmpty())
            setupEmptyListState()
    }

    private fun setupEmptyListState() {
        callback.setEmptyListState(items.isEmpty())
    }

    private fun setupEmptyFilterResultsState() {
        callback.setEmptyResultsState(items.isEmpty())
    }

    interface Callback {
        fun onPlaceClicked(item: Place)
        fun onLikeClicked(item: Place)
        fun onPlaceLocationClicked(item: Place)

        fun setEmptyListState(isEmpty: Boolean)
        fun setEmptyResultsState(isEmpty: Boolean)
    }
}