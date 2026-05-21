package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "open_source_bounties")
data class BountyEntity(
    @PrimaryKey val id: String,
    val title: String,
    val project: String,
    val reward: Double,
    val difficulty: String, // "Easy", "Medium", "Hard"
    val category: String, // "Documentation", "Bug Fix", "Feature", "Performance"
    val skills: String, // e.g., "Kotlin, Compose", "Python, Vertex AI"
    val description: String,
    val requirements: String, // Comma-separated or bullet points
    var status: String = "OPEN", // "OPEN", "CLAIMED", "SUBMITTED", "COMPLETED"
    var prUrl: String = "",
    var submissionNotes: String = ""
)
