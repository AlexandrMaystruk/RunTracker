package com.gmail.maystruks08.data.repository

import com.gmail.maystruks08.data.XLSParser
import com.gmail.maystruks08.data.local.ConfigPreferences
import com.gmail.maystruks08.data.mappers.toFirestoreRunner
import com.gmail.maystruks08.data.remote.Api
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
        val runners = xlsParser.readExcelFileFromAssets(raceId, "runners_$uniqueId", "100_24_run.xls").toMutableList().apply {
            val runnersGroup =  xlsParser.readExcelFileFromAssets(raceId, "runners_$uniqueId", "100_24_run_group.xls")
            addAll(runnersGroup)
        }

        val iron = xlsParser.readExcelFileFromAssets(raceId, "iron_$uniqueId",  "100_24_iron_run.xls")
        val relayRace = xlsParser.readExcelFileFromAssets(raceId, "relay_race_$uniqueId", "100_24_estaf.xls")
        val scandinavians = xlsParser.readExcelFileFromAssets(raceId, "scandinavians_$uniqueId",  "100_24_scandinav.xls")

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
                id = "scandinavians_$uniqueId",
                raceId = raceId,
                name = "Скандинавы",
                authorId = authorId,
                dateOfStart = null,
                runnerIds = scandinavians.map { it.number }
            ),
        )

        runners.forEach { it.distanceIds.add(distances[0].id) }
        iron.forEach { it.distanceIds.add(distances[1].id) }
        relayRace.forEach { it.distanceIds.add(distances[2].id) }
        scandinavians.forEach { it.distanceIds.add(distances[3].id) }

        val race = RacePojo(
            id = raceId,
            name = raceName,
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
        exportRunners(scandinavians)
    }

    private suspend fun exportRunners(runners: List<Runner>){
        Timber.i("Parse count ${runners.size}")
        var count = 0
        runners.forEach {
            api.saveRunner(it.toFirestoreRunner())
            count++
        }
        Timber.i("Saved count $count")
    }

    fun getUUID(name: String): String{
        return "${name.replace(" ", "_")}_${UUID.randomUUID().toString().replace("-", "_")}"

    }
}