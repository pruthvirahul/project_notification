package com.example.notificationfilter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notificationfilter.data.AppDatabase
import com.example.notificationfilter.data.FilterRule
import com.example.notificationfilter.data.NotificationLog
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val filterDao = database.filterDao()
    private val logDao = database.logDao()

    val rules: StateFlow<List<FilterRule>> = filterDao.getAllRules()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val logs: StateFlow<List<NotificationLog>> = logDao.getAllLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addRule(packageName: String, keyword: String, isRegex: Boolean, action: String) {
        viewModelScope.launch {
            filterDao.insertRule(
                FilterRule(
                    packageName = packageName.ifBlank { null },
                    keyword = keyword.ifBlank { null },
                    isRegex = isRegex,
                    isEnabled = true,
                    action = action
                )
            )
        }
    }

    fun toggleRule(rule: FilterRule) {
        viewModelScope.launch {
            filterDao.updateRule(rule.copy(isEnabled = !rule.isEnabled))
        }
    }

    fun deleteRule(rule: FilterRule) {
        viewModelScope.launch {
            filterDao.deleteRule(rule)
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            logDao.clearLogs()
        }
    }
}
