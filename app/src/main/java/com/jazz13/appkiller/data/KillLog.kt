package com.jazz13.appkiller.data

import kotlinx.serialization.Serializable

@Serializable
data class KillLog(
    val id: Long = System.currentTimeMillis(),
    val date: String,
    val time: String,
    val appName: String,
    val killMode: String
)
