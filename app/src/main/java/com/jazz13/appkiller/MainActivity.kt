package com.jazz13.appkiller

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.jazz13.appkiller.data.AppInfo
import com.jazz13.appkiller.data.AppRepository
import com.jazz13.appkiller.ui.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppKillerApp()
        }
    }
}

@Composable
fun AppKillerApp() {
    val fkGroteskNeue = FontFamily(
        Font(R.font.fkgroteskneuetrial_light_bf6576818c0f3e8, FontWeight.Light),
        Font(R.font.fkgroteskneuetrial_regular_bf6576818c3af74, FontWeight.Normal),
        Font(R.font.fkgroteskneuetrial_medium_bf6576818c3a00a, FontWeight.Medium),
        Font(R.font.fkgroteskneuetrial_bold_bf6576818bd3700, FontWeight.Bold)
    )
    
    val darkColors = darkColorScheme(
        background = Color(0xFF1C1C1E),
        surface = Color(0xFF2C2C2E),
        primary = Color(0xFF007AFF),
        onBackground = Color(0xFFFFFFFF),
        onSurface = Color(0xFFFFFFFF)
    )
    
    val context = LocalContext.current
    val viewModel: MainViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(
                    AppRepository(context),
                    com.jazz13.appkiller.settings.AppSettings(context),
                    com.jazz13.appkiller.system.SystemMonitor(context)
                ) as T
            }
        }
    )
    
    val showLogScreen by viewModel.showLogScreen.collectAsState()
    val showSystemStats by viewModel.showSystemStats.collectAsState()
    val killLogs by viewModel.killLogs.collectAsState()
    val systemStatsHistory by viewModel.systemStatsHistory.collectAsState()
    
    MaterialTheme(colorScheme = darkColors) {
        when {
            showLogScreen -> {
                com.jazz13.appkiller.ui.LogScreen(
                    logs = killLogs,
                    fontFamily = fkGroteskNeue,
                    onBackClick = { viewModel.hideLogScreen() }
                )
            }
            showSystemStats -> {
                com.jazz13.appkiller.ui.SystemStatsScreen(
                    statsHistory = systemStatsHistory,
                    fontFamily = fkGroteskNeue,
                    onBackClick = { viewModel.hideSystemStats() }
                )
            }
            else -> {
                AppListScreen(fontFamily = fkGroteskNeue, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AppListScreen(fontFamily: FontFamily, viewModel: MainViewModel) {
    val context = LocalContext.current
    
    val apps by viewModel.apps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val needsUsagePermission by viewModel.needsUsagePermission.collectAsState()
    val needsDeviceAdmin by viewModel.needsDeviceAdmin.collectAsState()
    val killBehavior by viewModel.killBehavior.collectAsState()
    
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)
    
    LaunchedEffect(Unit) {
        viewModel.loadApps()
        viewModel.startStatsCollection()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AppKiller",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = fontFamily,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // System stats icon
                IconButton(onClick = { viewModel.showSystemStats() }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "System Statistics",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                // Log icon
                IconButton(onClick = { viewModel.showLogScreen() }) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "View Logs",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                Text(
                    text = if (killBehavior == com.jazz13.appkiller.settings.AppSettings.KILL_BEHAVIOR_AUTO) "Auto" else "Manual",
                    fontSize = 14.sp,
                    fontFamily = fontFamily,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Switch(
                    checked = killBehavior == com.jazz13.appkiller.settings.AppSettings.KILL_BEHAVIOR_MANUAL,
                    onCheckedChange = { isChecked ->
                        viewModel.setKillBehavior(
                            if (isChecked) com.jazz13.appkiller.settings.AppSettings.KILL_BEHAVIOR_MANUAL 
                            else com.jazz13.appkiller.settings.AppSettings.KILL_BEHAVIOR_AUTO
                        )
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = Color.Gray
                    )
                )
            }
        }
        
        if (needsUsagePermission) {
            PermissionCard(
                title = "Usage Access Required",
                description = "Grant Usage Access permission to detect running apps accurately.",
                buttonText = "Grant Usage Access",
                fontFamily = fontFamily,
                onGrantPermission = {
                    context.startActivity(viewModel.getUsagePermissionIntent())
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        
        if (needsDeviceAdmin) {
            PermissionCard(
                title = "Device Admin Required",
                description = "Enable Device Admin to automatically force stop applications.",
                buttonText = "Enable Device Admin",
                fontFamily = fontFamily,
                onGrantPermission = {
                    context.startActivity(viewModel.getDeviceAdminIntent())
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.loadApps() },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                items(apps) { app ->
                    AppListItem(
                        app = app, 
                        fontFamily = fontFamily,
                        onKillApp = { packageName ->
                            if (killBehavior == com.jazz13.appkiller.settings.AppSettings.KILL_BEHAVIOR_AUTO) {
                                viewModel.killApp(packageName)
                            } else {
                                viewModel.openAppSettings(packageName)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PermissionCard(
    title: String,
    description: String,
    buttonText: String,
    fontFamily: FontFamily, 
    onGrantPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = fontFamily,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                fontFamily = fontFamily,
                color = Color(0xFF8E8E93)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onGrantPermission,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = buttonText,
                    fontFamily = fontFamily,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.material.ExperimentalMaterialApi::class)
@Composable
fun AppListItem(app: AppInfo, fontFamily: FontFamily, onKillApp: (String) -> Unit) {
    val context = LocalContext.current
    val dismissState = rememberDismissState { dismissValue ->
        if (dismissValue == DismissValue.DismissedToStart) {
            Toast.makeText(context, "Killing ${app.appName}", Toast.LENGTH_SHORT).show()
            onKillApp(app.packageName)
        }
        false // Don't actually dismiss, just show toast and kill
    }
    
    // Calculate if swipe is past 40% threshold based on progress
    val swipeProgress = dismissState.progress.fraction
    val isPastThreshold = swipeProgress >= 0.4f
    val backgroundColor = if (isPastThreshold) Color(0xFFFF3B30) else Color(0xFFFF9500) // Red vs Orange
    
    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart), // Only left swipe
        modifier = Modifier.padding(vertical = 4.dp),
        dismissThresholds = { FractionalThreshold(0.4f) }, // 40% threshold
        background = {
            Card(
                modifier = Modifier.fillMaxSize(),
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                shape = CardDefaults.shape
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Kill app",
                        tint = Color.White,
                        modifier = Modifier.padding(end = 24.dp)
                    )
                }
            }
        },
        dismissContent = {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        bitmap = app.icon.toBitmap(
                            com.jazz13.appkiller.settings.AppSettings.ICON_BITMAP_SIZE, 
                            com.jazz13.appkiller.settings.AppSettings.ICON_BITMAP_SIZE
                        ).asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.size(com.jazz13.appkiller.settings.AppSettings.ICON_DISPLAY_SIZE.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = app.appName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = fontFamily,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = app.packageName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light,
                            fontFamily = fontFamily,
                            color = Color(0xFF8E8E93)
                        )
                    }
                    
                    Text(
                        text = if (app.isRunning) "Running" else "Stopped",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = fontFamily,
                        color = if (app.isRunning) Color(0xFF34C759) else Color(0xFFFF3B30)
                    )
                }
            }
        }
    )
}
