package com.steeshock.streetworkout.common

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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

    fun getBottomBaseline(): View? {
        return (activity as? MainActivity)?.getBottomBaseline()
    }

    fun getTopBaseline(): View? {
        return (activity as? MainActivity)?.getTopBaseline()
    }

    fun showModalDialog(
        title: String? = null,
        message: String? = null,
        positiveText: String,
        negativeText: String,
        onPositiveAction: () -> Unit = {},
        onNegativeAction: () -> Unit = {},
    ) {
        return AlertDialog.Builder(requireActivity())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText) { _, _ -> onPositiveAction.invoke()}
            .setNegativeButton(negativeText) {_, _ -> onNegativeAction.invoke() }
            .create()
            .show()
    }

    fun showSnackbar(
        message: String,
        action: () -> Unit = {},
        actionText: String? = null,
    ) {
        view?.let {
            val snackbar = Snackbar.make(it, message, Snackbar.LENGTH_LONG)
            snackbar.anchorView = getBottomBaseline()
            snackbar.setAction(actionText) { action.invoke() }
            snackbar.show()
        }
    }
}