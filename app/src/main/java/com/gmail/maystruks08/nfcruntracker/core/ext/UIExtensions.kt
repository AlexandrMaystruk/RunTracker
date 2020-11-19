package com.gmail.maystruks08.nfcruntracker.core.ext

import android.util.DisplayMetrics
import android.view.View

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}


fun View.setVisibility(isVisible: Boolean) {
    this.visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun DisplayMetrics.toPx(dp: Float): Float =
    (dp * (this.densityDpi / DisplayMetrics.DENSITY_DEFAULT))


