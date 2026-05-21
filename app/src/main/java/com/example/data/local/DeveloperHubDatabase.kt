package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ToolEntity::class, DailyUpdateEntity::class, PerkEntity::class, BountyEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DeveloperHubDatabase : RoomDatabase() {
    abstract fun toolDao(): ToolDao

    companion object {
        @Volatile
        private var INSTANCE: DeveloperHubDatabase? = null

        fun getDatabase(context: Context): DeveloperHubDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DeveloperHubDatabase::class.java,
                    "developer_hub_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
