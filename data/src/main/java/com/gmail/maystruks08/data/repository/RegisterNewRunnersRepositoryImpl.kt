package com.gmail.maystruks08.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.gmail.maystruks08.data.local.dao.RunnerDao
import com.gmail.maystruks08.data.mappers.toResultTable
import com.gmail.maystruks08.data.mappers.toRunnerTable
import com.gmail.maystruks08.data.remote.FirestoreApi
import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.exception.RunnerWithIdAlreadyExistException
import com.gmail.maystruks08.domain.repository.RegisterNewRunnersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RegisterNewRunnersRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi,
    private val runnerDao: RunnerDao
) : RegisterNewRunnersRepository {

    override suspend fun saveNewRunner(runner: Runner): ResultOfTask<Exception, Unit> {
        return try {
            withContext(Dispatchers.IO) {
                runnerDao.insertRunner(
                    runner.toRunnerTable(),
                    runner.checkpoints.map { it.toResultTable(runner.id) }
                )
                firestoreApi.updateRunner(runner)
            }
            ResultOfTask.build { }
        } catch (e: SQLiteConstraintException) {
            ResultOfTask.build { throw RunnerWithIdAlreadyExistException() }
        } catch (e: Exception) {
            withContext(Dispatchers.IO) {
                runnerDao.markAsNeedToSync(runnerId = runner.id, needToSync = true)
            }
            ResultOfTask.build { throw e }
        }
    }
}