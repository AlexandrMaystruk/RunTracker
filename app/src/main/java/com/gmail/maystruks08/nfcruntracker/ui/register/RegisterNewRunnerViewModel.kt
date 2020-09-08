package com.gmail.maystruks08.nfcruntracker.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.exception.EmptyRegistrationRunnerDataException
import com.gmail.maystruks08.domain.exception.RunnerWithIdAlreadyExistException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import com.gmail.maystruks08.domain.interactors.RegisterNewRunnerInteractor
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import kotlinx.coroutines.*
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

class RegisterNewRunnerViewModel @Inject constructor(
    private val interactor: RegisterNewRunnerInteractor,
    private val router: Router
) : BaseViewModel() {

    val scannedCard get() : LiveData<String> = _cardIdLiveData
    val addNewTeamMemberItem get() : LiveData<InputDataView> = _addNewTeamMemberItem
    val error get() : LiveData<Throwable> = _errorLiveData

    private val _cardIdLiveData = MutableLiveData<String>()
    private val _addNewTeamMemberItem = MutableLiveData<InputDataView>()
    private val _errorLiveData = MutableLiveData<Throwable>()

    fun onNfcCardScanned(cardId: String) {
        _cardIdLiveData.postValue(cardId)
    }

    fun onBackClicked() {
        router.exit()
    }

    fun onRegisterNewRunnerClicked(runnerRegisterData: List<InputDataView>, teamName: String?) {
        val isEmptyInputField = runnerRegisterData.any { it.isEmpty() }
        if (isEmptyInputField || (runnerRegisterData.size > 1 && teamName.isNullOrEmpty())) {
            _errorLiveData.postValue(EmptyRegistrationRunnerDataException())
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val inputData = runnerRegisterData.map {
                    val shortName = it.shortName ?: it.fullName?.substring(0, it.fullName!!.indexOf(" ")) ?: ""
                    RegisterNewRunnerInteractor.RegisterInputData(
                        fullName = it.fullName!!,
                        shortName = shortName,
                        phone = it.phone!!,
                        runnerSex = it.runnerSex!!,
                        dateOfBirthday = it.dateOfBirthday!!,
                        city = it.city!!,
                        runnerNumber = it.runnerNumber!!,
                        runnerType = it.runnerType!!,
                        runnerCardId = it.runnerCardId!!,
                        teamName = teamName
                    )
                }
                when (val result = interactor.registerNewRunners(inputData)) {
                    is ResultOfTask.Value -> withContext(Dispatchers.Main) { onBackClicked() }
                    is ResultOfTask.Error -> handleError(result.error)
                }
            }
        }
    }

    private fun handleError(e: Exception) {
        when (e) {
            is SaveRunnerDataException,
            is SyncWithServerException,
            is RunnerWithIdAlreadyExistException -> _errorLiveData.postValue(e)
            else -> Timber.e(e)
        }
    }

    fun onCreateTeamMemberClick() {
        _addNewTeamMemberItem.value = InputDataView()
    }
}