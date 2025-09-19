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
    
    private fun getRunningAppPackages(): Set<String> {
        val runningApps = mutableSetOf<String>()
        
        // Primary method: UsageStats (most reliable on modern Android)
        if (permissionManager.hasUsageStatsPermission()) {
            runningApps.addAll(getActiveAppsFromUsageStats())
        }
        
        // Secondary: Recent tasks for foreground/minimized apps
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                activityManager.appTasks.forEach { task ->
                    task.taskInfo.baseActivity?.packageName?.let { packageName ->
                        runningApps.add(packageName)
                    }
                }
            } catch (e: Exception) {
                // Continue if can't access app tasks
            }
        }
        
        // Fallback: Running processes (limited on modern Android)
        activityManager.runningAppProcesses?.forEach { process ->
            val packageName = process.processName.split(":")[0]
            runningApps.add(packageName)
        }
        
        return runningApps
    }
    
    private fun getActiveAppsFromUsageStats(): Set<String> {
        val currentTime = System.currentTimeMillis()
        val recentWindow = 30000L // 30 seconds
        
        return try {
            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST,
                currentTime - recentWindow,
                currentTime
            )
            
            stats?.filter { 
                it.lastTimeUsed > currentTime - recentWindow || 
                it.lastTimeVisible > currentTime - recentWindow
            }?.map { it.packageName }?.toSet() ?: emptySet()
        } catch (e: Exception) {
            emptySet()
        }
    }
    
    fun killApp(packageName: String): Boolean {
        if (packageName == context.packageName) return false
        
        return try {
            activityManager.killBackgroundProcesses(packageName)
            
            try {
                Runtime.getRuntime().exec("am force-stop $packageName").waitFor()
            } catch (e: Exception) {
                // Continue with other methods
            }
            
            activityManager.runningAppProcesses?.forEach { process ->
                if (process.processName.startsWith(packageName)) {
                    android.os.Process.killProcess(process.pid)
                }
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activityManager.appTasks.forEach { task ->
                    if (task.taskInfo.baseActivity?.packageName == packageName) {
                        task.finishAndRemoveTask()
                    }
                }
            }
            
            true
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
            val resources = packageManager.getResourcesForApplication(appInfo)
            val iconId = appInfo.icon
            
            if (iconId != 0) {
                resources.getDrawableForDensity(iconId, DisplayMetrics.DENSITY_XXXHIGH, null)
                    ?: resources.getDrawableForDensity(iconId, DisplayMetrics.DENSITY_XXHIGH, null)
                    ?: resources.getDrawableForDensity(iconId, DisplayMetrics.DENSITY_XHIGH, null)
                    ?: packageManager.getApplicationIcon(appInfo)
            } else {
                packageManager.getApplicationIcon(appInfo)
            }
        } catch (e: Exception) {
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
    
    fun hasUsageStatsPermission(): Boolean = permissionManager.hasUsageStatsPermission()
    
    fun getUsageStatsPermissionIntent(): Intent = permissionManager.requestUsageStatsPermission()
}
