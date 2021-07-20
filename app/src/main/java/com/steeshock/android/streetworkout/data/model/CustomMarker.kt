package com.steeshock.android.streetworkout.data.model

import com.google.android.gms.maps.model.Marker

data class CustomMarker(
    var place_id: Int? = null,
    var mapMarker: Marker? = null
)
