package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.CustomDatasetDao
import com.example.data.dao.EmergencyContactDao
import com.example.data.dao.RouteHistoryDao
import com.example.data.dao.SosQueueDao
import com.example.data.entity.CustomDatasetEntity
import com.example.data.entity.EmergencyContactEntity
import com.example.data.entity.RouteHistoryEntity
import com.example.data.entity.SosQueueEntity

@Database(
    entities = [
        RouteHistoryEntity::class,
        EmergencyContactEntity::class,
        SosQueueEntity::class,
        CustomDatasetEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routeHistoryDao(): RouteHistoryDao
    abstract fun emergencyContactDao(): EmergencyContactDao
    abstract fun sosQueueDao(): SosQueueDao
    abstract fun customDatasetDao(): CustomDatasetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ner_access_offline.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
