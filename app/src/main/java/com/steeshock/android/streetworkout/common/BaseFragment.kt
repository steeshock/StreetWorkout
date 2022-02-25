package com.steeshock.android.streetworkout.common

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.steeshock.android.streetworkout.R
import com.steeshock.android.streetworkout.presentation.views.MainActivity

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
}