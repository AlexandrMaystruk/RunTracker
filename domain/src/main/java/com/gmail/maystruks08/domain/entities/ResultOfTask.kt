package com.gmail.maystruks08.domain.entities

sealed class ResultOfTask<out E, out V> {

    data class Value<out V>(val value: V) : ResultOfTask<Nothing, V>()
    object Loading : ResultOfTask<Nothing, Nothing>()
    data class Error<out E>(val error: E) : ResultOfTask<E, Nothing>()

    companion object Factory {

        inline fun <V> build(function: () -> V): ResultOfTask<Exception, V> =
            try {
                Value(function.invoke())
            } catch (e: java.lang.Exception) {
                Error(e)
            }

        fun buildLoading(): ResultOfTask<Exception, Nothing> {
            return Loading
        }
    }
}