package com.gmail.maystruks08.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.gmail.maystruks08.data.local.dao.DistanceDAO
import com.gmail.maystruks08.data.local.dao.RunnerDao
import com.gmail.maystruks08.data.local.entity.relation.DistanceRunnerCrossRef
import com.gmail.maystruks08.data.local.entity.relation.RunnerResultCrossRef
import com.gmail.maystruks08.data.local.entity.tables.TeamNameTable
import com.gmail.maystruks08.data.mappers.toFirestoreRunner
import com.gmail.maystruks08.data.mappers.toResultTable
import com.gmail.maystruks08.data.mappers.toRunnerTable
import com.gmail.maystruks08.data.remote.Api
import com.gmail.maystruks08.domain.NetworkUtil
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointResultIml
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.exception.RunnerWithIdAlreadyExistException
import com.gmail.maystruks08.domain.repository.RegisterNewRunnersRepository
import javax.inject.Inject

class RegisterNewRunnersRepositoryImpl @Inject constructor(
    private val api: Api,
    private val runnerDao: RunnerDao,
    private val distanceDAO: DistanceDAO,
    private val networkUtil: NetworkUtil
) : RegisterNewRunnersRepository {

    override suspend fun saveNewRunners(raceId: String, distanceId: String, runners: List<Runner>) {
        runners.forEach { runner ->
            try {
                val runnerTable = runner.toRunnerTable(false)
                val resultTables = runner.currentCheckpoints
                    .filterIsInstance<CheckpointResultIml>()
                    .map { it.toResultTable(runnerTable.runnerNumber) }
                val runnerResultCrossRefTables = resultTables.map { RunnerResultCrossRef(runner.number, it.checkpointId) }
                val distanceRunnerCrossRefTables = mutableListOf(DistanceRunnerCrossRef(runner.actualDistanceId, runner.number))
                val teamNameTables = mutableListOf<TeamNameTable>().apply {
                    runner.currentTeamName?.let {
                        TeamNameTable(
                            distanceId = runner.actualDistanceId,
                            runnerId = runner.number,
                            name = it
                        )
                    }?.also { add(it) }
                }

                runnerDao.insertOrReplaceRunner(
                    runnerTable,
                    resultTables,
                    teamNameTables,
                    runnerResultCrossRefTables,
                    distanceRunnerCrossRefTables
                )
                if (networkUtil.isOnline()) {
                    api.saveRunner(runner.toFirestoreRunner())
                } else {
                    runnerDao.markAsNeedToSync(runnerNumber = runner.number, needToSync = true)
                }
            } catch (e: SQLiteConstraintException) {
                throw RunnerWithIdAlreadyExistException()
            } catch (e: Exception) {
                runners.forEach {
                    runnerDao.markAsNeedToSync(runnerNumber = it.number, needToSync = true)
                }
                throw e
            }
        }

        if (networkUtil.isOnline()) {
            val runnerIds = distanceDAO.getDistanceRunnersIds(distanceId)
            api.updateDistanceRunners(distanceId, runnerIds)
        }
    }
}