package com.gmail.maystruks08.data.mappers

import com.gmail.maystruks08.data.local.RunnerTable
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.entities.RunnerType

fun RunnerTable.toRunner(): Runner {
    return Runner(
        id = this.id,
        number = this.number,
        name = this.name,
        surname = this.surname,
        city = this.city,
        dateOfBirthday = this.dateOfBirthday,
        type = RunnerType.fromId(this.type),
        checkpoints = listOf()
    )
}