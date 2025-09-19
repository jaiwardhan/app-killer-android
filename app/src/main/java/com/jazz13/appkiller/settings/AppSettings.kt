package com.jazz13.appkiller.settings

import android.content.Context
import android.content.SharedPreferences
import com.jazz13.appkiller.data.KillLog
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

class AppSettings(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "app_killer_settings"
        private const val KEY_KILL_BEHAVIOR = "TOGGLE_APP_KILL_BEHAVIOUR_ROUTE"
        private const val KEY_SPLASH_BACKGROUND = "SPLASH_BACKGROUND_COLOR"
        private const val KEY_KILL_LOGS = "KILL_LOGS"
        
        // Kill behavior constants
        const val KILL_BEHAVIOR_AUTO = 1
        const val KILL_BEHAVIOR_MANUAL = 2
        
        // Color constants
        const val DEFAULT_SPLASH_BACKGROUND = "#3B3B3B"
        
        // Log management constants
        const val MAX_LOG_SIZE = 128
        const val DAILY_LOG_LIMIT = 100
        
        // UI constants
        const val ICON_BITMAP_SIZE = 144  // High-res icon size (3x of 48dp)
        const val ICON_DISPLAY_SIZE = 48  // Display size in dp
        
        // Timing constants
        const val SPLASH_DURATION_MS = 2000L  // 2 seconds
        const val RUNNING_APP_DETECTION_WINDOW_MS = 30000L  // 30 seconds (much shorter)
        const val AUTO_REFRESH_INTERVAL_MS = 30000L  // 30 seconds
        const val SYSTEM_STATS_UPDATE_INTERVAL_MS = 10000L  // 10 seconds
        const val SYSTEM_STATS_HISTORY_DURATION_MS = 600000L  // 10 minutes
    }
    
    var killBehavior: Int
        get() = prefs.getInt(KEY_KILL_BEHAVIOR, KILL_BEHAVIOR_MANUAL)
        set(value) = prefs.edit().putInt(KEY_KILL_BEHAVIOR, value).apply()
    
    var splashBackgroundColor: String
        get() = prefs.getString(KEY_SPLASH_BACKGROUND, DEFAULT_SPLASH_BACKGROUND) ?: DEFAULT_SPLASH_BACKGROUND
        set(value) = prefs.edit().putString(KEY_SPLASH_BACKGROUND, value).apply()
    
    fun isAutoKill(): Boolean = killBehavior == KILL_BEHAVIOR_AUTO
    fun isManualKill(): Boolean = killBehavior == KILL_BEHAVIOR_MANUAL
    
    fun addKillLog(appName: String) {
        val currentTime = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        
        val log = KillLog(
            date = dateFormat.format(currentTime.time),
            time = timeFormat.format(currentTime.time),
            appName = appName,
            killMode = if (isAutoKill()) "Auto" else "Manual"
        )
        
        val logs = getKillLogs().toMutableList()
        logs.add(0, log) // Add to beginning
        
        // Keep only last DAILY_LOG_LIMIT logs
        if (logs.size > DAILY_LOG_LIMIT) {
            logs.removeAt(logs.size - 1)
        }
        
        val json = Json.encodeToString(logs)
        prefs.edit().putString(KEY_KILL_LOGS, json).apply()
    }
    
    fun getKillLogs(): List<KillLog> {
        val json = prefs.getString(KEY_KILL_LOGS, null) ?: return emptyList()
        return try {
            Json.decodeFromString<List<KillLog>>(json)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun truncateLogs() {
        val logs = getKillLogs()
        if (logs.size > MAX_LOG_SIZE) {
            val truncatedLogs = logs.take(MAX_LOG_SIZE)
            val json = Json.encodeToString(truncatedLogs)
            prefs.edit().putString(KEY_KILL_LOGS, json).apply()
        }
    }
}
