package com.gmail.maystruks08.nfcruntracker.ui.register

import androidx.lifecycle.MutableLiveData
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import ru.terrakok.cicerone.Router
import java.util.*
import javax.inject.Inject

class RegisterNewRunnerViewModel @Inject constructor(private val router: Router) : BaseViewModel(){

    val selectDateOfBirthdayClicked get() = _selectDateOdBirthdayClickedLiveData
    val onDateOfBirthdaySelected get() = _selectDateOdBirthdaySelectedLiveData

    private val _selectDateOdBirthdayClickedLiveData = MutableLiveData<Unit>()
    private val _selectDateOdBirthdaySelectedLiveData = MutableLiveData<Date>()


    fun onSelectDateOfBirthdayClicked(){
        _selectDateOdBirthdayClickedLiveData.postValue(Unit)
    }

    fun onDateOfBirthdaySelected(date: Date){
        _selectDateOdBirthdaySelectedLiveData.postValue(date)
    }

    fun onNfcCardScanned(cardId: String) {
        //TODO save card id
    }

    fun onBackClicked(){
        router.exit()
    }
}