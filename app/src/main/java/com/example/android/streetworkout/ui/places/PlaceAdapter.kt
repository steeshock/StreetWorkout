package com.example.android.streetworkout.ui.places

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.streetworkout.R
import com.example.android.streetworkout.data.model.PlaceObject
import com.squareup.picasso.Picasso

class PlaceAdapter(val callback: Callback) : RecyclerView.Adapter<PlaceAdapter.PlaceHolder>() {

    private var items = emptyList<PlaceObject>() // Cached copy of words

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = PlaceHolder(LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: PlaceHolder, position: Int) {
        holder.bind(items[position])
    }

    internal fun setPlaces(places: List<PlaceObject>) {
        this.items = places
        notifyDataSetChanged()
    }

    inner class PlaceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val placeDescription = itemView.findViewById<TextView>(R.id.place_description_text)
        private val placeImage = itemView.findViewById<ImageView>(R.id.place_image)

        fun bind(item: PlaceObject) {
            placeDescription.text = item.description

            Picasso
                .get()
                .load(item.imagePath)
                .placeholder(R.drawable.place)
                .error(R.drawable.place)
                .fit()
                .into(placeImage)

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }
        }
    }

    interface Callback {
        fun onItemClicked(item: PlaceObject)
    }
}