package com.steeshock.android.streetworkout.presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import com.steeshock.android.streetworkout.data.model.Place

class PlacesDiffUtilCallback(
    private val oldList: List<Place>,
    private val newList: List<Place>,
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].place_uuid == newList[newItemPosition].place_uuid
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]== newList[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return if (oldList[oldItemPosition].isFavorite != newList[newItemPosition].isFavorite) true else null
    }
}