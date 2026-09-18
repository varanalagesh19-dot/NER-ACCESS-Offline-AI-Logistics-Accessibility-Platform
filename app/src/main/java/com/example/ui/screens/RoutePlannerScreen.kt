package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.ui.components.OfflineMapCanvas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutePlannerScreen(
    nodes: List<LocationNode>,
    edges: List<RouteEdge>,
    startNodeId: String,
    destNodeId: String,
    onStartNodeChange: (String) -> Unit,
    onDestNodeChange: (String) -> Unit,
    preference: RoutePreference,
    onPreferenceChange: (RoutePreference) -> Unit,
    accessibilityReqs: AccessibilityRequirements,
    onReqsChange: (AccessibilityRequirements) -> Unit,
    routeOptions: List<RouteOption>,
    selectedRouteOption: RouteOption?,
    onSelectRouteOption: (RouteOption) -> Unit,
    isCalculating: Boolean,
    onCalculateRoute: () -> Unit,
    onSaveRouteToHistory: (RouteOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    var expandedStart by remember { mutableStateOf(false) }
    var expandedDest by remember { mutableStateOf(false) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }

    val startNode = nodes.find { it.id == startNodeId } ?: nodes.firstOrNull()
    val destNode = nodes.find { it.id == destNodeId } ?: nodes.getOrNull(1)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp)
    ) {
        // Section Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "OFFLINE ROUTE PLANNER",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = colorScheme.onSurface
                )
                Text(
                    text = "Zero internet graph calculation engine (A* heuristic over preloaded NER road network)",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }

        // Location Inputs Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Start Location Picker
                    ExposedDropdownMenuBox(
                        expanded = expandedStart,
                        onExpandedChange = { expandedStart = it }
                    ) {
                        OutlinedTextField(
                            value = startNode?.name ?: "Select Starting Location",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("START LOCATION") },
                            leadingIcon = {
                                Icon(Icons.Default.TripOrigin, contentDescription = null, tint = Color(0xFF10B981))
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStart) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("start_location_input")
                        )
                        ExposedDropdownMenu(
                            expanded = expandedStart,
                            onDismissRequest = { expandedStart = false }
                        ) {
                            nodes.forEach { node ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(node.name, fontWeight = FontWeight.SemiBold)
                                            Text(
                                                "${node.state} • ${node.elevationMeters}m altitude",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        onStartNodeChange(node.id)
                                        expandedStart = false
                                    }
                                )
                            }
                        }
                    }

                    // Use Current Location button & Swap Locations
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { onStartNodeChange("GHY_01") }, // Simulates GPS fix at Guwahati Central
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("use_current_location_button")
                        ) {
                            Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Use Current Location (GPS / Local Fix)", fontSize = 11.sp)
                        }

                        IconButton(
                            onClick = {
                                val oldStart = startNodeId
                                onStartNodeChange(destNodeId)
                                onDestNodeChange(oldStart)
                            },
                            modifier = Modifier.size(32.dp).testTag("swap_locations_button")
                        ) {
                            Icon(Icons.Default.SwapVert, contentDescription = "Swap start and dest", tint = colorScheme.primary)
                        }
                    }

                    // Destination Picker
                    ExposedDropdownMenuBox(
                        expanded = expandedDest,
                        onExpandedChange = { expandedDest = it }
                    ) {
                        OutlinedTextField(
                            value = destNode?.name ?: "Select Destination",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("DESTINATION") },
                            leadingIcon = {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFEF4444))
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDest) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("destination_input")
                        )
                        ExposedDropdownMenu(
                            expanded = expandedDest,
                            onDismissRequest = { expandedDest = false }
                        ) {
                            nodes.forEach { node ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(node.name, fontWeight = FontWeight.SemiBold)
                                            Text(
                                                "${node.state} • ${node.elevationMeters}m elevation",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        onDestNodeChange(node.id)
                                        expandedDest = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Route Preference Selector
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "ROUTE PREFERENCE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = colorScheme.primary
                    )

                    // Preference Chips Grid
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val prefs = RoutePreference.values().toList()
                        prefs.chunked(2).forEach { rowPrefs ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowPrefs.forEach { p ->
                                    val isSelected = preference == p
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { onPreferenceChange(p) },
                                        label = {
                                            Text(
                                                p.label,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        modifier = Modifier.weight(1f).testTag("pref_${p.name.lowercase()}"),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = colorScheme.primaryContainer,
                                            selectedLabelColor = colorScheme.onPrimaryContainer
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Accessibility Requirements Checklist
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Accessible,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "ACCESSIBILITY REQUIREMENTS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = colorScheme.onSurface
                        )
                    }

                    AccessibilityCheckboxItem(
                        label = "Wheelchair Accessible (Ramped, < 7% Grade)",
                        checked = accessibilityReqs.wheelchairAccessible,
                        onCheckedChange = { onReqsChange(accessibilityReqs.copy(wheelchairAccessible = it)) },
                        testTag = "req_wheelchair"
                    )
                    AccessibilityCheckboxItem(
                        label = "Avoid Stairs / Flight Steps",
                        checked = accessibilityReqs.avoidStairs,
                        onCheckedChange = { onReqsChange(accessibilityReqs.copy(avoidStairs = it)) },
                        testTag = "req_stairs"
                    )
                    AccessibilityCheckboxItem(
                        label = "Avoid Steep Mountain Roads (> 6% Incline)",
                        checked = accessibilityReqs.avoidSteepRoads,
                        onCheckedChange = { onReqsChange(accessibilityReqs.copy(avoidSteepRoads = it)) },
                        testTag = "req_steep"
                    )
                    AccessibilityCheckboxItem(
                        label = "Low Walking Distance / Direct Vehicle Proximity",
                        checked = accessibilityReqs.lowWalkingDistance,
                        onCheckedChange = { onReqsChange(accessibilityReqs.copy(lowWalkingDistance = it)) },
                        testTag = "req_walking"
                    )
                    AccessibilityCheckboxItem(
                        label = "Accessible Entry / Automatic Doors",
                        checked = accessibilityReqs.accessibleEntry,
                        onCheckedChange = { onReqsChange(accessibilityReqs.copy(accessibleEntry = it)) },
                        testTag = "req_entry"
                    )
                    AccessibilityCheckboxItem(
                        label = "Accessible Rest Stops & Sanitaries En-Route",
                        checked = accessibilityReqs.accessibleRestStops,
                        onCheckedChange = { onReqsChange(accessibilityReqs.copy(accessibleRestStops = it)) },
                        testTag = "req_rest"
                    )
                }
            }
        }

        // Action Button: "OPTIMIZE ROUTE"
        item {
            Button(
                onClick = onCalculateRoute,
                enabled = !isCalculating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("optimize_route_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
            ) {
                if (isCalculating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Executing Local A* Search...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("OPTIMIZE ROUTE (OFFLINE)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }

        // Map View of Calculated Routes
        if (routeOptions.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "LIVE ROUTE TOPOLOGY & GRADIENT PREVIEW",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = colorScheme.primary
                    )
                    OfflineMapCanvas(
                        nodes = nodes,
                        edges = edges,
                        selectedRoute = selectedRouteOption ?: routeOptions.firstOrNull(),
                        alternativeRoute = routeOptions.getOrNull(1),
                        startNodeId = startNodeId,
                        destNodeId = destNodeId,
                        currentLocationNodeId = startNodeId
                    )
                }
            }

            // Route Comparison Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CALCULATED ROUTE CANDIDATES (${routeOptions.size})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = colorScheme.primary
                    )
                    Text(
                        text = "Select preferred option",
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            // Route Candidate Cards
            items(routeOptions) { option ->
                val isSelected = selectedRouteOption?.id == option.id

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) colorScheme.primaryContainer.copy(alpha = 0.35f) else colorScheme.surface
                    ),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, colorScheme.primary) else androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.3f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectRouteOption(option) }
                        .testTag("route_option_${option.id.lowercase()}")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Title & Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = option.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                    color = colorScheme.onSurface
                                )
                                Text(
                                    text = "${option.pathNodes.size} Transit Nodes • ${option.fuelEfficiencyRating}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (option.tag.contains("ACCESSIBLE")) Color(0xFFD1FAE5) else colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = option.tag,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                    color = if (option.tag.contains("ACCESSIBLE")) Color(0xFF065F46) else colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        // Metrics Grid: Distance, Time, Accessibility, Difficulty, Optimization Score
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MetricColumn(label = "Distance", value = "${option.distanceKm} km")
                            MetricColumn(
                                label = "Est. Time",
                                value = "${option.estimatedMinutes / 60}h ${option.estimatedMinutes % 60}m"
                            )
                            MetricColumn(
                                label = "Accessibility",
                                value = "${option.accessibilityScore}%",
                                valueColor = if (option.accessibilityScore >= 80) Color(0xFF10B981) else Color(0xFFF59E0B)
                            )
                            MetricColumn(label = "Score", value = "${option.optimizationScore}/10")
                        }

                        // Criteria Explanation (Mandate: Do NOT present arbitrary best route without explaining criteria)
                        Surface(
                            color = colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(
                                    text = "CRITERIA RATIONALE:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                    color = colorScheme.primary
                                )
                                Text(
                                    text = option.criteriaExplanation,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Terrain Profile Details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TerrainTag(
                                icon = Icons.Default.Landscape,
                                text = "Max Slope: ${option.maxSlopePercent}%",
                                isWarning = option.maxSlopePercent > 6.0
                            )
                            TerrainTag(
                                icon = Icons.Default.Stairs,
                                text = "Stairs: ${option.stairsEncountered} steps",
                                isWarning = option.stairsEncountered > 0
                            )
                            TerrainTag(
                                icon = Icons.Default.Terrain,
                                text = "+${option.elevationGainMeters}m Climbed",
                                isWarning = option.elevationGainMeters > 500
                            )
                        }

                        // Save to History Action
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onSaveRouteToHistory(option)
                                    showSuccessSnackbar = true
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("save_route_${option.id.lowercase()}")
                            ) {
                                Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save to History", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AccessibilityCheckboxItem(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun MetricColumn(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
            color = valueColor
        )
    }
}

@Composable
private fun TerrainTag(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, isWarning: Boolean) {
    Surface(
        color = if (isWarning) Color(0xFFFEF3C7) else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = if (isWarning) Color(0xFFB45309) else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = if (isWarning) Color(0xFF92400E) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
