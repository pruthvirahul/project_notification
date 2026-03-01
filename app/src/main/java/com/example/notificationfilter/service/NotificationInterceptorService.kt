package com.example.notificationfilter.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.notificationfilter.data.AppDatabase
import com.example.notificationfilter.data.NotificationLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class NotificationInterceptorService : NotificationListenerService() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return
        
        // Ignore persistent/ongoing notifications (like music players, step counters)
        if (sbn.isOngoing) return

        val packageName = sbn.packageName ?: ""
        val extras = sbn.notification.extras
        val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""

        // Resolve human-readable App Name
        val pm = applicationContext.packageManager
        val appName = try {
            val appInfo = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            packageName
        }

        // Ignore our own notifications
        if (packageName == applicationContext.packageName) return
        
        // Log to logcat for debugging easily
        Log.d("NotificationService", "Received notification from $packageName: $title - $text")

        scope.launch {
            val rules = database.filterDao().getAllRules().firstOrNull() ?: emptyList()
            var matchedAction = "SAVED"
            var shouldDismiss = false

            for (rule in rules) {
                if (!rule.isEnabled) continue

                val matchesPackage = rule.packageName.isNullOrEmpty() || rule.packageName == packageName
                
                val matchesKeyword = if (rule.keyword.isNullOrEmpty()) {
                    true
                } else if (rule.isRegex) {
                    try {
                        val regex = Regex(rule.keyword, RegexOption.IGNORE_CASE)
                        regex.containsMatchIn(text) || regex.containsMatchIn(title)
                    } catch (e: Exception) {
                        false // Skip matching if regex is invalid
                    }
                } else {
                    text.contains(rule.keyword, ignoreCase = true) || title.contains(rule.keyword, ignoreCase = true)
                }

                if (matchesPackage && matchesKeyword && (!rule.packageName.isNullOrEmpty() || !rule.keyword.isNullOrEmpty())) {
                    if (rule.action == "DISMISS") {
                        shouldDismiss = true
                        matchedAction = "DISMISSED"
                    }
                    break
                }
            }

            if (shouldDismiss) {
                try {
                    cancelNotification(sbn.key)
                    Log.d("NotificationService", "Dismissed notification from $packageName")
                } catch (e: Exception) {
                    Log.e("NotificationService", "Failed to cancel notification", e)
                }
            }

            // Save log
            val log = NotificationLog(
                packageName = packageName,
                appName = appName,
                title = title,
                text = text,
                receivedAt = System.currentTimeMillis(),
                actionTaken = matchedAction
            )
            database.logDao().insertLog(log)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
