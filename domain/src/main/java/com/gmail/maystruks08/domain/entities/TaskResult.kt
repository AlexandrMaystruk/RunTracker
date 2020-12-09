package com.gmail.maystruks08.domain.entities

sealed class TaskResult<out E, out V> {

    data class Value<out V>(val value: V) : TaskResult<Nothing, V>()
    object Loading : TaskResult<Nothing, Nothing>()
    data class Error<out E>(val error: E) : TaskResult<E, Nothing>()

    companion object Factory {

        inline fun <V> build(function: () -> V): TaskResult<Exception, V> =
            try {
                Value(function.invoke())
            } catch (e: java.lang.Exception) {
                Error(e)
            }

        fun buildLoading(): TaskResult<Exception, Nothing> {
            return Loading
        }
    }
}