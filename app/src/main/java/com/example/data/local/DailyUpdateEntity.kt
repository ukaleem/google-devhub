package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_updates")
data class DailyUpdateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String,
    val description: String,
    val techImpact: String,
    val releaseDate: String,
    val isRead: Boolean = false,
    val isAlertSimulated: Boolean = false
)
