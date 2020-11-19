package com.gmail.maystruks08.domain.entities.checkpoint

import java.util.*

class CheckpointResult(
    id: Int,
    name: String,
    type: CheckpointType,
    var date: Date,
    var hasPrevious: Boolean = true
) : Checkpoint(id, name, type) {

    override fun toString() = "CheckpointResult(id=$id, name='$name', type=$type, date=$date, hasPrevious=$hasPrevious)"

}