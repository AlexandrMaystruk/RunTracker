package com.gmail.maystruks08.nfcruntracker.ui.result

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.DEF_STRING_VALUE
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.interactors.RunnersInteractor
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerResultView
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.toRunnerResultView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router

@ExperimentalCoroutinesApi
class RunnerResultViewModel @ViewModelInject constructor(
    private val router: Router,
    private val interactor: RunnersInteractor
) : BaseViewModel() {

    val runnerResults get(): StateFlow<List<RunnerResultView>> = _runnerResultsStateFlow
    val error get() : StateFlow<Throwable?> = _errorStateFlow

    private val _runnerResultsStateFlow = MutableStateFlow<List<RunnerResultView>>(mutableListOf())
    private val _errorStateFlow = MutableStateFlow<Throwable?>(null)

    private var distanceId: String = DEF_STRING_VALUE

    fun provideFinishers(distanceId: String) {
        this.distanceId = distanceId
        viewModelScope.launch(Dispatchers.IO) {
            interactor.getFinishersFlow(distanceId)
                .catch { error ->
                    handleError(error)
                }
                .collect {
                    _runnerResultsStateFlow.value = it.mapIndexed { index: Int, runner: Runner ->
                        runner.toRunnerResultView(index + 1)
                    }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (query.isNotEmpty()) {
                when (val result = interactor.getFinishers(distanceId, query)) {
                    is TaskResult.Value -> {
                        _runnerResultsStateFlow.value =
                            result.value.mapIndexed { index: Int, runner: Runner ->
                                runner.toRunnerResultView(index + 1)
                            }
                    }
                    is TaskResult.Error -> handleError(result.error)
                }
            } else provideFinishers(distanceId)
        }
    }

    fun onBackClicked() {
        router.exit()
    }

    private fun handleError(e: Throwable) {
        when (e) {
            is RunnerNotFoundException, is SaveRunnerDataException -> _errorStateFlow.value = e
        }
    }
}