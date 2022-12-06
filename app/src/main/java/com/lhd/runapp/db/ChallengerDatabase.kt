package com.lhd.runapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lhd.runapp.models.Challenger

const val DB_NAME = "Challenger_Steps"
const val DB_VERSION = 1

@Database(entities = [Challenger::class], version = DB_VERSION, exportSchema = true)
abstract class ChallengerDatabase : RoomDatabase() {
    abstract val challengerDao: ChallengerDAO

    companion object {
        @Volatile
        private var INSTANCE: ChallengerDatabase? = null

        fun getInstanceDB(context: Context): ChallengerDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ChallengerDatabase::class.java,
                        DB_NAME
                    ).build()

                }
                return instance
            }
        }
    }

}
