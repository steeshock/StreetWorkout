package com.steeshock.streetworkout.presentation.delegates

import android.annotation.SuppressLint
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.steeshock.streetworkout.R
import com.steeshock.streetworkout.presentation.delegates.SnackbarPosition.*
import com.steeshock.streetworkout.presentation.views.IMainActivity

interface SnackbarDelegate {

    fun registerSnackbarDelegate(baseline: IMainActivity)

    fun View?.showSnackbar(
        message: String?,
        action: () -> Unit = {},
        actionText: String? = null,
        position: SnackbarPosition = BOTTOM,
        isError: Boolean = false,
    )

    fun View?.showErrorSnackbar(
        message: String?,
        position: SnackbarPosition = BELOW_NAVBAR,
    )

    fun View?.showNoInternetSnackbar()
}

class SnackbarDelegateImpl : SnackbarDelegate {

    private lateinit var baseline: IMainActivity

    override fun registerSnackbarDelegate(baseline: IMainActivity) {
        this.baseline = baseline
    }

    override fun View?.showSnackbar(
        message: String?,
        action: () -> Unit,
        actionText: String?,
        position: SnackbarPosition,
        isError: Boolean,
    ) {
        this?.let { view ->
            message?.let { message ->
                Snackbar.make(view, message, Snackbar.LENGTH_LONG).apply {
                    setAction(actionText) { action.invoke() }
                    setAnchorView(this, position)
                    setErrorSettings(isError)
                    show()
                }
            }
        }
    }

    override fun View?.showErrorSnackbar(
        message: String?,
        position: SnackbarPosition,
    ) {
        showSnackbar(
            message = message,
            position = position,
            isError = true,
        )
    }

    override fun View?.showNoInternetSnackbar() {
        showErrorSnackbar(
            message = this?.resources?.getString(R.string.no_internet_snackbar_message)
        )
    }

    private fun setAnchorView(snackbar: Snackbar, position: SnackbarPosition) {
        snackbar.anchorView = when(position) {
            TOP -> baseline.getTopBaseline()
            BOTTOM -> baseline.getBottomBaseline()
            BELOW_NAVBAR -> baseline.getNavBarBaseline()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun Snackbar.setErrorSettings(isError: Boolean) {
        if (isError) {
            val layoutParams = this.view.layoutParams as FrameLayout.LayoutParams
            layoutParams.setMargins(0, 0,0 ,0)
            this.view.layoutParams = layoutParams
            this.view.background = this.view.resources.getDrawable(R.drawable.flat_rectangle, null)
            setBackgroundTint(this.view.resources.getColor(R.color.redColor, null))
            setTextColor(this.view.resources.getColor(R.color.whiteColor, null))
            animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
            duration = 4000
        }
    }
}

enum class SnackbarPosition {
    TOP,
    BOTTOM,
    BELOW_NAVBAR,
}