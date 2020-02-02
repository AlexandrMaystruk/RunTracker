package com.gmail.maystruks08.data.cache

import com.gmail.maystruks08.domain.entities.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RunnersCache @Inject constructor() {

    //TODO remove
    var checkpointsList = listOf(
        Checkpoint("С", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("15", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("46", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("52", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("70", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("81", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("90", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("Ф", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date()))
    )

    var checkpointsIronPeopleList = listOf(
        Checkpoint("С", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("7.5", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("15", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("42", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("52", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("70", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("81", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("91", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date())),
        Checkpoint("Ф", CheckpointState.STEP_UNDO,null,  CheckpointSettings(Date(), Date()))
    )
    val runnersList = mutableListOf<Runner>().apply {
        this.add(Runner("96DAF3F7", 7,"Александр", "Майстук", "Одесса", Date(), RunnerType.NORMAL, checkpointsList.toMutableList()))
        this.add(Runner("5658FAF7", 8,"Анна", "Голодягина","Одесса", Date(), RunnerType.IRON, checkpointsIronPeopleList.toMutableList()))
        this.add(Runner("DBC5DB13", 36,"Степан", "Петрович", "Одесса",Date(), RunnerType.NORMAL, checkpointsList.toMutableList()))
        this.add(Runner("8BE1D513", 77,"Дядя", "Жора", "Одесса",Date(), RunnerType.IRON, checkpointsIronPeopleList.toMutableList()))
    }

}