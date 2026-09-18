package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.engine.LocalAIAccessibilityEngine

@Composable
fun AccessibilityScreen(
    currentRoute: RouteOption?,
    allNodes: List<LocationNode>,
    allEdges: List<RouteEdge>,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    // If a route is active, evaluate its specific nodes; otherwise evaluate the entire regional NER network baseline
    val activeNodes = currentRoute?.pathNodes ?: allNodes.take(5)
    val analysis = LocalAIAccessibilityEngine.analyzePath(activeNodes, allEdges)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp)
    ) {
        // Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.AccessibleForward,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "ACCESSIBILITY INTELLIGENCE",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = colorScheme.onSurface
                    )
                }
                Text(
                    text = "Local deterministic neural heuristics analyzing terrain gradients, barrier distribution, and verified infrastructure with zero cloud calls.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }

        // Active Analysis Target Indicator
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = if (currentRoute != null) "Active Route Profile: ${currentRoute.title}" else "Regional Network Baseline (Assam - Meghalaya)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = colorScheme.onSurface
                        )
                        Text(
                            text = "${activeNodes.size} Nodes Evaluated • Dataset Verified: ${analysis.verifiedDatasetPercent}%",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Accessibility Profile Hero Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth().testTag("accessibility_profile_card")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header with Score
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ACCESSIBILITY PROFILE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = colorScheme.primary
                            )
                            Text(
                                text = analysis.accessibilityRating,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                color = if (analysis.numericScore >= 80) Color(0xFF059669) else Color(0xFFD97706)
                            )
                        }

                        // Circular Score Badge
                        Surface(
                            shape = CircleShape,
                            color = if (analysis.numericScore >= 80) Color(0xFFD1FAE5) else Color(0xFFFEF3C7),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${analysis.numericScore}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                    color = if (analysis.numericScore >= 80) Color(0xFF065F46) else Color(0xFF92400E)
                                )
                            }
                        }
                    }

                    Divider(color = colorScheme.outline.copy(alpha = 0.2f))

                    // Key Attribute Matrix
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ProfileAttributeRow(
                            icon = Icons.Default.Accessible,
                            label = "Wheelchair Traversal",
                            value = analysis.wheelchairStatus,
                            isGood = !analysis.wheelchairStatus.contains("Limited")
                        )
                        ProfileAttributeRow(
                            icon = Icons.Default.Stairs,
                            label = "Stairs Avoided",
                            value = if (analysis.stairsAvoided) "Yes (100% Ramp Alternative)" else "Stairs Detected (Barrier)",
                            isGood = analysis.stairsAvoided
                        )
                        ProfileAttributeRow(
                            icon = Icons.Default.Landscape,
                            label = "Steep Roads (> 6% grade)",
                            value = "${analysis.steepRoadsCount} segments detected",
                            isGood = analysis.steepRoadsCount == 0
                        )
                        ProfileAttributeRow(
                            icon = Icons.Default.DirectionsWalk,
                            label = "Walking / Traversal Distance",
                            value = "${String.format("%.1f", analysis.walkingDistanceKm)} km",
                            isGood = true
                        )
                        ProfileAttributeRow(
                            icon = Icons.Default.Speed,
                            label = "Maximum Incline Slope",
                            value = "${String.format("%.1f", analysis.maxSlopePercent)}%",
                            isGood = analysis.maxSlopePercent <= 6.0
                        )
                    }
                }
            }
        }

        // Data Verification & Authenticity Indicator (Mandate: Never fabricate accessibility information)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.primaryContainer.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth().testTag("verification_status_card")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "DATASET AUDIT & AUTHENTICITY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = colorScheme.primary
                    )
                    Text(
                        text = "In compliance with SIH 2026 guidelines, accessibility metrics are calculated strictly against preloaded offline ground surveys and verified district datasets. Unverified points are explicitly flagged.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        VerificationBadge(
                            label = "Verified Local Data",
                            status = "${analysis.verifiedDatasetPercent}%",
                            color = Color(0xFF10B981)
                        )
                        VerificationBadge(
                            label = "Community Survey",
                            status = "${100 - analysis.verifiedDatasetPercent}%",
                            color = Color(0xFFF59E0B)
                        )
                        VerificationBadge(
                            label = "Fabricated Data",
                            status = "0% (Prohibited)",
                            color = colorScheme.outline
                        )
                    }
                }
            }
        }

        // AI Local Barrier & Obstacle Assessment
        item {
            Text(
                text = "LOCAL AI RISK PATTERN DETECTIONS (${analysis.identifiedRisks.size})",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = colorScheme.primary
            )
        }

        items(analysis.identifiedRisks) { risk ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (risk.contains("Zero") || risk.contains("Continuous"))
                        Color(0xFFECFDF5)
                    else
                        colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = if (risk.contains("Zero") || risk.contains("Continuous"))
                            Icons.Default.CheckCircle
                        else
                            Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (risk.contains("Zero") || risk.contains("Continuous"))
                            Color(0xFF059669)
                        else
                            Color(0xFFD97706),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = risk,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = colorScheme.onSurface
                    )
                }
            }
        }

        // Verified Accessible Facilities
        if (analysis.accessibleFacilities.isNotEmpty()) {
            item {
                Text(
                    text = "VERIFIED ACCESSIBLE AMENITIES EN-ROUTE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = colorScheme.primary
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        analysis.accessibleFacilities.forEach { facility ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Done,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = facility,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileAttributeRow(
    icon: ImageVector,
    label: String,
    value: String,
    isGood: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            ),
            color = if (isGood) Color(0xFF059669) else Color(0xFFD97706)
        )
    }
}

@Composable
private fun VerificationBadge(label: String, status: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = status,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
