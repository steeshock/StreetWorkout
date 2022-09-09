package com.steeshock.streetworkout.presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import com.steeshock.streetworkout.data.model.PlaceDto
import com.steeshock.streetworkout.presentation.adapters.PlacePayloadType.*

class PlacesDiffUtilCallback(
    private val oldList: List<PlaceDto>,
    private val newList: List<PlaceDto>,
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].placeId == newList[newItemPosition].placeId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]== newList[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any {
        val payloads: MutableList<PlacePayloadType> = mutableListOf()
        if (oldList[oldItemPosition].isFavorite != newList[newItemPosition].isFavorite) {
            payloads.add(FAVORITE_PAYLOAD)
        }
        if (oldList[oldItemPosition].images?.size != newList[newItemPosition].images?.size) {
            payloads.add(IMAGES_PAYLOAD)
        }
        if (oldList[oldItemPosition].title != newList[newItemPosition].title) {
            payloads.add(TITLE_PAYLOAD)
        }
        if (oldList[oldItemPosition].address != newList[newItemPosition].address) {
            payloads.add(ADDRESS_PAYLOAD)
        }
        if (oldList[oldItemPosition].isUserPlaceOwner != newList[newItemPosition].isUserPlaceOwner) {
            payloads.add(PLACE_OWNER_PAYLOAD)
        }
        return payloads
    }
}