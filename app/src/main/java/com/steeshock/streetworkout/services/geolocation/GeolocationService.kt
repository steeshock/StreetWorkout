package com.steeshock.streetworkout.services.geolocation

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.lang.Exception
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GeolocationService @Inject constructor(private val context: Context) {

    private var fusedLocationClient: FusedLocationProviderClient? = null

    init {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    suspend fun getLastLocation(): Location? =
        suspendCoroutine { continuation ->
            fusedLocationClient?.lastLocation
                ?.addOnSuccessListener {
                    if (!Geocoder.isPresent()) {
                        continuation.resumeWithException(Exception("No geocoder available"))
                        return@addOnSuccessListener
                    }
                    continuation.resume(it)
                }
                ?.addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
}