package com.jazz13.appkiller.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jazz13.appkiller.data.SystemStatsHistory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemStatsScreen(
    statsHistory: SystemStatsHistory,
    fontFamily: FontFamily,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = "System Statistics",
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
        
        // Memory Graph
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Memory Usage",
                        fontFamily = fontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    // Legend
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(Color(0xFF007AFF))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "RAM %",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                SystemGraph(
                    data = statsHistory.memoryData,
                    modifier = Modifier.fillMaxSize(),
                    maxValue = 100f,
                    unit = "%"
                )
            }
        }
        
        // CPU Graph
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CPU Usage",
                        fontFamily = fontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    // Legend
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(Color(0xFF007AFF))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "CPU %",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                SystemGraph(
                    data = statsHistory.cpuData,
                    modifier = Modifier.fillMaxSize(),
                    maxValue = 100f,
                    unit = "%"
                )
            }
        }
    }
}

@Composable
fun SystemGraph(
    data: List<Pair<Long, Float>>,
    modifier: Modifier = Modifier,
    maxValue: Float,
    unit: String
) {
    if (data.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Collecting data...",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
        return
    }
    
    Canvas(modifier = modifier) {
        drawGraphWithAxes(data, maxValue, unit)
    }
}

private fun DrawScope.drawGraphWithAxes(
    data: List<Pair<Long, Float>>,
    maxValue: Float,
    unit: String
) {
    if (data.size < 2) return
    
    val width = size.width
    val height = size.height
    val padding = 60f
    val bottomPadding = 80f
    
    val graphWidth = width - padding * 2
    val graphHeight = height - padding - bottomPadding
    
    // Time range: last 10 minutes
    val currentTime = System.currentTimeMillis()
    val tenMinutesAgo = currentTime - (10 * 60 * 1000)
    
    // Filter data to last 10 minutes
    val filteredData = data.filter { it.first >= tenMinutesAgo }
    if (filteredData.isEmpty()) return
    
    // Draw Y-axis labels (0%, 25%, 50%, 75%, 100%)
    val textColor = Color.White.copy(alpha = 0.7f)
    for (i in 0..4) {
        val value = (i * 25).toString() + unit
        val y = padding + graphHeight - (i * graphHeight / 4)
        
        drawContext.canvas.nativeCanvas.drawText(
            value,
            20f,
            y + 5f,
            android.graphics.Paint().apply {
                color = textColor.toArgb()
                textSize = 24f
                isAntiAlias = true
            }
        )
    }
    
    // Draw X-axis labels (time) - every 2 minutes
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    for (i in 0..5) {
        val timeOffset = i * 2 * 60 * 1000L // Every 2 minutes
        val timestamp = tenMinutesAgo + timeOffset
        val timeLabel = timeFormat.format(Date(timestamp))
        val x = padding + (i * graphWidth / 5)
        
        drawContext.canvas.nativeCanvas.drawText(
            timeLabel,
            x - 20f,
            height - 20f,
            android.graphics.Paint().apply {
                color = textColor.toArgb()
                textSize = 24f
                isAntiAlias = true
            }
        )
    }
    
    // Draw grid lines
    val gridColor = Color.White.copy(alpha = 0.1f)
    
    // Horizontal grid lines
    for (i in 0..4) {
        val y = padding + graphHeight - (i * graphHeight / 4)
        drawLine(
            color = gridColor,
            start = Offset(padding, y),
            end = Offset(padding + graphWidth, y),
            strokeWidth = 1.dp.toPx()
        )
    }
    
    // Vertical grid lines
    for (i in 0..5) {
        val x = padding + (i * graphWidth / 5)
        drawLine(
            color = gridColor,
            start = Offset(x, padding),
            end = Offset(x, padding + graphHeight),
            strokeWidth = 1.dp.toPx()
        )
    }
    
    // Create path for the line and area
    val path = Path()
    val areaPath = Path()
    
    filteredData.forEachIndexed { index, (timestamp, value) ->
        val x = padding + ((timestamp - tenMinutesAgo).toFloat() / (10 * 60 * 1000)) * graphWidth
        val y = padding + graphHeight - (value / maxValue) * graphHeight
        
        if (index == 0) {
            path.moveTo(x, y)
            areaPath.moveTo(x, padding + graphHeight)
            areaPath.lineTo(x, y)
        } else {
            path.lineTo(x, y)
            areaPath.lineTo(x, y)
        }
    }
    
    // Close area path
    if (filteredData.isNotEmpty()) {
        val lastX = padding + ((filteredData.last().first - tenMinutesAgo).toFloat() / (10 * 60 * 1000)) * graphWidth
        areaPath.lineTo(lastX, padding + graphHeight)
        areaPath.close()
    }
    
    // Draw area fill (translucent blue)
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0x60007AFF),  // More translucent blue at top
            Color(0x20007AFF)   // Very translucent blue at bottom
        ),
        startY = padding,
        endY = padding + graphHeight
    )
    
    drawPath(
        path = areaPath,
        brush = gradient
    )
    
    // Draw line (solid blue)
    drawPath(
        path = path,
        color = Color(0xFF007AFF),
        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
    )
}
