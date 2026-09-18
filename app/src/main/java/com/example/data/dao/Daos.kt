package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.CustomDatasetEntity
import com.example.data.entity.EmergencyContactEntity
import com.example.data.entity.RouteHistoryEntity
import com.example.data.entity.SosQueueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteHistoryDao {
    @Query("SELECT * FROM route_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<RouteHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(route: RouteHistoryEntity): Long

    @Query("DELETE FROM route_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM route_history")
    suspend fun clearAll()
}

@Dao
interface EmergencyContactDao {
    @Query("SELECT * FROM emergency_contacts ORDER BY isPrimary DESC, name ASC")
    fun getAllContacts(): Flow<List<EmergencyContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: EmergencyContactEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contacts: List<EmergencyContactEntity>)

    @Query("DELETE FROM emergency_contacts WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM emergency_contacts")
    suspend fun count(): Int
}

@Dao
interface SosQueueDao {
    @Query("SELECT * FROM sos_dispatch_queue ORDER BY timestamp DESC")
    fun getAllQueue(): Flow<List<SosQueueEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SosQueueEntity): Long

    @Query("UPDATE sos_dispatch_queue SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("DELETE FROM sos_dispatch_queue WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface CustomDatasetDao {
    @Query("SELECT * FROM custom_imported_nodes ORDER BY importedAt DESC")
    fun getAllCustomNodes(): Flow<List<CustomDatasetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(nodes: List<CustomDatasetEntity>)

    @Query("DELETE FROM custom_imported_nodes")
    suspend fun clearCustomNodes()

    @Query("SELECT COUNT(*) FROM custom_imported_nodes")
    suspend fun count(): Int
}
