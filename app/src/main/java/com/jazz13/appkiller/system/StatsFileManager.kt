package com.jazz13.appkiller.system

import android.content.Context
import com.jazz13.appkiller.data.SystemStats
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class StatsFileManager(private val context: Context) {
    private val statsFile = File(context.filesDir, "system_stats.json")
    private val maxFileSize = 1024 * 1024 // 1MB
    
    fun writeStats(stats: SystemStats) {
        try {
            // Check file size before writing to preserve existing data
            if (statsFile.exists() && statsFile.length() > maxFileSize) {
                truncateFile()
            }
            
            val statsJson = Json.encodeToString(stats) + "\n"
            statsFile.appendText(statsJson)
            
            // Check again after writing
            if (statsFile.length() > maxFileSize) {
                truncateFile()
            }
        } catch (e: Exception) {
            // Ignore write errors
        }
    }
    
    fun readAllStats(): List<SystemStats> {
        return try {
            if (!statsFile.exists()) return emptyList()
            
            statsFile.readLines()
                .filter { it.isNotBlank() }
                .mapNotNull { line ->
                    try {
                        Json.decodeFromString<SystemStats>(line)
                    } catch (e: Exception) {
                        null
                    }
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun readRecentStats(windowMinutes: Int = 30): List<SystemStats> {
        val cutoffTime = System.currentTimeMillis() - (windowMinutes * 60 * 1000L)
        return readAllStats().filter { it.timestamp >= cutoffTime }
    }
    
    private fun truncateFile() {
        try {
            val allStats = readAllStats()
            val keepCount = allStats.size / 2 // Keep newest half
            val statsToKeep = allStats.takeLast(keepCount)
            
            statsFile.writeText("")
            statsToKeep.forEach { stats ->
                val statsJson = Json.encodeToString(stats) + "\n"
                statsFile.appendText(statsJson)
            }
        } catch (e: Exception) {
            // If truncation fails, clear file
            statsFile.writeText("")
        }
    }
}
