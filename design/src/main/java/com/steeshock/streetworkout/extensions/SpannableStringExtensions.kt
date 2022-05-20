package com.steeshock.streetworkout.extensions

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.view.View

/**
 * Selects substring of the source string as a clickable link
 * @param [link] clickable substring in source string
 * @param [onClick] callback, which invoked on click
 */
fun SpannableString.setLinkSpan(
    link: String,
    onClick: () -> Unit,
) {
    val linkIndex = this.indexOf(link)
    if (linkIndex >= 0) {
        setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onClick()
                }
            },
            linkIndex,
            linkIndex + link.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}