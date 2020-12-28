package com.gmail.maystruks08.data.cache

import com.gmail.maystruks08.domain.entities.Race
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class ApplicationCache @Inject constructor() {

    val race: Race? = null

    /**
     * Key -> distance id, value -> checkpoint
     */
    val selectedCheckpoints = mutableMapOf<Long, Checkpoint?>()

    var adminUserIds = ArrayList<String>()

}