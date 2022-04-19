package com.steeshock.streetworkout.common

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
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

    private fun getBottomBaseline(): View? {
        return (activity as? MainActivity)?.getBottomBaseline()
    }

    fun getTopBaseline(): View? {
        return (activity as? MainActivity)?.getTopBaseline()
    }

    fun showAlertDialog(
        title: String? = null,
        message: String? = null,
        positiveText: String? = null,
        negativeText: String? = null,
        onPositiveAction: () -> Unit = {},
        onNegativeAction: () -> Unit = {},
    ) {
        return getAlertDialogBuilder(
            title = title,
            message = message,
            positiveText = positiveText,
            negativeText = negativeText,
            onPositiveAction = onPositiveAction,
            onNegativeAction = onNegativeAction,
        )
            .create()
            .show()
    }

    fun getAlertDialogBuilder(
        title: String? = null,
        message: String? = null,
        positiveText: String? = null,
        negativeText: String? = null,
        onPositiveAction: () -> Unit = {},
        onNegativeAction: () -> Unit = {},
    ): AlertDialog.Builder {
        return AlertDialog.Builder(requireActivity())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText) { _, _ -> onPositiveAction.invoke() }
            .setNegativeButton(negativeText) { _, _ -> onNegativeAction.invoke() }
    }

    fun showSnackbar(
        message: String?,
        action: () -> Unit = {},
        actionText: String? = null,
        showOnTop: Boolean = false,
    ) {
        view?.let { view ->
            message?.let { message ->
                val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                snackbar.anchorView = if (showOnTop) getTopBaseline() else getBottomBaseline()
                snackbar.setAction(actionText) { action.invoke() }
                snackbar.show()
            }
        }
    }
}