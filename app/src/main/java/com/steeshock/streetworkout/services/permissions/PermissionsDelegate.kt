package com.steeshock.streetworkout.services.permissions

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.FragmentActivity
import com.steeshock.streetworkout.R
import com.steeshock.streetworkout.services.permissions.PermissionsUtils.getPermissionExplanation
import com.steeshock.streetworkout.utils.extensions.showAlertDialog

/**
 * Delegate interface for handling permission requests
 */
interface PermissionsDelegate {

    fun registerPermissionDelegate(activity: Activity)

    fun checkPermission(
        permission: String,
        onPermissionGranted: () -> Unit = {},
        onCustomRationale: ((startPermissionRequestCallback: () -> Unit) -> Unit)? = null,
        onCustomDenied: (() -> Unit)? = null,
        showSettingsButton: Boolean = false,
    )
}

class PermissionsDelegateImpl : PermissionsDelegate {

    private lateinit var activity: Activity

    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null

    private var onPermissionGranted: (() -> Unit)? = null

    private var onCustomDenied: (() -> Unit)? = null

    private var showSettingsButton: Boolean = false

    private lateinit var permissionExplanation: PermissionExplanation

    override fun registerPermissionDelegate(activity: Activity) {
        this.activity = activity
        requestPermissionLauncher =
            (this.activity as? FragmentActivity)?.registerForActivityResult(RequestPermission()
            ) { isGranted: Boolean ->
                when {
                    isGranted -> onPermissionGranted?.invoke()
                    else -> showDeniedPermissionDialog(showSettingsButton, onCustomDenied)
                }
            }
    }

    override fun checkPermission(
        permission: String,
        onPermissionGranted: () -> Unit,
        onCustomRationale: ((() -> Unit) -> Unit)?,
        onCustomDenied: (() -> Unit)?,
        showSettingsButton: Boolean,
    ) {
        this.onPermissionGranted = onPermissionGranted
        this.onCustomDenied = onCustomDenied
        this.permissionExplanation = getPermissionExplanation(permission)
        this.showSettingsButton = showSettingsButton

        when {
            checkSelfPermission(activity, permission) == PERMISSION_GRANTED -> {
                onPermissionGranted.invoke()
            }
            shouldShowRequestPermissionRationale(activity, permission) -> {
                showRationaleDialog(permission, onCustomRationale)
            }
            else -> {
                startPermissionRequest(permission)
            }
        }
    }

    private fun showRationaleDialog(
        permission: String,
        onCustomRationale: ((startPermissionRequestCallback: () -> Unit) -> Unit)?,
    ) {
        when (onCustomRationale) {
            null -> {
                showDefaultRationaleDialog(permission)
            }
            else -> {
                onCustomRationale.invoke { startPermissionRequest(permission) }
            }
        }
    }

    private fun showDeniedPermissionDialog(
        showSettingsButton: Boolean,
        onCustomDenied: (() -> Unit)?,
    ) {
        when (onCustomDenied) {
            null -> {
                showDefaultDeniedPermissionDialog(showSettingsButton)
            }
            else -> {
                onCustomDenied.invoke()
            }
        }
    }

    private fun startPermissionRequest(permission: String) {
        requestPermissionLauncher?.launch(permission)
    }

    /**
     * In an educational UI, explain to the user why your app requires this
     * permission for a specific feature to behave as expected. In this UI,
     * include a "cancel" or "no thanks" button that allows the user to
     * continue using your app without granting the permission.
     */
    private fun showDefaultRationaleDialog(permission: String) {

        activity.showAlertDialog(
            title = activity.getString(R.string.permission_rationale_default_title),
            message = activity.getString(permissionExplanation.rationaleExplanation),
            positiveText = activity.getString(R.string.ok_item),
            negativeText = activity.getString(R.string.thanks_item),
            onPositiveAction = { startPermissionRequest(permission) },
        )
    }

    /**
     * Explain to the user that the feature is unavailable because the
     * features requires a permission that the user has denied. At the
     * same time, respect the user's decision. Don't link to system
     * settings in an effort to convince the user to change their decision.
     */
    private fun showDefaultDeniedPermissionDialog(showSettingsButton: Boolean) {
        activity.showAlertDialog(
            title = activity.getString(R.string.permission_rationale_default_title),
            message = activity.getString(permissionExplanation.deniedExplanation),
            positiveText = activity.getString(R.string.clear_item),
            negativeText = if (showSettingsButton) activity.getString(R.string.settings_item) else null,
            onNegativeAction = { openSystemSettings() }
        )
    }

    private fun openSystemSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", activity.packageName, null)
            intent.data = uri
            activity.startActivity(intent)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

