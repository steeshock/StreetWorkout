package com.steeshock.streetworkout.common

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.steeshock.streetworkout.extensions.toVisibility
import com.steeshock.streetworkout.presentation.delegates.SnackbarDelegate
import com.steeshock.streetworkout.presentation.delegates.SnackbarDelegateImpl
import com.steeshock.streetworkout.presentation.views.IMainActivity
import com.steeshock.streetworkout.presentation.views.MainActivity


abstract class BaseFragment : Fragment(), SnackbarDelegate by SnackbarDelegateImpl() {

    abstract fun injectComponent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        registerSnackbarDelegate(activity as IMainActivity)
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
    ) = (activity as? MainActivity)?.checkPermission(
        permission,
        onPermissionGranted,
        onCustomRationale,
        onCustomDenied,
        showSettingsButton,
    )

    fun setFullscreenLoader(isVisible: Boolean) {
        (activity as? IMainActivity)?.getFullscreenLoader()?.visibility = isVisible.toVisibility()
    }
}