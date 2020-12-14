package com.gmail.maystruks08.domain.entities.runner

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointImpl
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointResultIml
import java.util.*
import kotlin.collections.ArrayList

data class Runner(
    val number: Long,
    var cardId: String,
    val fullName: String,
    val shortName: String,
    val phone: String,
    val sex: RunnerSex,
    val city: String,
    val dateOfBirthday: Date,
    val actualDistanceId: Long,
    val distanceIds: List<Long>,
    val checkpoints: MutableList<Checkpoint>,
    var isOffTrack: Boolean,
    val teamName: String?,
    var totalResult: Date?,
) {

    fun markThatRunnerIsOffTrack(){
        isOffTrack = true
    }

    fun updateCardId(newCardId: String){
        cardId = newCardId
    }

    /**
     * Add checkpoint to runner entity
     * If previous checkpoint is absent -> mark current checkpoint as hasPrevious = false
     * If checkpoint with current checkpoint id already exist -> remove old checkpoint and add new
     */
    fun addPassedCheckpoint(
        checkpoint: Checkpoint,
        checkpointsCount: Int,
        isRestart: Boolean = false
    ) {
       check(checkpoint.getResult() != null)
        val indexOfExistingElement = checkpoints.indexOfFirst { it.getId() == checkpoint.getId() && it.getDistanceId() == checkpoint.getDistanceId() }
        if (indexOfExistingElement != -1) {
            checkpoints.removeAt(indexOfExistingElement)
            if (!isRestart) addCheckpoint(checkpoint, checkpointsCount) else addStartCheckpoint(checkpoint)
            checkpoints.sortBy { it.getId() }
            for (index in 1 until checkpoints.lastIndex) {
                val current = checkpoints[index]
                if (!hasNotPassedPreviously(current)) {
                    checkpoints[index].setHasPrevious(true)
                }
            }
        }
    }

    fun removeCheckpoint(checkpointId: Long) {
        val index = checkpoints.indexOfFirst { it.getId() == checkpointId }
        if (index != -1) {
            totalResult = null
            val oldCheckpoint = checkpoints[index]
            checkpoints[index] = CheckpointImpl(
                oldCheckpoint.getId(),
                oldCheckpoint.getName(),
                oldCheckpoint.getDistanceId()
            )
        }
    }

    private fun addStartCheckpoint(checkpoint: Checkpoint) {
        val mappedCheckpoints = checkpoints.map { CheckpointImpl(it.getId(), it.getName(), it.getDistanceId()) }
        totalResult = null
        checkpoints.clear()
        checkpoints.add(checkpoint)
        checkpoints.addAll(mappedCheckpoints)
    }

    /**
     * Need to add logic. Is it possible to change the order of passage of checkpoints?
     */
    private fun addCheckpoint(checkpoint: Checkpoint, checkpointsCount: Int) {
        if (hasNotPassedPreviously(checkpoint)) {
            checkpoint.setHasPrevious(false)
        }
        checkpoints.add(checkpoint.getId().toInt(), checkpoint)
        if (checkpoints.size == checkpointsCount) {
            totalResult = calculateTotalResult()
        }
    }

    private fun hasNotPassedPreviously(checkpoint: Checkpoint): Boolean {
        for (x in 0 until checkpoint.getId()) {
            val item = checkpoints[x.toInt()]
            if (item.getResult() == null || !item.hasPrevious()) return true
        }
        return false
    }

    /**
     * Need to add logic. Is it possible to change the order of passage of checkpoints?
     */
    private fun calculateTotalResult(): Date? {
        val first = checkpoints.firstOrNull()?.getResult()
        val last = checkpoints.lastOrNull()?.getResult()
        return if (first != null && last != null) Date(last.time - first.time) else null
    }
}

