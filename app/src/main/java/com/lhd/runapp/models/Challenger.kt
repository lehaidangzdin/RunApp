package com.lhd.runapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenger")
data class Challenger(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val img: Int,
    val name: String,
    val date: Long,
    val progress: Float,
    val max: Float
)