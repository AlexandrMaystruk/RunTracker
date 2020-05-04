package com.gmail.maystruks08.domain.entities

import java.util.*

class CheckpointResult(
    override val id: Int,
    override val name: String,
    override val type: CheckpointType,
    var date: Date,
    var hasPrevious: Boolean = true
) : Checkpoint(id, name, type) {

    constructor() : this(-1, "", CheckpointType.NORMAL, Date())

    override fun toString(): String = "CheckpointResult(id=$id, name='$name', type=$type, date=$date, hasPrevious=$hasPrevious)"

}