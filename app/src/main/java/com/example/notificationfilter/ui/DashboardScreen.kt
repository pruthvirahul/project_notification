package com.example.notificationfilter.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.notificationfilter.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val logs by viewModel.logs.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    val totalLogs = logs.size
    val dismissedCount = logs.count { it.actionTaken == "DISMISSED" }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Notification Logs", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = { viewModel.clearLogs() }) {
                Text("Clear")
            }
        }
        
        // Stats Summary
        if (totalLogs > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "$totalLogs", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text(text = "Total Intercepted", style = MaterialTheme.typography.labelMedium)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "$dismissedCount", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        Text(text = "Auto-Dismissed", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        if (logs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No notifications intercepted yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(logs) { log ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (log.actionTaken == "DISMISSED") MaterialTheme.colorScheme.errorContainer.copy(alpha=0.3f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = log.appName, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                if (log.appName != log.packageName) {
                                    Text(text = log.packageName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = log.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                if (log.text.isNotBlank()) {
                                    Text(text = log.text, style = MaterialTheme.typography.bodyMedium)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                val dateString = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(log.receivedAt))
                                Text(
                                    text = "$dateString • ${log.actionTaken}", 
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (log.actionTaken == "DISMISSED") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = { 
                                    val textToCopy = "App: ${log.appName}\nTitle: ${log.title}\nBody: ${log.text}"
                                    clipboardManager.setText(AnnotatedString(textToCopy)) 
                                }
                            ) {
                                Text("📋")
                            }
                        }
                    }
                }
            }
        }
    }
}
