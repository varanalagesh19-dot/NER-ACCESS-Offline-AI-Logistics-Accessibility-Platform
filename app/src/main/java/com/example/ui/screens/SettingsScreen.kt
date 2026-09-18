package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    isOfflineDemoMode: Boolean,
    onToggleOfflineDemoMode: () -> Unit,
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    onClearAllData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    var voiceGuidanceEnabled by remember { mutableStateOf(true) }
    var highContrastEnabled by remember { mutableStateOf(false) }
    var useMetricUnits by remember { mutableStateOf(true) }

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
                        Icons.Default.Settings,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "SETTINGS & SYSTEM PROFILE",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = colorScheme.onSurface
                    )
                }
                Text(
                    text = "Configure offline operating modes, accessibility preferences, and review Smart India Hackathon 2026 specifications.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }

        // Operational Modes
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
                    Text(
                        text = "NETWORK & DEMO CONFIGURATION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = colorScheme.primary
                    )

                    SettingsSwitchRow(
                        title = "Offline Demo Mode",
                        subtitle = "Disables any external network calls; forces strict offline bundled dataset operation.",
                        checked = isOfflineDemoMode,
                        onCheckedChange = { onToggleOfflineDemoMode() },
                        icon = Icons.Default.WifiOff,
                        testTag = "settings_offline_toggle"
                    )

                    Divider(color = colorScheme.outline.copy(alpha = 0.2f))

                    SettingsSwitchRow(
                        title = "Dark Theme",
                        subtitle = "High-contrast dark canvas optimized for low-power OLED & field usage.",
                        checked = isDarkTheme,
                        onCheckedChange = { onToggleDarkTheme() },
                        icon = Icons.Default.DarkMode,
                        testTag = "settings_dark_theme_toggle"
                    )

                    Divider(color = colorScheme.outline.copy(alpha = 0.2f))

                    SettingsSwitchRow(
                        title = "Voice Navigation Prompts",
                        subtitle = "Synthesize turn cues locally without requiring external speech cloud API.",
                        checked = voiceGuidanceEnabled,
                        onCheckedChange = { voiceGuidanceEnabled = it },
                        icon = Icons.Default.VolumeUp,
                        testTag = "settings_voice_toggle"
                    )

                    Divider(color = colorScheme.outline.copy(alpha = 0.2f))

                    SettingsSwitchRow(
                        title = "Metric Distance Units (km / m)",
                        subtitle = "Toggle between kilometers (Metric) and statute miles.",
                        checked = useMetricUnits,
                        onCheckedChange = { useMetricUnits = it },
                        icon = Icons.Default.Straighten,
                        testTag = "settings_units_toggle"
                    )
                }
            }
        }

        // SIH 2026 Hackathon Metadata Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.primaryContainer.copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("sih_specs_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "SMART INDIA HACKATHON 2026 SPECIFICATION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = colorScheme.primary
                    )

                    MetadataRow("Problem Statement ID", "SIH26002")
                    MetadataRow("Title", "AI-Powered Logistics & Accessibility Platform for NER")
                    MetadataRow("Theme", "Smart Automation")
                    MetadataRow("Category", "Software (Offline-First)")
                    MetadataRow("Core Motto", "“SMART NAVIGATION. ZERO INTERNET DEPENDENCY.”")
                    MetadataRow("Local Engine", "A* Heuristic Graph Search & Priority TSP")
                    MetadataRow("Offline Storage", "Jetpack Room / SQLite SQLite3")
                    MetadataRow("Emergency Flow", "Offline Queue + Direct Cellular SMS")
                }
            }
        }

        // Reset and Clear Data Actions
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "DATA MANAGEMENT",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = colorScheme.primary
                    )

                    Button(
                        onClick = {
                            onClearAllData()
                            Toast.makeText(context, "All route history and cache cleared.", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error),
                        modifier = Modifier.fillMaxWidth().testTag("reset_all_data_button")
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Clear All Local History & Cache")
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
private fun MetadataRow(key: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(text = key, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
