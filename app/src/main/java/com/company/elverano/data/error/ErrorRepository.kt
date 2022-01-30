package com.company.elverano.data.error

import com.company.elverano.data.AppDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorRepository @Inject constructor(
    private val database: AppDatabase,
) {
    private val dao = database.errorDao()

    suspend fun insertErrorToDatabase(customError: CustomError) = dao.insertError(customError)

    suspend fun getErrorFromDB() = dao.getError()

    suspend fun deleteErrorFromDatabase() = dao.deleteError()
}