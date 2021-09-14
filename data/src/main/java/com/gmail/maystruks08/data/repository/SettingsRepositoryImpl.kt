package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.data.XLSParser
import com.gmail.maystruks08.data.local.ConfigPreferences
import com.gmail.maystruks08.data.mappers.toFirestoreRunner
import com.gmail.maystruks08.data.remote.Api
import com.gmail.maystruks08.data.remote.pojo.DistanceCheckpointPojo
import com.gmail.maystruks08.data.remote.pojo.DistancePojo
import com.gmail.maystruks08.data.remote.pojo.RacePojo
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class SettingsRepositoryImpl @Inject constructor(
    private val preferences: ConfigPreferences,
    private val firebaseAuth: FirebaseAuth,
    private val xlsParser: XLSParser,
    private val api: Api
) : SettingsRepository {


    override suspend fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override fun getAdminUserIds(): List<String> {
        return emptyList()
    }

    override fun clearCurrentSelectedRace() {
        preferences.clearRaceId()
        preferences.clearRaceName()
    }

    override suspend fun exportRunnerDataFromXlsToRemote() {
        val authorId = "maystruks08@gmail.com"
        val raceName = "100_for_24_2021"
        val raceId = getUUID(raceName)
        val uniqueId = raceId
        val runners = xlsParser.readExcelFileFromAssets(raceId, "runners_$uniqueId", "100_24_run.xls")
        val iron = xlsParser.readExcelFileFromAssets(raceId, "iron_$uniqueId", "100_24_iron_run.xls")
        val relayRace = xlsParser.readExcelFileFromAssets(raceId, "relay_race_$uniqueId", "100_24_estaf.xls")
        val teams = xlsParser.readExcelFileFromAssets(raceId, "teams_$uniqueId", "100_24_run_group.xls")

        val distances = listOf(
            DistancePojo(
                id = "runners_$uniqueId",
                raceId = raceId,
                name = "Бегуны",
                authorId = authorId,
                dateOfStart = null,
                runnerIds = runners.map { it.number }
            ),
            DistancePojo(
                id = "iron_$uniqueId",
                raceId = raceId,
                name = "Железные",
                authorId = authorId,
                dateOfStart = null,
                runnerIds = iron.map { it.number }
            ),
            DistancePojo(
                id = "relay_race_$uniqueId",
                raceId = raceId,
                name = "Эстафета",
                authorId = authorId,
                dateOfStart = null,
                runnerIds = relayRace.map { it.number }
            ),
            DistancePojo(
                id = "teams_$uniqueId",
                raceId = raceId,
                name = "Команды",
                authorId = authorId,
                dateOfStart = null,
                runnerIds = teams.map { it.number }
            ),
        )

        distances.forEach {
            val checkpoints = getCheckpoints(it.name, it.id)
            api.saveDistanceCheckpoints(it.id, checkpoints)
        }

        val race = RacePojo(
            id = raceId,
            name = "Одесская Сотка 2021",
            dateCreation = Date(),
            registrationIsOpen = true,
            authorId = authorId,
            adminListIds = emptyList(),
            distanceListIds = distances.map { it.id }
        )
        api.saveRace(race)
        distances.forEach { api.saveDistance(it) }

        exportRunners(runners)
        exportRunners(iron)
        exportRunners(relayRace)
        exportRunners(teams)
    }

    private fun getCheckpoints(distanceName: String, distanceId: String): List<DistanceCheckpointPojo> {
        return when (distanceName) {
            "Бегуны" -> getNormalCheckpoints(distanceId)
            "Железные" -> getIronCheckpoints(distanceId)
            "Эстафета" -> getEstafCheckpoints(distanceId)
            "Команды" -> getTeamCheckpoints(distanceId)
            else -> throw RuntimeException()
        }
    }

    private fun getEstafCheckpoints(distanceId: String) = listOf(
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "S",
            position = 0
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "15",
            position = 1
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "40",
            position = 2
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "52",
            position = 3
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "70",
            position = 4
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "80",
            position = 5
        ),

        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "90",
            position = 6
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "F",
            position = 7
        ),
    )

    private fun getTeamCheckpoints(distanceId: String) = listOf(
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "S",
            position = 0
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "15",
            position = 1
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "40",
            position = 2
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "52",
            position = 3
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "70",
            position = 4
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "80",
            position = 5
        ),

        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "90",
            position = 6
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "F",
            position = 7
        ),
    )

    private fun getNormalCheckpoints(distanceId: String) = listOf(
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "S",
            position = 0
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "15",
            position = 1
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "40",
            position = 2
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "52",
            position = 3
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "70",
            position = 4
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "80",
            position = 5
        ),

        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "90",
            position = 6
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "F",
            position = 7
        ),
    )

    private fun getIronCheckpoints(distanceId: String) = listOf(
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "S",
            position = 0
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "7.5",
            position = 1
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "15",
            position = 1
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "40",
            position = 2
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "52",
            position = 3
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "70",
            position = 4
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "80",
            position = 5
        ),

        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "90",
            position = 6
        ),
        DistanceCheckpointPojo(
            id = UUID.randomUUID().toString(),
            distanceId = distanceId,
            name = "F",
            position = 7
        )
    )

    private suspend fun exportRunners(runners: List<Runner>) {
        Timber.i("Parse count ${runners.size}")
        var count = 0
        runners.forEach {
            api.saveRunner(it.toFirestoreRunner())
            count++
        }
        Timber.i("Saved count $count")
    }

    private fun getUUID(name: String): String{
        return "${name.replace(" ", "_")}_${UUID.randomUUID().toString().replace("-", "_")}"

    }
}