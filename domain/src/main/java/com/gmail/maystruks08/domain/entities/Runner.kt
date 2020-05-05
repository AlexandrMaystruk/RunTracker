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

    /**
     * Add checkpoint to runner entity
     * If previous checkpoint is absent -> mark current checkpoint as hasPrevious = false
     * If checkpoint with current checkpoint id already exist -> remove old checkpoint and add new
     */
    fun addPassedCheckpoint(checkpoint: CheckpointResult, checkpointsCount: Int, isRestart: Boolean = false) {
        val indexOfExistingElement = checkpoints.indexOfFirst { it.id == checkpoint.id && it.type == checkpoint.type }
        if (indexOfExistingElement != -1) {
            checkpoints.removeAt(indexOfExistingElement)
        }
        val lastCheckpoint = checkpoints.lastOrNull()
        if (!isRestart) {
            if (lastCheckpoint?.id != checkpoint.id - 1 || !lastCheckpoint.hasPrevious) {
                checkpoint.hasPrevious = false
            }
            checkpoints.add(checkpoint)
            if (checkpoints.size == checkpointsCount) {
                totalResult = calculateTotalResult()
            }
        } else {
            checkpoints.clear()
            totalResult = null
            checkpoints.add(checkpoint)
        }
        checkpoints.sortBy { it.id }
        for (index in 0 until checkpoints.lastIndex) {
            val current = checkpoints[index]
            val next = checkpoints[index + 1]
            if (current.id == next.id - 1) {
                checkpoints[index].hasPrevious = true
            } else return
        }
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