package com.gmail.maystruks08.domain

import java.text.SimpleDateFormat
import java.util.*


fun String.isolateSpecialSymbolsForRegex(): String =
    this.replace("*", "\\*")
        .replace("(", "\\(")
        .replace(")", "\\)")
        .replace("{", "\\{")
        .replace("}", "\\}")
        .replace("[", "\\[")
        .replace("]", "\\]")

const val DATE_FORMAT = "dd.MM.yyyy"

const val TIME_FORMAT = "HH:mm:ss"

const val DATA_TIME_FORMAT = "dd.MM.yyyy HH:mm:ss"

const val DATA_TIME_SHORT_FORMAT = "E, HH:mm:ss"

const val SERVER_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"


fun Date.toTimeUTCFormat(): String = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
    .apply { timeZone = TimeZone.getTimeZone("UTC") }
    .format(this)

fun Date.toTimeFormat(): String = SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(this)
fun Date.toDateFormat(): String = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(this)
fun Date.toDateTimeFormat(): String = SimpleDateFormat(DATA_TIME_FORMAT, Locale.getDefault()).format(this)
fun Date.toDateTimeShortFormat(): String = SimpleDateFormat(DATA_TIME_SHORT_FORMAT, Locale.getDefault()).format(this)

fun Date.toServerFormat(): String = SimpleDateFormat(SERVER_FORMAT, Locale.getDefault()).format(this)





