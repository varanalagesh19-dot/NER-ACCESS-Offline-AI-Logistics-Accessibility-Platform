package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import java.util.UUID

@Composable
fun LogisticsScreen(
    stops: List<LogisticsStop>,
    logisticsPlan: LogisticsPlan?,
    onAddStop: (LogisticsStop) -> Unit,
    onRemoveStop: (String) -> Unit,
    onToggleStopComplete: (String) -> Unit,
    onOptimizeLogistics: () -> Unit,
    isOptimizing: Boolean,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp)
    ) {
        // Section Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "LOGISTICS INTELLIGENCE",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = colorScheme.onSurface
                    )
                }
                Text(
                    text = "Local multi-stop route sequencing with critical medical prioritization, time window compliance, and axle load verification.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }

        // Action Toolbar: Add Stop & Optimize
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.weight(1f).height(48.dp).testTag("add_stop_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AddLocationAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Stop", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onOptimizeLogistics,
                    enabled = !isOptimizing && stops.isNotEmpty(),
                    modifier = Modifier.weight(1.2f).height(48.dp).testTag("optimize_logistics_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                ) {
                    if (isOptimizing) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = colorScheme.onPrimary)
                    } else {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Optimize Sequence", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Optimization Plan Summary Card (if generated)
        logisticsPlan?.let { plan ->
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.primaryContainer.copy(alpha = 0.4f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().testTag("logistics_plan_card")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "OPTIMIZED DISPATCH METRICS",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                    color = colorScheme.primary
                                )
                                Text(
                                    text = "Efficiency Score: ${plan.efficiencyScore}%",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                    color = colorScheme.onSurface
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF10B981)
                            ) {
                                Text(
                                    text = "LOCAL TSP SOLVED",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        // Metrics Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            LogisticsMetricItem(label = "Total Dist", value = "${plan.totalDistanceKm} km")
                            LogisticsMetricItem(label = "Est. Duration", value = "${plan.estimatedHours} hrs")
                            LogisticsMetricItem(label = "Total Stops", value = "${plan.totalStops}")
                            LogisticsMetricItem(label = "Priority Drops", value = "${plan.priorityDeliveriesCount}", valueColor = Color(0xFFEF4444))
                        }

                        Surface(
                            color = colorScheme.surface,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = plan.summary,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        // Freight Axle Clearance Tag
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(14.dp))
                            Text(
                                text = plan.freightClearanceStatus,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Stop List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DISPATCH ITINERARY & STOPS (${stops.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = colorScheme.primary
                )
                Text(
                    text = "Tap circle to toggle completion",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }

        // Ordered Stops List
        itemsIndexed(stops) { index, stop ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (stop.completed) colorScheme.surfaceVariant.copy(alpha = 0.5f) else colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth().testTag("stop_item_$index")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Sequence Badge / Checkbox
                    IconButton(
                        onClick = { onToggleStopComplete(stop.id) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        if (stop.completed) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Completed", tint = Color(0xFF10B981))
                        } else {
                            Surface(
                                shape = CircleShape,
                                color = colorScheme.primaryContainer,
                                modifier = Modifier.size(26.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }

                    // Stop Details
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = stop.name,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = if (stop.completed) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                ),
                                color = colorScheme.onSurface
                            )
                            PriorityBadge(stop.priority)
                        }

                        Text(
                            text = "${stop.stateLocation} • Window: ${stop.timeWindow} • ${stop.weightKg} kg",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = colorScheme.onSurfaceVariant
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Accessible, contentDescription = null, modifier = Modifier.size(12.dp), tint = colorScheme.primary)
                            Text(
                                text = stop.accessibilityRequirement,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = colorScheme.primary
                            )
                        }
                    }

                    // Remove Stop Button
                    IconButton(
                        onClick = { onRemoveStop(stop.id) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Delete stop", tint = colorScheme.outline, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }

    // Add Stop Modal Dialog
    if (showAddDialog) {
        AddStopDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { newStop ->
                onAddStop(newStop)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddStopDialog(
    onDismiss: () -> Unit,
    onConfirm: (LogisticsStop) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Guwahati / Shillong Highway") }
    var stopType by remember { mutableStateOf(StopType.DELIVERY) }
    var priority by remember { mutableStateOf(StopPriority.HIGH) }
    var timeWindow by remember { mutableStateOf("10:00 - 12:00") }
    var weightText by remember { mutableStateOf("250.0") }
    var accReq by remember { mutableStateOf("Universal Medical Ramp Required") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Freight Stop / Consignment", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Facility / Hub Name") },
                    placeholder = { Text("e.g. Shillong Cold Depot") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("dialog_stop_name")
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location / NER Route") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = timeWindow,
                    onValueChange = { timeWindow = it },
                    label = { Text("Delivery Time Window") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = accReq,
                    onValueChange = { accReq = it },
                    label = { Text("Accessibility / Dock Requirement") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val stop = LogisticsStop(
                            id = "LS_${UUID.randomUUID().toString().take(6)}",
                            name = name,
                            stateLocation = location,
                            type = stopType,
                            priority = priority,
                            timeWindow = timeWindow,
                            weightKg = weightText.toDoubleOrNull() ?: 100.0,
                            accessibilityRequirement = accReq
                        )
                        onConfirm(stop)
                    }
                },
                modifier = Modifier.testTag("dialog_confirm_button")
            ) {
                Text("Add Stop")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun PriorityBadge(priority: StopPriority) {
    val (bgColor, textColor) = when (priority) {
        StopPriority.CRITICAL_MEDICAL -> Color(0xFFFEE2E2) to Color(0xFF991B1B)
        StopPriority.HIGH -> Color(0xFFFEF3C7) to Color(0xFF92400E)
        StopPriority.MEDIUM -> Color(0xFFE0F2FE) to Color(0xFF075985)
        StopPriority.LOW -> Color(0xFFF1F5F9) to Color(0xFF475569)
    }

    Surface(shape = RoundedCornerShape(4.dp), color = bgColor) {
        Text(
            text = priority.label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.sp),
            color = textColor,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun LogisticsMetricItem(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp), color = valueColor)
    }
}
