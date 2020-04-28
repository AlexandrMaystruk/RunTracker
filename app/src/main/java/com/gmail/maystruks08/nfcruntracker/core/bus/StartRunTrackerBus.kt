package com.gmail.maystruks08.nfcruntracker.core.bus

import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.HashMap
import kotlin.properties.Delegates

@Singleton
class StartRunTrackerBus @Inject constructor() {

    private val onStartCommandCartage: HashMap<String, (Date) -> Unit> = hashMapOf()

    private var startRunTracker: Date by Delegates.observable(Date()) { _, _, newValue ->
        onStartCommandCartage.values.forEach { it.invoke(newValue) }
    }

    fun onStartCommand(date: Date){
        startRunTracker = date
    }

    fun subscribeStartCommandEvent(key: String, onStartCommand: (Date) -> Unit) {
        onStartCommandCartage[key] = onStartCommand
    }

    fun unsubscribe(key: String) {
        onStartCommandCartage.remove(key)
    }

}