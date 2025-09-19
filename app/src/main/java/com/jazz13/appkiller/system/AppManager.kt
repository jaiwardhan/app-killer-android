package com.jazz13.appkiller.system

import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.app.usage.UsageStatsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import com.jazz13.appkiller.data.AppInfo

class AppManager(private val context: Context) {
    private val packageManager = context.packageManager
    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    private val permissionManager = PermissionManager(context)
    
    fun getInstalledApps(): List<AppInfo> {
        val launcherIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        
        val launcherApps = packageManager.queryIntentActivities(launcherIntent, 0)
        val runningApps = getRunningAppPackages()
        
        return launcherApps
            .map { resolveInfo ->
                val packageName = resolveInfo.activityInfo.packageName
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                
                AppInfo(
                    packageName = packageName,
                    appName = packageManager.getApplicationLabel(appInfo).toString(),
                    icon = getHighDensityIcon(appInfo),
                    isRunning = runningApps.contains(packageName)
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.appName }
    }
    
    fun killApp(packageName: String): Boolean {
        return try {
            // Don't kill our own app
            if (packageName == context.packageName) {
                return false
            }
            
            var success = false
            
            // Method 1: Kill background processes
            activityManager.killBackgroundProcesses(packageName)
            success = true
            
            // Method 2: Use shell command to force stop (most effective)
            try {
                val process = Runtime.getRuntime().exec("am force-stop $packageName")
                process.waitFor()
                success = true
            } catch (e: Exception) {
                // Continue with other methods
            }
            
            // Method 3: Kill all processes with the package name
            try {
                val runningProcesses = activityManager.runningAppProcesses
                runningProcesses?.forEach { processInfo ->
                    if (processInfo.processName.startsWith(packageName)) {
                        android.os.Process.killProcess(processInfo.pid)
                        success = true
                    }
                }
            } catch (e: Exception) {
                // Continue
            }
            
            // Method 4: Remove from recent tasks (Android 5.0+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    val recentTasks = activityManager.appTasks
                    recentTasks.forEach { task ->
                        try {
                            val taskInfo = task.taskInfo
                            if (taskInfo.baseActivity?.packageName == packageName) {
                                task.finishAndRemoveTask()
                                success = true
                            }
                        } catch (e: Exception) {
                            // Continue with other tasks
                        }
                    }
                } catch (e: Exception) {
                    // Continue
                }
            }
            
            // Method 5: Send KILL signal using shell (requires root but try anyway)
            try {
                val process = Runtime.getRuntime().exec("pkill -f $packageName")
                process.waitFor()
                success = true
            } catch (e: Exception) {
                // Ignore
            }
            
            success
        } catch (e: Exception) {
            false
        }
    }
    
    fun openAppSettings(packageName: String) {
        try {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = android.net.Uri.parse("package:$packageName")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Ignore if can't open settings
        }
    }
    
    private fun getHighDensityIcon(appInfo: ApplicationInfo): Drawable {
        return try {
            // Try to get high-density icon
            val resources = packageManager.getResourcesForApplication(appInfo)
            val iconId = appInfo.icon
            
            if (iconId != 0) {
                // Try to get the highest density icon available
                val drawable = resources.getDrawableForDensity(
                    iconId, 
                    DisplayMetrics.DENSITY_XXXHIGH, // 640dpi
                    null
                ) ?: resources.getDrawableForDensity(
                    iconId,
                    DisplayMetrics.DENSITY_XXHIGH, // 480dpi  
                    null
                ) ?: resources.getDrawableForDensity(
                    iconId,
                    DisplayMetrics.DENSITY_XHIGH, // 320dpi
                    null
                ) ?: packageManager.getApplicationIcon(appInfo)
                
                drawable
            } else {
                packageManager.getApplicationIcon(appInfo)
            }
        } catch (e: Exception) {
            // Fallback to default icon loading
            packageManager.getApplicationIcon(appInfo)
        }
    }
    
    fun isDeviceAdminEnabled(): Boolean {
        val adminComponent = ComponentName(context, AppKillerDeviceAdmin::class.java)
        return devicePolicyManager.isAdminActive(adminComponent)
    }
    
    fun requestDeviceAdminPermission(): Intent {
        val adminComponent = ComponentName(context, AppKillerDeviceAdmin::class.java)
        return Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
            putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, 
                "Enable device admin to allow AppKiller to force stop applications")
        }
    }
    
    private fun getRunningAppPackages(): Set<String> {
        // Try ActivityManager first (real-time)
        val activityManagerResult = getRunningAppsFromActivityManager()
        
        return if (activityManagerResult != null) {
            // ActivityManager found sufficient apps
            activityManagerResult
        } else if (permissionManager.hasUsageStatsPermission()) {
            // Fallback to UsageStats
            getRunningAppsFromUsageStats()
        } else {
            // No permission, return empty set
            emptySet()
        }
    }
    
    private fun getRunningAppsFromUsageStats(): Set<String> {
        val currentTime = System.currentTimeMillis()
        val detectionWindow = com.jazz13.appkiller.settings.AppSettings.RUNNING_APP_DETECTION_WINDOW_MS
        
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_BEST,
            currentTime - detectionWindow,
            currentTime
        )
        return stats?.filter { it.lastTimeUsed > currentTime - detectionWindow }
            ?.map { it.packageName }
            ?.toSet() ?: emptySet()
    }
    
    private fun getRunningAppsFromActivityManager(): Set<String>? {
        val runningApps = mutableSetOf<String>()
        
        // Get running processes
        activityManager.runningAppProcesses?.forEach { process ->
            // Only include foreground and visible apps
            if (process.importance <= ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                // Extract main package name (before any ":" separator)
                val packageName = process.processName.split(":")[0]
                runningApps.add(packageName)
            }
        }
        
        // Return null if insufficient apps found (less than 3)
        return if (runningApps.size >= 3) runningApps else null
    }
    
    fun hasUsageStatsPermission(): Boolean = permissionManager.hasUsageStatsPermission()
    
    fun getUsageStatsPermissionIntent(): Intent = permissionManager.requestUsageStatsPermission()
}
