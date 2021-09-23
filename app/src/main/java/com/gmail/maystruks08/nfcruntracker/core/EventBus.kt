package com.gmail.maystruks08.nfcruntracker.core

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ExperimentalCoroutinesApi
class EventBus @Inject constructor() {

    private val _reloadStateFloat = MutableStateFlow<ReloadEvent?>(null)
    val needToReload: StateFlow<ReloadEvent?> = _reloadStateFloat

    fun sendReloadEvent(){
        _reloadStateFloat.value = ReloadEvent.DistanceWithRunners
    }

    fun sendUpdateOnlyCircleMenuEvent(){
        _reloadStateFloat.value = ReloadEvent.UpdateCircleMenuState
    }

    sealed class ReloadEvent{
        object DistanceWithRunners: ReloadEvent()
        object UpdateCircleMenuState: ReloadEvent()
    }

}