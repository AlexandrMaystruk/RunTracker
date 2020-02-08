package com.gmail.maystruks08.nfcruntracker.ui.runner

import androidx.lifecycle.MutableLiveData
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.ext.toTimeFormat
import com.gmail.maystruks08.nfcruntracker.ui.stepview.StepBean
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.CheckpointView
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class RunnerViewModel @Inject constructor(private val router: Router) : BaseViewModel() {

    val runner get() = runnerLiveData

    private val runnerLiveData = MutableLiveData<RunnerView>()

    fun onShowRunnerClicked(runnerView: RunnerView) {
        val stringBuilder = StringBuilder()
        val runner = RunnerView(
            runnerView.id,
            runnerView.number,
            runnerView.name,
            runnerView.surname,
            runnerView.dateOfBirthday,
            runnerView.checkpoints.map {
                stringBuilder.append(it.stepBean.name)
                if (it.date != null) {
                    stringBuilder.append(" ")
                    stringBuilder.append(it.date.toTimeFormat())
                }
                val mapped = CheckpointView(
                    it.id,
                    StepBean(stringBuilder.toString(), it.stepBean.state),
                    it.date
                )
                stringBuilder.clear()
                stringBuilder.setLength(0)
                mapped
            }
        )
        runnerLiveData.postValue(runner)
    }

    fun markCheckpointAsPassed(runnerView: RunnerView){
        //TODO mark checkpoint as passed in manual
    }

    fun onBackClicked(){
        router.exit()
    }
}