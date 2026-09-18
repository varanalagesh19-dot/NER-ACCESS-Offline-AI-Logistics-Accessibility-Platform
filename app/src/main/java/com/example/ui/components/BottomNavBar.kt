package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AppDestination(val label: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    PLAN_ROUTE("Plan Route", Icons.Default.AltRoute),
    ACCESSIBILITY("Accessibility", Icons.Default.AccessibleForward),
    LOGISTICS("Logistics", Icons.Default.LocalShipping),
    SOS("SOS Emergency", Icons.Default.Emergency),
    OFFLINE_DATA("Offline Data", Icons.Default.Storage),
    HISTORY("History", Icons.Default.History),
    SETTINGS("Settings", Icons.Default.Settings)
}

@Composable
fun BottomNavBar(
    currentDestination: AppDestination,
    onSelectDestination: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colorScheme.surface,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppDestination.values().forEach { destination ->
                val isSelected = currentDestination == destination
                val isSos = destination == AppDestination.SOS

                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectDestination(destination) },
                    label = {
                        Text(
                            text = destination.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = destination.label,
                            modifier = Modifier.size(16.dp),
                            tint = if (isSos && !isSelected) Color(0xFFEF4444) else if (isSelected) colorScheme.onPrimaryContainer else colorScheme.onSurfaceVariant
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = if (isSos) Color(0xFFFFD8D8) else colorScheme.primaryContainer,
                        selectedLabelColor = if (isSos) Color(0xFF991B1B) else colorScheme.onPrimaryContainer,
                        containerColor = if (isSos) Color(0xFFFFEEEE) else colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.testTag("nav_${destination.name.lowercase()}")
                )
            }
        }
    }
}
