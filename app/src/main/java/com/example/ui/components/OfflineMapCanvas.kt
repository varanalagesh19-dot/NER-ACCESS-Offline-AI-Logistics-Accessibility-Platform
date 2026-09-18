package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import kotlin.math.max
import kotlin.math.min

@Composable
fun OfflineMapCanvas(
    nodes: List<LocationNode>,
    edges: List<RouteEdge>,
    selectedRoute: RouteOption?,
    alternativeRoute: RouteOption?,
    startNodeId: String?,
    destNodeId: String?,
    currentLocationNodeId: String?,
    modifier: Modifier = Modifier,
    onNodeSelected: ((LocationNode) -> Unit)? = null
) {
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }
    var inspectedNode by remember { mutableStateOf<LocationNode?>(null) }

    val textMeasurer = rememberTextMeasurer()

    // Determine geographic bounding box for NER
    val minLat = 24.6
    val maxLat = 27.0
    val minLon = 91.4
    val maxLon = 94.4

    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(1.dp, colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .testTag("offline_map_canvas")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        panOffsetX += dragAmount.x
                        panOffsetY += dragAmount.y
                    }
                }
        ) {
            val width = size.width
            val height = size.height

            // Helper to project lat/lon to Canvas coordinates
            fun project(lat: Double, lon: Double): Offset {
                val normX = ((lon - minLon) / (maxLon - minLon)).toFloat()
                val normY = (1f - ((lat - minLat) / (maxLat - minLat))).toFloat() // Inverted Y

                val centerX = width * normX
                val centerY = height * normY

                val scaledX = (centerX - width / 2) * zoomLevel + width / 2 + panOffsetX
                val scaledY = (centerY - height / 2) * zoomLevel + height / 2 + panOffsetY
                return Offset(scaledX, scaledY)
            }

            // 1. Draw topographic grid lines
            val gridStep = 40f * zoomLevel
            val startX = (panOffsetX % gridStep)
            val startY = (panOffsetY % gridStep)
            var curX = startX
            while (curX < width) {
                drawLine(
                    color = colorScheme.outline.copy(alpha = 0.12f),
                    start = Offset(curX, 0f),
                    end = Offset(curX, height),
                    strokeWidth = 1f
                )
                curX += gridStep
            }
            var curY = startY
            while (curY < height) {
                drawLine(
                    color = colorScheme.outline.copy(alpha = 0.12f),
                    start = Offset(0f, curY),
                    end = Offset(width, curY),
                    strokeWidth = 1f
                )
                curY += gridStep
            }

            // 2. Draw all road edges in background
            val nodeMap = nodes.associateBy { it.id }
            for (edge in edges) {
                val n1 = nodeMap[edge.fromId]
                val n2 = nodeMap[edge.toId]
                if (n1 != null && n2 != null) {
                    val p1 = project(n1.lat, n1.lon)
                    val p2 = project(n2.lat, n2.lon)

                    val edgeColor = when {
                        edge.slopePercent > 7.0 -> Color(0xFFF59E0B).copy(alpha = 0.5f) // Steep amber
                        edge.surface == RoadSurface.EXCELLENT_HIGHWAY -> colorScheme.primary.copy(alpha = 0.35f)
                        else -> colorScheme.onSurface.copy(alpha = 0.25f)
                    }

                    drawLine(
                        color = edgeColor,
                        start = p1,
                        end = p2,
                        strokeWidth = if (edge.surface == RoadSurface.EXCELLENT_HIGHWAY) 4f * zoomLevel else 2.5f * zoomLevel,
                        cap = StrokeCap.Round
                    )
                }
            }

            // 3. Draw Alternative Route line if present
            alternativeRoute?.let { alt ->
                for (i in 0 until alt.pathNodes.size - 1) {
                    val p1 = project(alt.pathNodes[i].lat, alt.pathNodes[i].lon)
                    val p2 = project(alt.pathNodes[i + 1].lat, alt.pathNodes[i + 1].lon)
                    drawLine(
                        color = Color(0xFFF59E0B),
                        start = p1,
                        end = p2,
                        strokeWidth = 5f * zoomLevel,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f),
                        cap = StrokeCap.Round
                    )
                }
            }

            // 4. Draw Active Selected Route line
            selectedRoute?.let { sel ->
                for (i in 0 until sel.pathNodes.size - 1) {
                    val p1 = project(sel.pathNodes[i].lat, sel.pathNodes[i].lon)
                    val p2 = project(sel.pathNodes[i + 1].lat, sel.pathNodes[i + 1].lon)
                    // Outer glow
                    drawLine(
                        color = Color(0xFF10B981).copy(alpha = 0.4f),
                        start = p1,
                        end = p2,
                        strokeWidth = 10f * zoomLevel,
                        cap = StrokeCap.Round
                    )
                    // Core route line
                    drawLine(
                        color = Color(0xFF10B981),
                        start = p1,
                        end = p2,
                        strokeWidth = 6f * zoomLevel,
                        cap = StrokeCap.Round
                    )
                }
            }

            // 5. Draw Location Nodes
            for (node in nodes) {
                val pos = project(node.lat, node.lon)
                val isStart = node.id == startNodeId
                val isDest = node.id == destNodeId
                val isCurrent = node.id == currentLocationNodeId
                val isInSelectedPath = selectedRoute?.pathNodeIds?.contains(node.id) == true

                val nodeColor = when {
                    isStart -> Color(0xFF10B981) // Green
                    isDest -> Color(0xFFEF4444) // Red
                    isCurrent -> Color(0xFF3B82F6) // Blue GPS
                    node.category == NodeCategory.EMERGENCY_HOSPITAL -> Color(0xFFDC2626)
                    node.category == NodeCategory.LOGISTICS_HUB -> Color(0xFFF59E0B)
                    node.category == NodeCategory.REST_POINT -> Color(0xFF0D9488)
                    node.category == NodeCategory.ACCESSIBLE_TRANSIT -> Color(0xFF059669)
                    else -> colorScheme.primary
                }

                val radius = when {
                    isStart || isDest || isCurrent -> 9f * zoomLevel
                    isInSelectedPath -> 7.5f * zoomLevel
                    else -> 5.5f * zoomLevel
                }

                // Node circle
                drawCircle(
                    color = nodeColor,
                    radius = radius,
                    center = pos
                )
                drawCircle(
                    color = Color.White,
                    radius = radius * 0.45f,
                    center = pos
                )

                // Draw Current location beacon ripple if active
                if (isCurrent) {
                    drawCircle(
                        color = Color(0xFF3B82F6).copy(alpha = 0.3f),
                        radius = radius * 2.2f,
                        center = pos,
                        style = Stroke(width = 2f)
                    )
                }

                // Node label for important/selected nodes
                if (zoomLevel > 0.85f || isInSelectedPath || isStart || isDest) {
                    val label = when {
                        isStart -> "START: ${node.name.substringBefore(" ")}"
                        isDest -> "DEST: ${node.name.substringBefore(" ")}"
                        else -> node.name.substringBefore(" ")
                    }
                    val textLayout = textMeasurer.measure(
                        text = label,
                        style = TextStyle(
                            fontSize = (10 * min(zoomLevel, 1.4f)).sp,
                            fontWeight = if (isInSelectedPath || isStart || isDest) FontWeight.Bold else FontWeight.Medium,
                            color = if (isStart || isDest) nodeColor else colorScheme.onSurface
                        )
                    )
                    drawText(
                        textLayoutResult = textLayout,
                        topLeft = Offset(pos.x - textLayout.size.width / 2, pos.y + radius + 3f)
                    )
                }
            }
        }

        // Top Status Badge: Zero Internet Guarantee & Region Info
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp),
            color = colorScheme.surface.copy(alpha = 0.92f),
            shape = RoundedCornerShape(8.dp),
            tonalElevation = 3.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF10B981), CircleShape)
                )
                Text(
                    text = "LOCAL VECTOR MAP • NER CORRIDORS (OFFLINE)",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                    color = colorScheme.onSurface
                )
            }
        }

        // Map Control Floating Buttons (Zoom In, Zoom Out, Reset Center)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilledTonalIconButton(
                onClick = { zoomLevel = (zoomLevel * 1.25f).coerceAtMost(3.0f) },
                modifier = Modifier.size(34.dp).testTag("map_zoom_in_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom in", modifier = Modifier.size(18.dp))
            }
            FilledTonalIconButton(
                onClick = { zoomLevel = (zoomLevel / 1.25f).coerceAtLeast(0.6f) },
                modifier = Modifier.size(34.dp).testTag("map_zoom_out_button")
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom out", modifier = Modifier.size(18.dp))
            }
            FilledTonalIconButton(
                onClick = {
                    zoomLevel = 1.0f
                    panOffsetX = 0f
                    panOffsetY = 0f
                },
                modifier = Modifier.size(34.dp).testTag("map_reset_button")
            ) {
                Icon(Icons.Default.CenterFocusStrong, contentDescription = "Reset map", modifier = Modifier.size(18.dp))
            }
        }

        // Bottom Map Legend Bar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            color = colorScheme.surface.copy(alpha = 0.94f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = Color(0xFF10B981), label = "Route")
                LegendItem(color = Color(0xFFF59E0B), label = "Alternative / Steep")
                LegendItem(color = Color(0xFFDC2626), label = "Hospital SOS")
                LegendItem(color = Color(0xFF0D9488), label = "Rest Point")
                LegendItem(color = Color(0xFF3B82F6), label = "GPS Pin")
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(color, CircleShape)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
