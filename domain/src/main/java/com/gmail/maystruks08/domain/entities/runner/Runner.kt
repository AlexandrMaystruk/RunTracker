package com.gmail.maystruks08.domain.entities.runner

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointImpl
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointResultIml
import java.util.*

data class Runner(
    val number: String,
    var cardId: String?,
    val fullName: String,
    val shortName: String,
    val phone: String,
    val sex: RunnerSex,
    val city: String,
    val dateOfBirthday: Date?,
    val actualRaceId: String,
    override val actualDistanceId: String,
    val currentCheckpoints: MutableList<Checkpoint>,
    var offTrackDistance: String?,
    val currentTeamName: String?,
    var currentResult: Date? = null
) : IRunner {

    override val id get() = number
    override var lastAddedCheckpoint: Checkpoint? = null

    fun markThatRunnerIsOffTrack() {
        offTrackDistance = actualDistanceId
    }

    fun updateCardId(newCardId: String) {
        cardId = newCardId
    }

    /**
     * Add checkpoint to runner entity
     * If previous checkpoint is absent -> mark current checkpoint as hasPrevious = false
     * If checkpoint with current checkpoint id already exist -> remove old checkpoint and add new
     */
    fun addPassedCheckpoint(
        checkpoint: Checkpoint,
        isRestart: Boolean = false
    ) {
       check(checkpoint.getResult() != null)
        val actualCheckpoints = currentCheckpoints
        val indexOfExistingElement = actualCheckpoints.indexOfFirst { it.getId() == checkpoint.getId() && it.getDistanceId() == checkpoint.getDistanceId() }
        if (indexOfExistingElement != -1) {
            actualCheckpoints.removeAt(indexOfExistingElement)
            if (!isRestart) addCheckpoint(checkpoint, indexOfExistingElement, actualCheckpoints.size) else addStartCheckpoint(checkpoint)
            for (index in 1 until actualCheckpoints.lastIndex) {
                val current = actualCheckpoints[index]
                if (!hasNotPassedPreviously(current)) {
                    actualCheckpoints[index].setHasPrevious(true)
                }
            }
        }
        lastAddedCheckpoint = checkpoint
        calculateTotalResults()
    }

    fun removeCheckpoint(checkpointId: String) {
        val actualCheckpoints = currentCheckpoints
        val index = actualCheckpoints.indexOfFirst { it.getId() == checkpointId }
        if (index != -1) {
            currentResult = null
            val oldCheckpoint = actualCheckpoints[index]
            actualCheckpoints[index] = CheckpointImpl(
                oldCheckpoint.getId(),
                oldCheckpoint.getDistanceId(),
                oldCheckpoint.getName(),
                oldCheckpoint.getPosition()
            )
        }
    }

    private fun addStartCheckpoint(checkpoint: Checkpoint) {
        val mappedCheckpoints = currentCheckpoints.mapNotNull {
            if (checkpoint.getId() == it.getId() && checkpoint.getDistanceId() == it.getDistanceId()) return@mapNotNull null
            CheckpointImpl(
                it.getId(),
                it.getDistanceId(),
                it.getName(),
                it.getPosition()
            )
        }
        currentResult = null
        currentCheckpoints.clear()
        currentCheckpoints.add(checkpoint)
        currentCheckpoints.addAll(mappedCheckpoints)
        lastAddedCheckpoint = checkpoint
    }

    /**
     * Need to add logic. Is it possible to change the order of passage of checkpoints?
     */
    private fun addCheckpoint(checkpoint: Checkpoint, index: Int,  checkpointsCount: Int) {
        if (hasNotPassedPreviously(checkpoint)) {
            checkpoint.setHasPrevious(false)
        }
        currentCheckpoints.add(index, checkpoint)
        if (currentCheckpoints.size == checkpointsCount) {
            calculateTotalResults()
        }
    }

    private fun hasNotPassedPreviously(checkpoint: Checkpoint): Boolean {
        val index = currentCheckpoints.indexOfFirst { it.getId() == checkpoint.getId() }
        if (index == -1) return true
        for (x in 0 until index) {
            val item = currentCheckpoints[x]
            if (item.getResult() == null || !item.hasPrevious()) return true
        }
        return false
    }

    fun calculateTotalResults() {
        if (currentCheckpoints.any { it is CheckpointImpl }) return
        val first = currentCheckpoints.firstOrNull()?.getResult()
        val last = currentCheckpoints.lastOrNull()?.getResult()
        if (first != null && last != null) {
            currentResult = Date(last.time - first.time)
        }
    }

    fun getCheckpointCount() = currentCheckpoints.count()

    override fun getPassedCheckpointCount() = currentCheckpoints.count { it is CheckpointResultIml }

}

