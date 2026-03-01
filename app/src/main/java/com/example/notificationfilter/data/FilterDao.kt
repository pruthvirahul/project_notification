package com.example.notificationfilter.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FilterDao {
    @Query("SELECT * FROM filter_rules")
    fun getAllRules(): Flow<List<FilterRule>>

    @Insert
    suspend fun insertRule(rule: FilterRule)

    @Delete
    suspend fun deleteRule(rule: FilterRule)

    @Update
    suspend fun updateRule(rule: FilterRule)
}
