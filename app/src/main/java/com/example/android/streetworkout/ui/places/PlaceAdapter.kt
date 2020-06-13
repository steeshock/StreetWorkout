package com.example.android.streetworkout.ui.places

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.android.streetworkout.R

class PlaceAdapter(var items: List<PlaceObject>, val callback: Callback) : RecyclerView.Adapter<PlaceAdapter.PlaceHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = PlaceHolder(LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: PlaceHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class PlaceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val firstName = itemView.findViewById<TextView>(R.id.place_description_text)

        fun bind(item: PlaceObject) {
            firstName.text = item.simpleText
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback.onItemClicked(items[adapterPosition])
            }
        }
    }

    interface Callback {
        fun onItemClicked(item: PlaceObject)
    }
}