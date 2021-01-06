package com.gmail.maystruks08.data.repository

import android.database.sqlite.SQLiteException
import com.gmail.maystruks08.data.awaitTaskCompletable
import com.gmail.maystruks08.data.cache.ApplicationCache
import com.gmail.maystruks08.data.local.ConfigPreferences
import com.gmail.maystruks08.data.local.dao.CheckpointDAO
import com.gmail.maystruks08.data.local.dao.DistanceDAO
import com.gmail.maystruks08.data.local.dao.RaceDAO
import com.gmail.maystruks08.data.local.dao.RunnerDao
import com.gmail.maystruks08.data.mappers.*
import com.gmail.maystruks08.data.remote.FirestoreApi
import com.gmail.maystruks08.domain.NetworkUtil
import com.gmail.maystruks08.domain.entities.RunnerChange
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointImpl
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.entities.runner.RunnerSex
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import com.gmail.maystruks08.domain.repository.RunnersRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flatMapConcat
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class RunnersRepositoryImpl @Inject constructor(
    private val networkUtil: NetworkUtil,
    private val firestoreApi: FirestoreApi,
    private val raceDAO: RaceDAO,
    private val distanceDAO: DistanceDAO,
    private val runnerDao: RunnerDao,
    private val checkpointDAO: CheckpointDAO,
    private val configPreferences: ConfigPreferences,
    private val applicationCache: ApplicationCache,
) : RunnersRepository, RunnerDataChangeListener {

    override suspend fun updateRunnerData(runner: Runner): Runner {
        try {
//            Timber.log(Log.INFO, "Saving runner data: ${runner.number} checkpoints:${runner.checkpoints.map { "${it.name} ${if (it is CheckpointResult) it.date.toDateTimeShortFormat() else ""}" }}")
//            runnerDao.insertOrReplaceRunner(runner.toRunnerTable(), runner.checkpoints.toCheckpointsResult(runner.number))
//            val index = runnersCache.getRunnerList(runner.type).indexOfFirst { it.number == runner.number }
//            if (index != -1) runnersCache.getRunnerList(runner.type).removeAt(index)
//            runnersCache.getRunnerList(runner.type).add(runner)
        } catch (e: SQLiteException) {
            Timber.e(e, "Saving runner ${runner.number} data to room error")
            e.printStackTrace()
            throw SaveRunnerDataException(runner.fullName)
        }
        if (networkUtil.isOnline()) {
            try {
                runnerDao.markAsNeedToSync(runner.number, false)
                awaitTaskCompletable(firestoreApi.updateRunner(runner))
            } catch (e: FirebaseFirestoreException) {
                Timber.e(e, "Saving runner ${runner.number} data to firestore error")
                runnerDao.markAsNeedToSync(runner.number, true)
                throw SyncWithServerException()
            }
        }
        return runner
    }

    override suspend fun getCheckpoints(distanceId: Long): List<CheckpointImpl> {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentCheckpoint(distanceId: Long): CheckpointImpl {
        TODO("Not yet implemented")
    }

    override suspend fun getRunners(
        distanceId: Long,
        onlyFinishers: Boolean,
        initSize: Int?
    ): List<Runner> {
//        if (applicationCache.race == null) {
//            val currentRaceId = configPreferences.getRaceId()
//            val race = raceDAO.getRace(currentRaceId)
//            applicationCache.race = race.toRaceEntity()
//
//
//            val runners = runnerDao.getRunnersWithResults(RunnerType.NORMAL.ordinal)
//            if (settingsCache.getCheckpointList(RunnerType.NORMAL).isEmpty()) {
//                settingsCache.checkpoints =
//                    runnerDao.getCheckpoints(CheckpointType.NORMAL.ordinal).toCheckpoints()
//            }
//            runners.forEach {
//                val runner = it.toRunner(settingsCache.checkpoints)
//                applicationCache.normalRunnersList.add(runner)
//            }
//        }
//        val result =
//            applicationCache.normalRunnersList.run { if (onlyFinishers) filter { it.totalResult != null && !it.isOffTrack } else this }
//        return result.let {
//            if (initSize != null) {
//                try {
//                    it.take(initSize)
//                } catch (e: IllegalArgumentException) {
//                    it.toMutableList()
//                }
//            } else it.toMutableList()
//        }


        return mutableListOf(
            Runner(
                1,
                "q12e",
                "Full name",
                "Short name",
                "34r5345",
                RunnerSex.MALE,
                "Odessa",
                Date(),
                0,
                listOf(0),
                mutableListOf(),
                false,
                null,
                null
            ),
            Runner(
                1,
                "q12e",
                "Full name",
                "Short name",
                "34r5345",
                RunnerSex.MALE,
                "Odessa",
                Date(),
                0,
                listOf(0),
                mutableListOf(),
                false,
                null,
                null
            ),
            Runner(
                2,
                "q12e",
                "Full name",
                "Short name",
                "34r5345",
                RunnerSex.MALE,
                "Odessa",
                Date(),
                0,
                listOf(0),
                mutableListOf(),
                false,
                null,
                null
            ),
            Runner(
                3,
                "q12e",
                "Full name",
                "Short name",
                "34r5345",
                RunnerSex.MALE,
                "Odessa",
                Date(),
                0,
                listOf(0),
                mutableListOf(),
                false,
                null,
                null
            ),
            Runner(
                1,
                "q12e",
                "Full name",
                "Short name",
                "34r5345",
                RunnerSex.MALE,
                "Odessa",
                Date(),
                0,
                listOf(0),
                mutableListOf(),
                false,
                null,
                null
            ),
            Runner(
                4,
                "q12e",
                "Full name",
                "Short name",
                "34r5345",
                RunnerSex.MALE,
                "Odessa",
                Date(),
                0,
                listOf(0),
                mutableListOf(),
                false,
                null,
                null
            ),
            Runner(
                5,
                "q12e",
                "Full name",
                "Short name",
                "34r5345",
                RunnerSex.MALE,
                "Odessa",
                Date(),
                0,
                listOf(0),
                mutableListOf(),
                false,
                null,
                null
            ),
            Runner(
                6,
                "q12e",
                "Full name",
                "Short name",
                "34r5345",
                RunnerSex.MALE,
                "Odessa",
                Date(),
                0,
                listOf(0),
                mutableListOf(),
                false,
                null,
                null
            ),
            Runner(
                7,
                "q12e",
                "Full name",
                "Short name",
                "34r5345",
                RunnerSex.MALE,
                "Odessa",
                Date(),
                0,
                listOf(0),
                mutableListOf(),
                false,
                null,
                null
            ),
            Runner(
                7,
                "q12e",
                "Full name",
                "Short name",
                "34r5345",
                RunnerSex.MALE,
                "Odessa",
                Date(),
                0,
                listOf(0),
                mutableListOf(),
                false,
                null,
                null
            ),
            Runner(
                8,
                "q12e",
                "Full name",
                "Short name",
                "34r5345",
                RunnerSex.MALE,
                "Odessa",
                Date(),
                0,
                listOf(0),
                mutableListOf(),
                false,
                null,
                null
            ),
            Runner(
                9,
                "q12e",
                "Full name",
                "Short name",
                "34r5345",
                RunnerSex.MALE,
                "Odessa",
                Date(),
                0,
                listOf(0),
                mutableListOf(),
                false,
                null,
                null
            ),
            Runner(
                10,
                "q12e",
                "Full name",
                "Short name",
                "34r5345",
                RunnerSex.MALE,
                "Odessa",
                Date(),
                0,
                listOf(0),
                mutableListOf(),
                false,
                null,
                null
            ),
            Runner(
                11,
                "q12e",
                "Full name",
                "Short name",
                "34r5345",
                RunnerSex.MALE,
                "Odessa",
                Date(),
                0,
                listOf(0),
                mutableListOf(),
                false,
                null,
                null
            ),
        )
    }

    override suspend fun getRunnerByCardId(cardId: String): Runner? {
        return null
    }

    override suspend fun getRunnerByNumber(runnerNumber: Long): Runner? {
        return null
    }

    override suspend fun getRunnerTeamMembers(
        currentRunnerNumber: Long,
        teamName: String
    ): List<Runner>? {
        return null
    }


    private suspend fun checkIsDataUploaded(runnerNumber: Long): Boolean {
        return runnerDao.checkNeedToSync(runnerNumber) == null
    }

    private suspend fun insertRunner(runner: Runner) {
//        val runnerTable = runner.toRunnerTable(false)
//        val resultTables = runner.checkpoints.toCheckpointsResult(runner.number)
//        val index = runnersCache.getRunnerList(runner.type).indexOfFirst { it.number == runner.number }
//        if (index != -1) {
//            runnerDao.updateRunner(runnerTable, resultTables)
//            runnersCache.getRunnerList(runner.type).removeAt(index)
//            runnersCache.getRunnerList(runner.type).add(index, runner)
//        } else {
//            runnerDao.insertOrReplaceRunner(runnerTable, resultTables)
//            runnersCache.getRunnerList(runner.type).add(runner)
//        }
    }

    private suspend fun updateRunner(runner: Runner) {
//        val index = runnersCache.getRunnerList(runner.type).indexOfFirst { it.number == runner.number }
//        if (index != -1) {
//            runnerDao.updateRunner(runner.toRunnerTable(false), runner.checkpoints.toCheckpointsResult(runner.number))
//            runnersCache.getRunnerList(runner.type).removeAt(index)
//            runnersCache.getRunnerList(runner.type).add(index, runner)
//        }
    }

    private suspend fun deleteRunner(runner: Runner) {
//        val count = runnerDao.delete(runner.number)
//        val isRemoved = runnersCache.getRunnerList(runner.type).removeAll { it.number == runner.number }
//        Timber.i("Removed runner from DB count: $count, from cache removed: $isRemoved")
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override suspend fun observeRunnerData(): Flow<RunnerChange> {
        return firestoreApi.subscribeToRunnerDataRealtimeUpdates()
            .flatMapConcat { runnerChangeList ->
                runnerChangeList.forEach {
//                val canRewriteLocalCache = checkIsDataUploaded(it.runner.number)
//                val canRewriteLocalCache = checkIsDataUploaded(it.runner.number)
//
//                if (canRewriteLocalCache) {
//                    when (it.changeType) {
//                        Change.ADD -> insertRunner(it.runner)
//                        Change.UPDATE -> updateRunner(it.runner)
//                        Change.REMOVE -> deleteRunner(it.runner)
//                    }
//                }
                }
                return@flatMapConcat channelFlow { runnerChangeList.forEach { offer(it) } }
            }
    }


    override suspend fun getLastSavedRaceId(): TaskResult<Exception, Long> {
        return TaskResult.build {
            val lastSelectedRaceId = configPreferences.getRaceId()
            if (lastSelectedRaceId == -1L) {
                throw Exception("First start. Race not selected yet")
            } else {
                lastSelectedRaceId
            }
        }
    }
}