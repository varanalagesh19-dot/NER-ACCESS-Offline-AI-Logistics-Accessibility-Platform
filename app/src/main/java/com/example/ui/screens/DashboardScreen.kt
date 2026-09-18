package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.LocationNode
import com.example.data.RouteEdge
import com.example.ui.components.AppDestination
import com.example.ui.components.OfflineMapCanvas

@Composable
fun DashboardScreen(
    nodes: List<LocationNode>,
    edges: List<RouteEdge>,
    onNavigate: (AppDestination) -> Unit,
    onQuickStartRoute: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp)
    ) {
        // Zero Internet Status Hero Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("offline_ready_banner")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF10B981),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "🟢 OFFLINE READY",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                ),
                                color = colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "All core features are available offline.",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Text(
                        text = "“SMART NAVIGATION. ZERO INTERNET DEPENDENCY.”",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        color = colorScheme.primary
                    )

                    Surface(
                        color = colorScheme.surface.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = colorScheme.primary
                            )
                            Text(
                                text = "Your location & route data stays 100% on your device during offline operation.",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Live Offline Vector Map Preview
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NER TOPOLOGY & TRANSIT MAP",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = colorScheme.primary
                    )
                    Text(
                        text = "${nodes.size} Nodes • ${edges.size / 2} Road Links",
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                OfflineMapCanvas(
                    nodes = nodes,
                    edges = edges,
                    selectedRoute = null,
                    alternativeRoute = null,
                    startNodeId = "GHY_01",
                    destNodeId = "SHL_08",
                    currentLocationNodeId = "GHY_01"
                )
            }
        }

        // Quick Demonstration Shortcut (Guwahati -> Shillong / NEIGRIHMS)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colorScheme.secondaryContainer),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SIH 2026 Live Demo Pathway",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Guwahati Central → NEIGRIHMS Shillong (Hill Corridor)",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    Button(
                        onClick = { onQuickStartRoute("GHY_01", "SHL_08") },
                        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("quick_demo_button")
                    ) {
                        Text("Calculate", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Primary 6 Dashboard Cards
        item {
            Text(
                text = "SYSTEM MODULES",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = colorScheme.primary
            )
        }

        item {
            DashboardModuleCard(
                title = "1. Route Planner",
                description = "Plan an optimized route without internet.",
                badge = "A* Algorithm",
                icon = Icons.Default.AltRoute,
                iconTint = Color(0xFF0284C7),
                testTag = "card_route_planner",
                onClick = { onNavigate(AppDestination.PLAN_ROUTE) }
            )
        }

        item {
            DashboardModuleCard(
                title = "2. Accessibility Intelligence",
                description = "Find routes based on accessibility requirements & terrain slopes.",
                badge = "Local AI Model",
                icon = Icons.Default.AccessibleForward,
                iconTint = Color(0xFF10B981),
                testTag = "card_accessibility",
                onClick = { onNavigate(AppDestination.ACCESSIBILITY) }
            )
        }

        item {
            DashboardModuleCard(
                title = "3. Logistics Intelligence",
                description = "Optimize movement and multi-stop delivery planning.",
                badge = "TSP Heuristic",
                icon = Icons.Default.LocalShipping,
                iconTint = Color(0xFFF59E0B),
                testTag = "card_logistics",
                onClick = { onNavigate(AppDestination.LOGISTICS) }
            )
        }

        item {
            DashboardModuleCard(
                title = "4. Emergency SOS",
                description = "Access emergency assistance even without internet.",
                badge = "Offline Queue & SMS",
                icon = Icons.Default.Emergency,
                iconTint = Color(0xFFEF4444),
                testTag = "card_sos",
                onClick = { onNavigate(AppDestination.SOS) }
            )
        }

        item {
            DashboardModuleCard(
                title = "5. Offline Data Center",
                description = "Manage downloaded maps, road networks, and local datasets.",
                badge = "5 Bundled Layers",
                icon = Icons.Default.Storage,
                iconTint = Color(0xFF8B5CF6),
                testTag = "card_offline_data",
                onClick = { onNavigate(AppDestination.OFFLINE_DATA) }
            )
        }

        item {
            DashboardModuleCard(
                title = "6. Route History",
                description = "View previously calculated routes and reuse offline paths.",
                badge = "Local SQLite",
                icon = Icons.Default.History,
                iconTint = Color(0xFF0D9488),
                testTag = "card_history",
                onClick = { onNavigate(AppDestination.HISTORY) }
            )
        }

        // SIH 2026 Problem Statement Footer Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Smart India Hackathon 2026 • SIH26002",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "AI-Powered Logistics & Accessibility Intelligence Platform for North Eastern Region (NER). Built for high-altitude terrain, landslide zones, and zero-connectivity conditions.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardModuleCard(
    title: String,
    description: String,
    badge: String,
    icon: ImageVector,
    iconTint: Color,
    testTag: String,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = iconTint.copy(alpha = 0.12f),
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = colorScheme.onSurface
                    )
                    Surface(
                        color = colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = colorScheme.onSurfaceVariant
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = colorScheme.outline,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
