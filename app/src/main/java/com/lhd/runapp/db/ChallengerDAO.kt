package com.lhd.runapp.db

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.lhd.runapp.models.Challenger
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengerDAO {
//    @WorkerThread
//    @Query("SELECT * FROM challenger")
//    suspend fun getAll(): Flow<MutableList<Challenger>>

    @WorkerThread
    @Query("SELECT * FROM challenger WHERE name LIKE :name LIMIT 1")
    suspend fun findByName(name: String): Challenger

    @WorkerThread
    @Query("SELECT * FROM challenger")
    suspend fun getAll(): List<Challenger>

    @WorkerThread
    @Insert
    suspend fun insertAll(vararg challengers: Challenger)

    @WorkerThread
    @Insert
    suspend fun insert(challenger: Challenger)

    @WorkerThread
    @Query("Delete FROM challenger")
    suspend fun delete()
}