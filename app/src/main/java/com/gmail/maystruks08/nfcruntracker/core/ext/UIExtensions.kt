package com.gmail.maystruks08.nfcruntracker.core.ext

import android.view.View
import java.text.SimpleDateFormat
import java.util.*

const val DATE_FORMAT = "dd.MM.yyyy"

const val TIME_FORMAT = "HH:mm:ss"

fun View.visible() {
    if (this.visibility == View.GONE || this.visibility == View.INVISIBLE) {
        this.visibility = View.VISIBLE
    }
}

fun View.gone() {
    if (this.visibility == View.VISIBLE || this.visibility == View.INVISIBLE) {
        this.visibility = View.GONE
    }
}

fun Date.toTimeFormat() = SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(this)
fun Date.toDateFormat() = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(this)

