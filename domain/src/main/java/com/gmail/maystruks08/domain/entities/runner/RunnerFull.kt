package com.gmail.maystruks08.domain.entities.runner

import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointImpl
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointResultIml
import java.util.*

data class RunnerFull(
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
    val raceIds: MutableList<String>,
    val distanceIds: MutableList<String>,
    val checkpoints: MutableMap<String, MutableList<Checkpoint>>, //key distance id value checkpoints
    val offTrackDistances: MutableList<String>, //distance id
    val teamNames: MutableMap<String, String?>, //key distance id
    val totalResults: MutableMap<String, Date?>  //key distance id
) : IRunner {

    override val id get() = number
    override var lastAddedCheckpoint: Checkpoint? = null

    override fun getTotalResult(): Date? {
        return totalResults[actualDistanceId]
    }

    override fun checkIsOffTrack(): Boolean {
        return offTrackDistances.contains(actualDistanceId)
    }

    val currentTeamName get() = teamNames[actualDistanceId]
    val currentCheckpoints get() = checkpoints[actualDistanceId]
    val currentResult get() = totalResults[actualDistanceId]

    fun markThatRunnerIsOffTrack() {
        offTrackDistances.add(actualDistanceId)
    }

    fun updateCardId(newCardId: String) {
        cardId = newCardId
    }

    fun addCheckpoints(distanceId: String, newCheckpoints: MutableList<Checkpoint>) {
        checkpoints[distanceId] = newCheckpoints
        calculateTotalResultForDistance(distanceId)
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
        val actualCheckpoints = currentCheckpoints ?: mutableListOf()
        val indexOfExistingElement =
            actualCheckpoints.indexOfFirst { it.getId() == checkpoint.getId() && it.getDistanceId() == checkpoint.getDistanceId() }
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
    }

    fun removeCheckpoint(checkpointId: String) {
        val actualCheckpoints = currentCheckpoints ?: mutableListOf()
        val index = actualCheckpoints.indexOfFirst { it.getId() == checkpointId }
        if (index != -1) {
            totalResults[actualDistanceId] = null
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
        val mappedCheckpoints = currentCheckpoints?.map {
            CheckpointImpl(
                it.getId(),
                it.getDistanceId(),
                it.getName(),
                it.getPosition()
            )
        }.orEmpty()
        totalResults[actualDistanceId] = null
        checkpoints.clear()
        checkpoints[actualDistanceId]?.add(checkpoint)
        checkpoints[actualDistanceId]?.addAll(mappedCheckpoints)
        lastAddedCheckpoint = checkpoint
    }

    /**
     * Need to add logic. Is it possible to change the order of passage of checkpoints?
     */
    private fun addCheckpoint(checkpoint: Checkpoint, index: Int,  checkpointsCount: Int) {
        if (hasNotPassedPreviously(checkpoint)) {
            checkpoint.setHasPrevious(false)
        }
        val actualCheckpoints = currentCheckpoints ?: mutableListOf()
        actualCheckpoints.add(index, checkpoint)
        if (checkpoints.size == checkpointsCount) {
            totalResults[actualDistanceId] = calculateTotalResult()
        }
    }

    private fun hasNotPassedPreviously(checkpoint: Checkpoint): Boolean {
        val actualCheckpoints = currentCheckpoints ?: mutableListOf()
        val index = actualCheckpoints.indexOfFirst { it.getId() == checkpoint.getId() }
        if (index == -1) return true
        for (x in 0 until index) {
            val item = actualCheckpoints[x]
            if (item.getResult() == null || !item.hasPrevious()) return true
        }
        return false
    }

    /**
     * Need to add logic. Is it possible to change the order of passage of checkpoints?
     */
    private fun calculateTotalResult(): Date? {
        val actualCheckpoints = currentCheckpoints ?: mutableListOf()
        val first = actualCheckpoints.firstOrNull()?.getResult()
        val last = actualCheckpoints.lastOrNull()?.getResult()
        return if (first != null && last != null) Date(last.time - first.time) else null
    }

    private fun calculateTotalResultForDistance(distanceId: String) {
        val actualCheckpoints = currentCheckpoints ?: mutableListOf()
        val first = actualCheckpoints.firstOrNull()?.getResult()
        val last = actualCheckpoints.lastOrNull()?.getResult()
        if (first != null && last != null) {
            totalResults[distanceId] = Date(last.time - first.time)
        }
    }

    fun calculateTotalResults() {
        if(currentCheckpoints?.any { it is CheckpointImpl } == true) return
        val actualCheckpoints = currentCheckpoints ?: mutableListOf()
        val first = actualCheckpoints.firstOrNull()?.getResult()
        val last = actualCheckpoints.lastOrNull()?.getResult()
        if (first != null && last != null) {
            totalResults[actualDistanceId] = Date(last.time - first.time)
        }
    }

    fun getCheckpointCount() = checkpoints[actualDistanceId]?.count() ?: 0

    override fun getPassedCheckpointCount() =
        currentCheckpoints?.count { it is CheckpointResultIml } ?: 0

}

