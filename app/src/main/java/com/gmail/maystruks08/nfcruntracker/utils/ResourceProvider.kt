package com.gmail.maystruks08.nfcruntracker.utils

import android.content.Context
import androidx.annotation.StringRes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceProvider @Inject constructor(private val context: Context) {

    fun getString(@StringRes id: Int): String {
        return context.getString(id)
    }
}