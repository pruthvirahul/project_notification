package com.example.notificationfilter.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Query("SELECT * FROM notification_logs ORDER BY receivedAt DESC")
    fun getAllLogs(): Flow<List<NotificationLog>>

    @Insert
    suspend fun insertLog(log: NotificationLog)

    @Query("DELETE FROM notification_logs")
    suspend fun clearLogs()
}
