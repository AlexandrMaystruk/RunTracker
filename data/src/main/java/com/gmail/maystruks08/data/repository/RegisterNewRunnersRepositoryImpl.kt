package com.gmail.maystruks08.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.gmail.maystruks08.data.cache.ApplicationCache
import com.gmail.maystruks08.data.local.dao.RunnerDao
import com.gmail.maystruks08.data.remote.FirestoreApi
import com.gmail.maystruks08.domain.NetworkUtil
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.exception.RunnerWithIdAlreadyExistException
import com.gmail.maystruks08.domain.repository.RegisterNewRunnersRepository
import javax.inject.Inject

class RegisterNewRunnersRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi,
    private val runnerDao: RunnerDao,
    private val applicationCache: ApplicationCache,
    private val networkUtil: NetworkUtil
) : RegisterNewRunnersRepository {

    override suspend fun saveNewRunners(runners: List<Runner>) {
        try {
//            val runnersData = runners.map { it.toRunnerTable() to it.checkpoints.toCheckpointsResult(runnerNumber = it.number)  }
//            runnerDao.insertRunners(runnersData)
//            runners.forEach {
//                runnersCache.getRunnerList(it.type).add(it)
//                if(networkUtil.isOnline()) firestoreApi.updateRunner(it) else runnerDao.markAsNeedToSync(runnerNumber = it.number, needToSync = true)
//            }
        } catch (e: SQLiteConstraintException) {
            throw RunnerWithIdAlreadyExistException()
        } catch (e: Exception) {
            runners.forEach {
                runnerDao.markAsNeedToSync(
                    runnerNumber = it.number,
                    needToSync = true
                )
            }
            throw e
        }
    }
}