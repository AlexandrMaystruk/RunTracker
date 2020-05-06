package com.gmail.maystruks08.nfcruntracker.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.entities.RunnerSex
import com.gmail.maystruks08.domain.entities.RunnerType
import com.gmail.maystruks08.domain.exception.RunnerWithIdAlreadyExistException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import com.gmail.maystruks08.domain.interactors.RegisterNewRunnerInteractor
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.terrakok.cicerone.Router
import java.util.*
import javax.inject.Inject

class RegisterNewRunnerViewModel @Inject constructor(private val interactor: RegisterNewRunnerInteractor, private val router: Router) : BaseViewModel(){

    val selectDateOfBirthdayClicked get(): LiveData<Unit> = _selectDateOdBirthdayClickedLiveData
    val selectedDateOfBirthday get(): LiveData<Date> = _selectDateOdBirthdaySelectedLiveData
    val scannedCard get() : LiveData<String> = _cardIdLiveData

    private val _selectDateOdBirthdayClickedLiveData = MutableLiveData<Unit>()
    private val _selectDateOdBirthdaySelectedLiveData = MutableLiveData<Date>()
    private val _cardIdLiveData = MutableLiveData<String>()


    fun onSelectDateOfBirthdayClicked(){
        _selectDateOdBirthdayClickedLiveData.postValue(Unit)
    }

    fun onDateOfBirthdaySelected(date: Date){
        _selectDateOdBirthdaySelectedLiveData.postValue(date)
    }

    fun onNfcCardScanned(cardId: String) {
        _cardIdLiveData.postValue(cardId)
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

        viewModelScope.launch(Dispatchers.IO) {
            when (val onResult = interactor.registerNewRunner(fullName, runnerSex, dateOfBirthday, city, runnerNumber, runnerType, runnerCardId)) {
                is ResultOfTask.Value -> withContext(Dispatchers.Main){ onBackClicked() }
                is ResultOfTask.Error -> handleError(onResult.error)
            }
        }
    }

    private fun handleError(e: Exception) {
        when(e){
            is SaveRunnerDataException -> {}
            is SyncWithServerException -> {}
            is RunnerWithIdAlreadyExistException-> toastLiveData.postValue("Участник с такой картой уже существует")
            else ->  e.printStackTrace()
        }
    }
}