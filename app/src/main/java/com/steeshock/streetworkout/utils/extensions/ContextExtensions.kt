package com.steeshock.streetworkout.utils.extensions

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog

fun Activity.showAlertDialog(
    title: String? = null,
    message: String? = null,
    positiveText: String? = null,
    negativeText: String? = null,
    onPositiveAction: () -> Unit = {},
    onNegativeAction: () -> Unit = {},
) {
    return this.getAlertDialogBuilder(
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

fun Activity.getAlertDialogBuilder(
    title: String? = null,
    message: String? = null,
    positiveText: String? = null,
    negativeText: String? = null,
    onPositiveAction: () -> Unit = {},
    onNegativeAction: () -> Unit = {},
): AlertDialog.Builder {
    return AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveText) { _, _ -> onPositiveAction.invoke() }
        .setNegativeButton(negativeText) { _, _ -> onNegativeAction.invoke() }
}