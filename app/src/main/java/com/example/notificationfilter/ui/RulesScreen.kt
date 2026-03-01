package com.example.notificationfilter.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notificationfilter.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesScreen(viewModel: MainViewModel) {
    val rules by viewModel.rules.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var inputPackageName by remember { mutableStateOf("") }
    var inputKeyword by remember { mutableStateOf("") }
    var inputIsRegex by remember { mutableStateOf(false) }
    var selectedAction by remember { mutableStateOf("DISMISS") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Active Rules", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = { showDialog = true }) {
                Text("Add Rule")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (rules.isEmpty()) {
            Text("No filtering rules added.")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(rules) { rule ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (rule.isEnabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Package: ${rule.packageName ?: "ANY"}", style = MaterialTheme.typography.bodyMedium)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "Keyword: ${rule.keyword ?: "ANY"}", style = MaterialTheme.typography.bodyMedium)
                                    if (rule.isRegex) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Badge { Text("Regex") }
                                    }
                                }
                                Text(text = "Action: ${rule.action}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = rule.isEnabled,
                                    onCheckedChange = { viewModel.toggleRule(rule) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(onClick = { viewModel.deleteRule(rule) }) {
                                    Text("❌")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add New Rule") },
            text = {
                Column {
                    OutlinedTextField(
                        value = inputPackageName,
                        onValueChange = { inputPackageName = it },
                        label = { Text("Package (e.g., com.whatsapp)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputKeyword,
                        onValueChange = { inputKeyword = it },
                        label = { Text("Keyword (e.g., ^Promo.*)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = inputIsRegex,
                            onCheckedChange = { inputIsRegex = it }
                        )
                        Text("Use Regex for Keyword")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Action", style = MaterialTheme.typography.labelMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedAction == "DISMISS",
                            onClick = { selectedAction = "DISMISS" }
                        )
                        Text("Dismiss", modifier = Modifier.padding(end = 16.dp))
                        RadioButton(
                            selected = selectedAction == "SAVE",
                            onClick = { selectedAction = "SAVE" }
                        )
                        Text("Save Only")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addRule(inputPackageName, inputKeyword, inputIsRegex, selectedAction)
                        showDialog = false
                        inputPackageName = ""
                        inputKeyword = ""
                        inputIsRegex = false
                        selectedAction = "DISMISS"
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
