package com.jazz13.appkiller.data

import android.content.Context
import android.content.Intent
import com.jazz13.appkiller.system.AppManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(context: Context) {
    private val appManager = AppManager(context)
    
    suspend fun getApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        appManager.getInstalledApps()
    }
    
    suspend fun killApp(packageName: String): Boolean = withContext(Dispatchers.IO) {
        appManager.killApp(packageName)
    }
    
    fun openAppSettings(packageName: String) {
        appManager.openAppSettings(packageName)
    }
    
    fun hasUsageStatsPermission(): Boolean = appManager.hasUsageStatsPermission()
    
    fun getUsageStatsPermissionIntent(): Intent = appManager.getUsageStatsPermissionIntent()
    
    fun isDeviceAdminEnabled(): Boolean = appManager.isDeviceAdminEnabled()
    
    fun getDeviceAdminPermissionIntent(): Intent = appManager.requestDeviceAdminPermission()
}
