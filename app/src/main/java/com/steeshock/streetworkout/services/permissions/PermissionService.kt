package com.steeshock.streetworkout.services.permissions

import android.content.Context
import android.content.pm.PackageManager.*
import androidx.core.content.ContextCompat
import javax.inject.Inject

class PermissionService @Inject constructor(private val context: Context) {

    fun checkPermission(
        permission: String,
        onPermissionGranted: () -> Unit = {},
        onPermissionDenied: () -> Unit = {},
    ) {
        val permissionState = ContextCompat.checkSelfPermission(
            context,
            permission,
        )

        when(permissionState) {
            PERMISSION_GRANTED -> onPermissionGranted.invoke()
            else -> onPermissionDenied.invoke()
        }
    }
}