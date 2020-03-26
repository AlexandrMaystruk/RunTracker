package com.gmail.maystruks08.domain.entities

import java.util.*

data class Runner(
    val id: String = "",
    val number: Int = -1,
    val fullName: String = "",
    val sex: RunnerSex = RunnerSex.MALE,
    val city: String = "",
    val dateOfBirthday: Date = Date(),
    val type: RunnerType = RunnerType.NORMAL,
    val checkpoints: List<Checkpoint> = listOf()
) {

    fun getTotalResult(): Date? {
        return if (isAllDone()) {
            Date(checkpoints.last().date!!.time - checkpoints.first().date!!.time)
        } else null
    }

    fun getCurrentPosition(): Checkpoint? =
        checkpoints.find { it.state == CheckpointState.CURRENT }

    fun markCheckpointAsPassed(checkpoint: Checkpoint): Boolean {
        val index = checkpoints.indexOfFirst { it.id == checkpoint.id }
        val currentCheckpointState = checkpoints[index].state
        if(currentCheckpointState == CheckpointState.DONE || currentCheckpointState == CheckpointState.CURRENT ){
            return false
        }
        if (index <= checkpoints.size && index != -1) {
            val previousIsDone = if (index == 0) {
                true
            } else {
                checkpoints[index - 1].state == CheckpointState.CURRENT
            }
            if (previousIsDone) {
                for (i in 0 until index) {
                    checkpoints[i].state = CheckpointState.DONE
                }
                checkpoints[index].date = Date()
                checkpoints[index].state = CheckpointState.CURRENT
                return true
            }
        }
        return false
    }

    fun removeCheckpoint(checkpointId: Int){
        checkpoints.find { it.id == checkpointId }?.also {
            it.state = CheckpointState.UNDONE
            it.date = null
        }
    }

    private fun isAllDone(): Boolean {
        checkpoints.forEachIndexed { index, checkpoint ->
            if (checkpoint.state == CheckpointState.UNDONE && index != checkpoints.lastIndex || index == checkpoints.lastIndex && checkpoint.state == CheckpointState.CURRENT) {
                return false
            }
        }
        return true
    }
}