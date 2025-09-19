package com.jazz13.appkiller.data

import kotlinx.serialization.Serializable

@Serializable
data class SystemStats(
    val timestamp: Long,
    val memoryUsagePercent: Float,
    val cpuUsagePercent: Float
)

data class SystemStatsHistory(
    val memoryData: List<Pair<Long, Float>>,
    val cpuData: List<Pair<Long, Float>>
)
