package com.gmail.maystruks08.data.remote.pojo

data class RunnerPojo(
    var number: String = "",
    var cardId: String? = null,
    var fullName: String = "",
    val shortName: String = "",
    val phone: String = "",
    var sex: Int = 0,
    var city: String = "",
    var dateOfBirthday: String? = null,
    val actualRaceId: String = "",
    val actualDistanceId: String = "",
    val raceIds: MutableList<String> = mutableListOf(),
    val distanceIds: MutableList<String> = mutableListOf(),
    val checkpoints: MutableMap<String, List<CheckpointPojo>> = mutableMapOf(), //key distance id value checkpoints
    val offTrackDistances: MutableList<String> = mutableListOf(), //key distance id
    val teamNames: MutableMap<String, String?> = mutableMapOf(), //key distance id
    val totalResults: MutableMap<String, String?> = mutableMapOf(), //key distance id
)
