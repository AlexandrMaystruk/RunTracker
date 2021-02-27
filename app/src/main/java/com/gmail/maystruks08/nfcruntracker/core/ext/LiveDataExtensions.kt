package com.gmail.maystruks08.nfcruntracker.core.ext

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

fun <T, K> LiveData<T>.switchMap(function: (T) -> LiveData<K>): LiveData<K> {
    return Transformations.switchMap(this) { value ->
        function(value)
    }
}


fun <T> MutableList<T>.updateElement(element: T, predicate: (T) -> Boolean) {
    val iterator = iterator()
    var index = 0
    while (iterator.hasNext()) {
        val item = iterator.next()
        if (predicate(item)) {
            iterator.remove()
            add(index, element)
            return
        }
        index++
    }
}
