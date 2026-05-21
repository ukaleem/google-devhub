package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "developer_tools")
data class ToolEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val shortDescription: String,
    val fullDescription: String,
    val isFavorite: Boolean = false,
    val docUrl: String,
    val apiDocSnippet: String,
    val tierPerks: String
)
