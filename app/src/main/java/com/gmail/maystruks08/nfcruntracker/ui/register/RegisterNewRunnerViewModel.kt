package com.gmail.maystruks08.nfcruntracker.ui.register

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.exception.EmptyRegistrationRunnerDataException
import com.gmail.maystruks08.domain.exception.RunnerWithIdAlreadyExistException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import com.gmail.maystruks08.domain.interactors.RegisterNewRunnerUseCase
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.base.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.terrakok.cicerone.Router
import timber.log.Timber

class RegisterNewRunnerViewModel @ViewModelInject constructor(
    private val router: Router,
    private val interactor: RegisterNewRunnerUseCase
) : BaseViewModel() {


    val scannedCard get() : LiveData<String> = _cardIdLiveData
    val addNewTeamMemberItem get() : LiveData<InputDataView> = _addNewTeamMemberItem
    val error get() : LiveData<Throwable> = _errorLiveData

    private val _cardIdLiveData = SingleLiveEvent<String>()
    private val _addNewTeamMemberItem = SingleLiveEvent<InputDataView>()
    private val _errorLiveData = SingleLiveEvent<Throwable>()

    fun onNfcCardScanned(cardId: String) {
        _cardIdLiveData.postValue(cardId)
    }

    fun onBackClicked() {
        router.exit()
    }

    fun onRegisterNewRunnerClicked(runnerRegisterData: List<InputDataView>, raceId: String, distanceId: String, teamName: String?) {
        val isEmptyInputField = runnerRegisterData.any { it.isEmpty() }
        if (isEmptyInputField || (runnerRegisterData.size > 1 && teamName.isNullOrEmpty())) {
            _errorLiveData.postValue(EmptyRegistrationRunnerDataException())
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val inputData = runnerRegisterData.map {
                    val shortName = if (it.fullName?.contains(" ".toRegex()) == true) {
                        it.shortName ?: it.fullName?.substring(0, it.fullName!!.indexOf(" ")) ?: ""
                    } else {
                        it.shortName ?: it.fullName ?: ""
                    }
                    RegisterNewRunnerUseCase.RegisterInputData(
                        fullName = it.fullName!!,
                        shortName = shortName,
                        phone = it.phone!!,
                        runnerSex = it.runnerSex!!,
                        dateOfBirthday = it.dateOfBirthday!!,
                        city = it.city!!,
                        runnerNumber = it.runnerNumber!!,
                        runnerCardId = it.runnerCardId,
                        teamName = teamName,
                    )
                }
                when (val result = interactor.invoke(raceId, distanceId, inputData)) {
                    is TaskResult.Value -> withContext(Dispatchers.Main) { onBackClicked() }
                    is TaskResult.Error -> handleError(result.error)
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