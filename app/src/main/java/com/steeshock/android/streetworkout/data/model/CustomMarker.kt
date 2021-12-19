package com.steeshock.android.streetworkout.data.model

import com.google.android.gms.maps.model.Marker

data class CustomMarker(
    var place_uuid: String,
    var mapMarker: Marker? = null,
)
