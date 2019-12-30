package com.gmail.maystruks08.data.mappers

import com.gmail.maystruks08.data.local.RunnerTable
import com.gmail.maystruks08.domain.entities.Runner

fun RunnerTable.toRunner(): Runner {
    return Runner(
        id = this.id,
        name = this.name,
        surname = this.surname,
        age = this.age
    )
}