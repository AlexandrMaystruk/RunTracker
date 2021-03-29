package com.gmail.maystruks08.data.cache

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApplicationCache @Inject constructor() {

    var adminUserIds = ArrayList<String>()

}