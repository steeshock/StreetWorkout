package com.steeshock.streetworkout.common

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginLeft
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE
import com.google.android.material.snackbar.Snackbar
import com.steeshock.streetworkout.R
import com.steeshock.streetworkout.presentation.views.MainActivity

abstract class BaseFragment : Fragment() {

    abstract fun injectComponent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        injectComponent()
        super.onAttach(context)
    }

    fun checkPermission(
        permission: String,
        onPermissionGranted: () -> Unit = {},
        onCustomRationale: ((startPermissionRequestCallback: () -> Unit) -> Unit)? = null,
        onCustomDenied: (() -> Unit)? = null,
        showSettingsButton: Boolean = false,
    ) = (activity as? MainActivity)?.checkPermission(permission,
        onPermissionGranted,
        onCustomRationale,
        onCustomDenied,
        showSettingsButton,
    )

    fun showNoInternetAlert() {
        showSnackbar(
            message = getString(R.string.no_internet_snackbar_message),
            isError = true,
        )
    }

    fun showSnackbar(
        message: String?,
        action: () -> Unit = {},
        actionText: String? = null,
        showOnTop: Boolean = false,
        isError: Boolean = false,
    ) {
        view?.let { view ->
            message?.let { message ->
                Snackbar.make(view, message, Snackbar.LENGTH_LONG).apply {
                    anchorView = if (showOnTop) getTopBaseline() else getBottomBaseline()
                    setAction(actionText) { action.invoke() }
                    if (isError) {
                        setBackgroundTint(resources.getColor(R.color.redColor, null))
                        animationMode = ANIMATION_MODE_SLIDE
                    }
                    show()
                }
            }
        }
    }

    private fun getBottomBaseline(): View? {
        return (activity as? MainActivity)?.getBottomBaseline()
    }

    private fun getTopBaseline(): View? {
        return (activity as? MainActivity)?.getTopBaseline()
    }
}