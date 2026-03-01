package com.example.notificationfilter

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notificationfilter.ui.DashboardScreen
import com.example.notificationfilter.ui.RulesScreen

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // This state will not re-check dynamically unless using lifecycle observers, 
                    // but enough for basic checks upon recomposition or onResume updating a state
                    var hasPermission by remember { mutableStateOf(isNotificationServiceEnabled(this)) }
                    
                    if (!hasPermission) {
                        PermissionScreen(onGrantPermissionClick = {
                            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                            try {
                                startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                // Fallback
                            }
                        })
                    } else {
                        MainAppScreen(viewModel)
                    }
                }
            }
        }
    }

    private fun isNotificationServiceEnabled(context: Context): Boolean {
        val pkgName = context.packageName
        val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        if (!TextUtils.isEmpty(flat)) {
            val names = flat.split(":")
            for (name in names) {
                val cn = ComponentName.unflattenFromString(name)
                if (cn != null && TextUtils.equals(pkgName, cn.packageName)) {
                    return true
                }
            }
        }
        return false
    }
}

@Composable
fun PermissionScreen(onGrantPermissionClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Notification Access Required", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Text("To filter and display notifications, this app requires special permission. Click the button to go to settings and allow 'Notification Access' for NotificationFilter.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onGrantPermissionClick) {
            Text("Grant Permission")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()
    var currentRoute by remember { mutableStateOf("dashboard") }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == "dashboard",
                    onClick = { 
                        currentRoute = "dashboard"
                        navController.navigate("dashboard") { launchSingleTop = true; popUpTo("dashboard") } 
                    },
                    icon = { Text("📝") },
                    label = { Text("Logs") }
                )
                NavigationBarItem(
                    selected = currentRoute == "rules",
                    onClick = { 
                        currentRoute = "rules"
                        navController.navigate("rules") { launchSingleTop = true; popUpTo("dashboard") } 
                    },
                    icon = { Text("⚙️") },
                    label = { Text("Rules") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = "dashboard", modifier = Modifier.padding(innerPadding)) {
            composable("dashboard") { DashboardScreen(viewModel) }
            composable("rules") { RulesScreen(viewModel) }
        }
    }
}
