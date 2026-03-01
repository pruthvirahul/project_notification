package com.example.notificationfilter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "filter_rules")
data class FilterRule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val packageName: String?,
    val keyword: String?,
    val isRegex: Boolean = false,
    val isEnabled: Boolean = true,
    val action: String
)
