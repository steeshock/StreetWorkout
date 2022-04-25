package com.steeshock.streetworkout.services.permissions

import android.Manifest.permission.ACCESS_FINE_LOCATION
import com.steeshock.streetworkout.R

object PermissionsUtils {

    fun getPermissionExplanation(permission: String) = run {
        permissionExplanationDictionary[permission] ?: defaultPermissionExplanation
    }

    private val permissionExplanationDictionary: Map<String, PermissionExplanation> = mapOf(
        ACCESS_FINE_LOCATION to PermissionExplanation(
            R.string.permission_geolocation_rationale_explanation,
            R.string.permission_geolocation_denied_explanation
        )
    )

    private val defaultPermissionExplanation = PermissionExplanation(
        R.string.permission_default_explanation,
        R.string.permission_default_explanation
    )
}

data class PermissionExplanation(
    /**
     * Describes message to show user in an educational UI
     * to explain why your app requires this permission
     */
    val rationaleExplanation: Int,
    /**
     * Describes message to show user in an educational UI
     * that feature requires a permission that the user has denied
     */
    val deniedExplanation: Int,
)

