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
    var totalResult: Date? = null,
    val checkpoints: List<Checkpoint> = listOf()
) {

    fun getCurrentPosition(): Checkpoint? = checkpoints.find { it.state == CheckpointState.CURRENT }

    fun markCheckpointAsPassed(checkpoint: Checkpoint): Boolean {
        val index = checkpoints.indexOfFirst { it.id == checkpoint.id }
        if (index <= checkpoints.lastIndex && index != -1) {
            val currentCheckpointState = checkpoints[index].state
            if (currentCheckpointState == CheckpointState.DONE || currentCheckpointState == CheckpointState.CURRENT) {
                return false
            }
            val previousIsDone = if (index == 0) true else checkpoints[index - 1].state == CheckpointState.CURRENT
            if (previousIsDone) {
                checkpoints[index].date = Date()
                checkpoints[index].state = CheckpointState.CURRENT
                for (i in 0 until index) {
                    checkpoints[i].state = CheckpointState.DONE
                }
                if (index == checkpoints.lastIndex) {
                    totalResult = calculateTotalResult()
                }
                return true
            }
        }
        return false
    }

    fun removeCheckpoint(checkpointId: Int) {
        val index = checkpoints.indexOfFirst { it.id == checkpointId }
        if (index <= checkpoints.lastIndex && index != -1) {
            totalResult = null
            checkpoints[index].date = null
            checkpoints[index].state = CheckpointState.UNDONE
            if (index > 0) {
                checkpoints[index - 1].state = CheckpointState.CURRENT
                for (i in index until checkpoints.size) {
                    checkpoints[i].state = CheckpointState.UNDONE
                    checkpoints[i].date = null
                }
            }
        }
    }

    private fun calculateTotalResult(): Date? {
        return if (isAllDone()) Date(checkpoints.last().date!!.time - checkpoints.first().date!!.time) else null
    }

    private fun isAllDone(): Boolean {
        checkpoints.forEachIndexed { index, checkpoint ->
            if (checkpoint.state == CheckpointState.UNDONE || (index == checkpoints.lastIndex && checkpoint.state != CheckpointState.CURRENT)) {
                return false
            }
        }
        return true
    }
}