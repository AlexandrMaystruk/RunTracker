package com.gmail.maystruks08.nfcruntracker.core.ext

import android.view.View
import java.text.SimpleDateFormat
import java.util.*

const val DATE_FORMAT = "dd.MM.yyyy"

const val TIME_FORMAT = "HH:mm:ss"

const val DATA_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss"

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun Date.toTimeFormat(): String = SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(this)
fun Date.toDateFormat(): String = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(this)
fun Date.toDateTimeFormat(): String = SimpleDateFormat(DATA_TIME_FORMAT, Locale.getDefault()).format(this)


