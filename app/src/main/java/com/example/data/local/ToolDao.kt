package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolDao {
    // ---- Developer Tools Queries ----
    @Query("SELECT * FROM developer_tools")
    fun getAllTools(): Flow<List<ToolEntity>>

    @Query("SELECT * FROM developer_tools WHERE isFavorite = 1")
    fun getFavoriteTools(): Flow<List<ToolEntity>>

    @Query("SELECT * FROM developer_tools WHERE id = :toolId LIMIT 1")
    suspend fun getToolById(toolId: String): ToolEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTools(tools: List<ToolEntity>)

    @Query("UPDATE developer_tools SET isFavorite = :isFav WHERE id = :toolId")
    suspend fun updateFavoriteStatus(toolId: String, isFav: Boolean)

    // ---- Daily Updates Queries ----
    @Query("SELECT * FROM daily_updates ORDER BY id DESC")
    fun getDailyUpdates(): Flow<List<DailyUpdateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyUpdates(updates: List<DailyUpdateEntity>)

    @Query("UPDATE daily_updates SET isRead = 1 WHERE id = :updateId")
    suspend fun markUpdateAsRead(updateId: Int)

    // ---- Developer Perks Queries ----
    @Query("SELECT * FROM developer_perks")
    fun getAllPerks(): Flow<List<PerkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerks(perks: List<PerkEntity>)

    @Query("UPDATE developer_perks SET claimedStatus = :status WHERE id = :perkId")
    suspend fun updatePerkStatus(perkId: String, status: String)
}
