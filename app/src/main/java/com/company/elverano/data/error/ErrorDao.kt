package com.company.elverano.data.error

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.jetbrains.annotations.NotNull

@Dao
interface ErrorDao {
    @NotNull
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertError(customError: CustomError)

    @Query("SELECT * FROM weather_error")
    suspend fun getError(): CustomError?

    @Query("DELETE FROM weather_error")
    suspend fun deleteError()
}