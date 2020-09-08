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

fun DisplayMetrics.toPx (dp: Float): Float = (dp*(this.densityDpi/ DisplayMetrics.DENSITY_DEFAULT))


