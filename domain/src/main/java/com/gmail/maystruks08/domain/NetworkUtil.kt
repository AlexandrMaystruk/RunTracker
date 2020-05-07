package com.gmail.maystruks08.domain

interface NetworkUtil {

    fun isOnline(): Boolean

    fun subscribeToConnectionChange(key: String, onConnectionChanged: (Boolean) -> Unit)

    fun unsubscribe(key: String)

}