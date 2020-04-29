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
    val checkpoints: MutableList<CheckpointResult> = mutableListOf()
) {

    fun getCurrentPosition(): CheckpointResult? = checkpoints.lastOrNull()

    /**
     * Add checkpoint to runner entity
     * If previous checkpoint is absent -> mark current checkpoint as hasPrevious = false
     */
    fun addPassedCheckpoint(checkpoint: CheckpointResult, finishCheckpointId: Int, isRestart: Boolean = false) {
        val lastCheckpoint = checkpoints.lastOrNull()
        if (!isRestart) {
            if (lastCheckpoint?.id != checkpoint.id - 1 || !lastCheckpoint.hasPrevious) {
                checkpoint.hasPrevious = false
            }
            if (checkpoint.id == finishCheckpointId) {
                totalResult = calculateTotalResult()
            }
        } else {
            checkpoints.clear()
            totalResult = null
        }
        checkpoints.add(checkpoint)
    }

    fun removeCheckpoint(checkpointId: Int) {
        val index = checkpoints.indexOfFirst { it.id == checkpointId }
        if (index != -1) {
            totalResult = null
            val filtered = checkpoints.filterIndexed { i, _ -> i < index }
            checkpoints.clear()
            checkpoints.addAll(filtered)
        }
    }

    private fun calculateTotalResult(): Date? {
        val first = checkpoints.firstOrNull()?.date?.time
        val last = checkpoints.lastOrNull()?.date?.time
        return if (first != null && last != null) Date(last - first) else null
    }
}