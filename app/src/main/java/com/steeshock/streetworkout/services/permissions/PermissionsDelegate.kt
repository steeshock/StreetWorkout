package com.steeshock.streetworkout.services.permissions

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.steeshock.streetworkout.R
import com.steeshock.streetworkout.utils.extensions.showAlertDialog

/**
 * Delegate interface for handling permission requests
 */
interface PermissionsDelegate {

    fun registerPermissionDelegate(activity: Activity)

    fun checkPermission(
        permission: String,
        onPermissionGranted: () -> Unit = {},
        showDefaultRationaleDialog: Boolean = true,
        onCustomRationale: (() -> Unit) -> Unit = {},
    )
}

class PermissionsDelegateImpl : PermissionsDelegate {

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    private lateinit var activity: Activity

    override fun registerPermissionDelegate(activity: Activity) {
        this.activity = activity
    }

    override fun checkPermission(
        permission: String,
        onPermissionGranted: () -> Unit,
        showDefaultRationaleDialog: Boolean,
        onCustomRationale: (() -> Unit) -> Unit
    ) {
        val permissionState = ContextCompat.checkSelfPermission(
            activity,
            permission,
        )

        when(permissionState) {
            PackageManager.PERMISSION_GRANTED -> onPermissionGranted.invoke()
            else -> requestRationale(permission, showDefaultRationaleDialog, onCustomRationale)
        }
    }

    private fun requestRationale(
        permission: String,
        showDefaultRationaleDialog: Boolean,
        onCustomRationale: (() -> Unit) -> Unit
    ) {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            permission
        )

        when(showDefaultRationaleDialog) {
            true -> showDefaultRationaleDialog(shouldProvideRationale, permission)
            else -> onCustomRationale.invoke { startPermissionRequest(activity, permission) }
        }
    }

    private fun showDefaultRationaleDialog(shouldProvideRationale: Boolean, permission: String) {
        activity.showAlertDialog(
            title = activity.getString(R.string.permission_rationale),
            message = activity.getString(R.string.permission_denied_explanation),
            positiveText = activity.getString(R.string.ok_item),
            negativeText = if (!shouldProvideRationale) activity.getString(R.string.settings_item) else null,
            onPositiveAction = { startPermissionRequest(activity, permission) },
            onNegativeAction = { openSettings() }
        )
    }

    private fun startPermissionRequest(activity: Activity, permission: String) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(permission),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun openSettings() {
        var kek = 8
    }
}