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
        checkpoints.find { it.state == CheckpointState.STEP_CURRENT }

    fun markCheckpointAsPassed(checkpoint: Checkpoint): Boolean {
        val index = checkpoints.indexOfFirst { it.id == checkpoint.id }
        if (index <= checkpoints.size && index != -1) {
            val previousIsDone = if (index == 0) {
                true
            } else {
                checkpoints[index - 1].state == CheckpointState.STEP_CURRENT
            }
            if (previousIsDone) {
                for (i in 0 until index) {
                    checkpoints[i].state = CheckpointState.STEP_COMPLETED
                }
                checkpoints[index].date = Date()
                checkpoints[index].state = CheckpointState.STEP_CURRENT
                return true
            }
        }
        return false
    }

    private fun isAllDone(): Boolean {
        checkpoints.forEachIndexed { index, checkpoint ->
            if (checkpoint.state == CheckpointState.STEP_UNDO && index != checkpoints.lastIndex || index == checkpoints.lastIndex && checkpoint.state == CheckpointState.STEP_CURRENT) {
                return false
            }
        }
        return true
    }
}