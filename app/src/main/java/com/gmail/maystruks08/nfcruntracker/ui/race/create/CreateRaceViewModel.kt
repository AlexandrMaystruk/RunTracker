package com.gmail.maystruks08.nfcruntracker.ui.race.create

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.interactors.CreateRaceUseCase
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.base.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*


sealed class ViewState
object NameInvalid : ViewState()
object CorrectName : ViewState()
object StartDateInvalid : ViewState()
object CorrectStartDate : ViewState()


@ObsoleteCoroutinesApi
class CreateRaceViewModel @ViewModelInject constructor(
    private val createRaceUseCase: CreateRaceUseCase
) : BaseViewModel() {

    val showProgress: SingleLiveEvent<Boolean> get() = _showProgressLiveData
    val validateInputState: SingleLiveEvent<ViewState> get() = _validateInputStateLiveData
    val dismissDialog: SingleLiveEvent<Unit> get() = _dismissDialogLiveData

    private val _showProgressLiveData = SingleLiveEvent<Boolean>()
    private val _validateInputStateLiveData = SingleLiveEvent<ViewState>()
    private val _dismissDialogLiveData = SingleLiveEvent<Unit>()


    private var name: String? = null
    private var startDate: Date? = null

    fun onRaceNameChanged(newName: String?) {
        name = newName
        validateInput()
    }

    fun onRaceDateChanged(newDate: Date) {
        startDate = newDate
        validateInput()
    }

    fun onCreateRaceClicked() {
        _showProgressLiveData.value = true
        if (!validateInput()) {
            _showProgressLiveData.value = false
        }
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = createRaceUseCase.invoke(name!!, startDate!!)) {
                is TaskResult.Value -> {
                    _dismissDialogLiveData.postValue(Unit)
                    _showProgressLiveData.postValue(false)
                }
                is TaskResult.Error -> {
                    _showProgressLiveData.postValue(false)
                    handleError(result.error)
                }
            }
        }
    }

    private fun validateInput(): Boolean {
        val isNameValid = checkIsNameValid()
        val isDateValid = checkIsDateValid()
        _validateInputStateLiveData.value = if (isDateValid) CorrectStartDate else StartDateInvalid
        _validateInputStateLiveData.value = if (isNameValid) CorrectName else NameInvalid
        return isNameValid && isDateValid
    }

    private fun checkIsNameValid(): Boolean {
        return name?.length ?: 0 > 3
    }

    private fun checkIsDateValid(): Boolean {
        return startDate != null && startDate?.after(Date()) == true
    }

    private fun handleError(throwable: Throwable) {
        _showProgressLiveData.postValue(false)
        Timber.e(throwable)
    }
}