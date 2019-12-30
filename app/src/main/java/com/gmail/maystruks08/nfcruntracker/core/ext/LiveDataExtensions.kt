package com.gmail.maystruks08.nfcruntracker.core.ext

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

fun <T, K> LiveData<T>.switchMap(function: (T) -> LiveData<K>): LiveData<K> {
    return Transformations.switchMap(this) { value ->
        function(value)
    }

}