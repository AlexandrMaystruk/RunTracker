package com.gmail.maystruks08.domain.entities

import java.util.*

class CheckpointResult(id: Int, name: String, var date: Date) : Checkpoint(id, name) {

    constructor() : this(-1, "", Date())
}