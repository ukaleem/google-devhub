package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "developer_perks")
data class PerkEntity(
    @PrimaryKey val id: String,
    val title: String,
    val provider: String,
    val valueDescription: String,
    val eligibilityCriteria: String,
    val benefitDetails: String,
    val trialPeriod: String,
    val claimedStatus: String = "ELIGIBLE", // "ELIGIBLE", "MATCHED", "CLAIMED"
    val docUrl: String
)
