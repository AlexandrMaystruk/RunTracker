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

    fun addPassedCheckpoint(
        checkpoint: CheckpointResult,
        maxCount: Int,
        isRestart: Boolean = false
    ): Boolean {
        val lastCheckpoint = checkpoints.lastOrNull()

        if (isRestart) {
            checkpoints.clear()
            totalResult = null
            checkpoints.add(checkpoint)
            return true
        }

        if (checkpoints.isEmpty() && checkpoint.id == 0 || lastCheckpoint?.id == checkpoint.id - 1) {
            checkpoints.add(checkpoint)
            if (checkpoints.size == maxCount) {
                totalResult = calculateTotalResult()
            }
            return true
        }
        return false
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