package com.gmail.maystruks08.data.local.pojo

import java.util.*

data class RunnerTableView(

    val id: String,
    val number: Int,
    val fullName: String,
    val city: String,
    val dateOfBirthday: Date,
    val type: Int,
    val totalResult: Date,
    val needToSync: Boolean,

    val name: String,
    val checkpointId: Int,
    val time: Date?,
    var hasPrevious: Boolean

)




