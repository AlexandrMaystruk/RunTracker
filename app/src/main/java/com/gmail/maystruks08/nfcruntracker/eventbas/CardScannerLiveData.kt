package com.gmail.maystruks08.nfcruntracker.eventbas

import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardScannerLiveData @Inject constructor() {

    val onNfcReaderScannedCard get() = _onScannedCardLiveData

    private val _onScannedCardLiveData = MutableLiveData<String>()

    fun onCardScanned(cardId: String){
        _onScannedCardLiveData.postValue(cardId)
    }
}