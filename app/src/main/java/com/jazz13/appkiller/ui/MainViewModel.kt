package com.jazz13.appkiller.ui

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jazz13.appkiller.data.AppInfo
import com.jazz13.appkiller.data.AppRepository
import com.jazz13.appkiller.data.KillLog
import com.jazz13.appkiller.data.SystemStats
import com.jazz13.appkiller.data.SystemStatsHistory
import com.jazz13.appkiller.settings.AppSettings
import com.jazz13.appkiller.system.SystemMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class MainViewModel(
    private val repository: AppRepository,
    private val settings: AppSettings,
    private val systemMonitor: SystemMonitor
) : ViewModel() {
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _needsUsagePermission = MutableStateFlow(false)
    val needsUsagePermission: StateFlow<Boolean> = _needsUsagePermission
    
    private val _needsDeviceAdmin = MutableStateFlow(false)
    val needsDeviceAdmin: StateFlow<Boolean> = _needsDeviceAdmin
    
    private val _killBehavior = MutableStateFlow(settings.killBehavior)
    val killBehavior: StateFlow<Int> = _killBehavior
    
    private val _showLogScreen = MutableStateFlow(false)
    val showLogScreen: StateFlow<Boolean> = _showLogScreen
    
    private val _killLogs = MutableStateFlow<List<KillLog>>(emptyList())
    val killLogs: StateFlow<List<KillLog>> = _killLogs
    
    private val _showSystemStats = MutableStateFlow(false)
    val showSystemStats: StateFlow<Boolean> = _showSystemStats
    
    private val _systemStatsHistory = MutableStateFlow(SystemStatsHistory(emptyList(), emptyList()))
    val systemStatsHistory: StateFlow<SystemStatsHistory> = _systemStatsHistory
    
    init {
        // Truncate logs on app startup
        settings.truncateLogs()
        
        // Start auto-refresh timer
        startAutoRefresh()
    }
    
    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(com.jazz13.appkiller.settings.AppSettings.AUTO_REFRESH_INTERVAL_MS)
                if (!_isLoading.value) { // Only refresh if not already loading
                    loadApps()
                }
            }
        }
    }
    
    fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            
            if (!repository.hasUsageStatsPermission()) {
                _needsUsagePermission.value = true
            }
            
            if (!repository.isDeviceAdminEnabled()) {
                _needsDeviceAdmin.value = true
            }
            
            _apps.value = repository.getApps()
            _isLoading.value = false
        }
    }
    
    fun killApp(packageName: String) {
        viewModelScope.launch {
            val appName = _apps.value.find { it.packageName == packageName }?.appName ?: packageName
            settings.addKillLog(appName)
            
            val success = repository.killApp(packageName)
            if (success) {
                loadApps()
            }
        }
    }
    
    fun openAppSettings(packageName: String) {
        val appName = _apps.value.find { it.packageName == packageName }?.appName ?: packageName
        settings.addKillLog(appName)
        repository.openAppSettings(packageName)
    }
    
    fun setKillBehavior(behavior: Int) {
        settings.killBehavior = behavior
        _killBehavior.value = behavior
    }
    
    fun showLogScreen() {
        _killLogs.value = settings.getKillLogs()
        _showLogScreen.value = true
    }
    
    fun hideLogScreen() {
        _showLogScreen.value = false
    }
    
    fun showSystemStats() {
        viewModelScope.launch {
            // Load initial 30-minute data
            updateSystemStatsHistory()
            _showSystemStats.value = true
            
            // Watch for new data every 30 seconds
            while (_showSystemStats.value) {
                delay(30000)
                if (_showSystemStats.value) {
                    updateSystemStatsHistory()
                }
            }
        }
    }
    
    private fun updateSystemStatsHistory() {
        val recentStats = systemMonitor.getRecentStats(30)
        val memoryData = recentStats.map { it.timestamp to it.memoryUsagePercent }
        val cpuData = recentStats.map { it.timestamp to it.cpuUsagePercent }
        _systemStatsHistory.value = SystemStatsHistory(memoryData, cpuData)
    }
    
    fun hideSystemStats() {
        _showSystemStats.value = false
    }
    
    fun startStatsCollection() {
        systemMonitor.startStatsCollection()
    }
    
    fun getUsagePermissionIntent(): Intent = repository.getUsageStatsPermissionIntent()
    
    fun getDeviceAdminIntent(): Intent = repository.getDeviceAdminPermissionIntent()
    
    fun onUsagePermissionGranted() {
        _needsUsagePermission.value = false
        loadApps()
    }
    
    fun onDeviceAdminGranted() {
        _needsDeviceAdmin.value = false
        loadApps()
    }
    
    override fun onCleared() {
        super.onCleared()
        systemMonitor.stopStatsCollection()
    }
}
