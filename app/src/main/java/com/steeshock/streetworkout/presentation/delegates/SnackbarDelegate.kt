package com.steeshock.streetworkout.presentation.delegates

import android.view.View
import android.widget.FrameLayout
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.steeshock.streetworkout.R
import com.steeshock.streetworkout.presentation.delegates.SnackbarPosition.*
import com.steeshock.streetworkout.presentation.views.IBaseline

interface SnackbarDelegate {
    fun View?.showSnackbar(
        message: String?,
        action: () -> Unit = {},
        actionText: String? = null,
        position: SnackbarPosition = BOTTOM,
        isError: Boolean = false,
    )
}

class SnackbarDelegateImpl() : SnackbarDelegate {

    private lateinit var baselineDelegate: IBaseline

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
                    //setAnchorView(this, position)
                    if (isError) {
                        val layoutParams = this.view.layoutParams as FrameLayout.LayoutParams
                        layoutParams.setMargins(0, 0,0 ,0)
                        this.view.layoutParams = layoutParams
                        setBackgroundTint(resources.getColor(R.color.redColor, null))
                        setTextColor(resources.getColor(R.color.whiteColor, null))
                        animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
                        duration = 4000
                    }
                    show()
                }
            }
        }
    }

    private fun setAnchorView(snackbar: Snackbar, position: SnackbarPosition) {
        snackbar.anchorView = when(position) {
            TOP -> baselineDelegate.getTopBaseline()
            BOTTOM -> baselineDelegate.getBottomBaseline()
            BELOW_NAVBAR -> baselineDelegate.getBelowNavBarBaseline()
        }
    }
}

enum class SnackbarPosition {
    TOP,
    BOTTOM,
    BELOW_NAVBAR,
}