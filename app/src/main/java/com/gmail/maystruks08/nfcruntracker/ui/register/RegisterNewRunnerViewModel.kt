package com.gmail.maystruks08.nfcruntracker.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.entities.RunnerSex
import com.gmail.maystruks08.domain.entities.RunnerType
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import com.gmail.maystruks08.domain.interactors.RegisterNewRunnerInteractor
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import java.util.*
import javax.inject.Inject

class RegisterNewRunnerViewModel @Inject constructor(private val interactor: RegisterNewRunnerInteractor, private val router: Router) : BaseViewModel(){

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

    fun onRegisterNewRunner(fullName: String,
                            runnerSex: RunnerSex,
                            dateOfBirthday: Date,
                            city: String,
                            runnerNumber: Int,
                            runnerType: RunnerType,
                            runnerCardId: String) {

        viewModelScope.launch {
            when (val onResult = interactor.registerNewRunner(fullName, runnerSex, dateOfBirthday,
                city, runnerNumber, runnerType, runnerCardId)) {

                is ResultOfTask.Value -> onBackClicked()
                is ResultOfTask.Error -> handleError(onResult.error)
            }
        }
    }

    private fun handleError(e: Exception) {
        when(e){
            is SaveRunnerDataException -> {}
            is SyncWithServerException -> {}
            else ->  e.printStackTrace()
        }
    }
}