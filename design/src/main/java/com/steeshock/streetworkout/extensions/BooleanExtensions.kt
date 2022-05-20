package com.steeshock.streetworkout.extensions

import android.view.View

fun Boolean.toVisibility(defaultVisibility: Int = View.GONE): Int {
    return if (this) View.VISIBLE else defaultVisibility
}
fun Boolean.toInvertedVisibility(defaultVisibility: Int = View.VISIBLE): Int {
    return if (this) View.GONE else defaultVisibility
}