package com.gmail.maystruks08.nfcruntracker.core.ext

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

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

fun Context.color(@ColorRes resId: Int) = ContextCompat.getColor(this, resId)
fun Context.drawable(@DrawableRes resId: Int) = ContextCompat.getDrawable(this, resId)
fun Context.string(@StringRes resId: Int) = resources.getString(resId)


