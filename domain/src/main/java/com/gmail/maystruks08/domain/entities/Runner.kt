package com.gmail.maystruks08.domain.entities

import java.util.*

data class Runner(
    val number: Int,
    var cardId: String,
    val fullName: String,
    val shortName: String,
    val phone: String,
    val sex: RunnerSex,
    val city: String,
    val dateOfBirthday: Date,
    val type: RunnerType,
    var totalResult: Date?,
    val teamName: String?,
    val checkpoints: MutableList<Checkpoint> = mutableListOf(),
    var isOffTrack: Boolean
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
    fun addPassedCheckpoint(checkpoint: CheckpointResult, checkpointsCount: Int, isRestart: Boolean = false) {
        val indexOfExistingElement = checkpoints.indexOfFirst { it.id == checkpoint.id && it.type == checkpoint.type }
        if (indexOfExistingElement != -1) {
            checkpoints.removeAt(indexOfExistingElement)
            if (!isRestart) addCheckpoint(checkpoint, checkpointsCount) else addStartCheckpoint(checkpoint)
            checkpoints.sortBy { it.id }
            for (index in 1 until checkpoints.lastIndex) {
                val current = checkpoints[index]
                if(current is CheckpointResult && !hasNotPassedPreviously(current)){
                    (checkpoints[index] as CheckpointResult).hasPrevious = true
                }
            }
        }
    }

    fun removeCheckpoint(checkpointId: Int) {
        val index = checkpoints.indexOfFirst { it.id == checkpointId }
        if (index != -1) {
            totalResult = null
            val oldCheckpoint =  checkpoints[index]
            checkpoints[index] = Checkpoint(oldCheckpoint.id, oldCheckpoint.name, oldCheckpoint.type)
        }
    }

    private fun addStartCheckpoint(checkpoint: CheckpointResult){
        val mappedCheckpoints = checkpoints.map { Checkpoint(it.id, it.name, it.type ) }
        totalResult = null
        checkpoints.clear()
        checkpoints.add(checkpoint)
        checkpoints.addAll(mappedCheckpoints)
    }

    /**
     * Need to add logic. Is it possible to change the order of passage of checkpoints?
     */
    private fun addCheckpoint(checkpoint: CheckpointResult, checkpointsCount: Int){
        if (hasNotPassedPreviously(checkpoint)) {
            checkpoint.hasPrevious = false
        }
        checkpoints.add(checkpoint.id, checkpoint)
        if (checkpoints.size == checkpointsCount) {
            totalResult = calculateTotalResult()
        }
    }

    private fun hasNotPassedPreviously(checkpoint: CheckpointResult): Boolean {
        for (x in 0 until checkpoint.id){
            if(checkpoints[x] !is CheckpointResult) return true
            if((checkpoints[x] as? CheckpointResult)?.hasPrevious == false ) return true
        }
        return false
    }

    /**
     * Need to add logic. Is it possible to change the order of passage of checkpoints?
     */
    private fun calculateTotalResult(): Date? {
        val first = checkpoints.firstOrNull() as? CheckpointResult
        val last = checkpoints.lastOrNull()as? CheckpointResult
        return if (first != null && last != null) Date(last.date.time - first.date.time) else null
    }
}

