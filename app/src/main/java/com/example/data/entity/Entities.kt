package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_contacts")
data class EmergencyContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val relationOrAgency: String,
    val phoneNumber: String,
    val isPrimary: Boolean = false,
    val notes: String = ""
)

@Entity(tableName = "sos_dispatch_queue")
data class SosQueueEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val alertCode: String,
    val latitude: Double,
    val longitude: Double,
    val altitudeMeters: Int,
    val messageContent: String,
    val targetRecipient: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PREPARED_OFFLINE" // PREPARED_OFFLINE, SMS_OPENED, QUEUED
)

@Entity(tableName = "custom_imported_nodes")
data class CustomDatasetEntity(
    @PrimaryKey
    val nodeId: String,
    val name: String,
    val state: String,
    val lat: Double,
    val lon: Double,
    val elevationM: Int,
    val isAccessible: Boolean,
    val notes: String = "",
    val importedAt: Long = System.currentTimeMillis()
)
