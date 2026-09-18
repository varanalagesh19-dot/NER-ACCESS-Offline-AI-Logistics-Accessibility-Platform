package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed

enum class OfflineStatusMode {
    OFFLINE_READY,
    ONLINE_ENHANCEMENT,
    SYNC_AVAILABLE
}

@Composable
fun TopHeaderBar(
    isOfflineDemoMode: Boolean,
    onToggleDemoMode: () -> Unit,
    statusMode: OfflineStatusMode,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Title and Subtitle
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = colorScheme.primaryContainer,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Navigation,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = colorScheme.primary
                                )
                            }
                        }
                        Text(
                            text = "NER ACCESS",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            ),
                            color = colorScheme.onSurface
                        )
                        Surface(
                            color = colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "SIH 2026",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                color = colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "AI-Powered Offline Logistics & Accessibility Intelligence",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = colorScheme.onSurfaceVariant
                    )
                }

                // Status Indicator and Actions
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Theme Toggle
                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier.size(36.dp).testTag("theme_toggle_button")
                    ) {
                        Icon(
                            if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle theme",
                            tint = colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Offline Status Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = when (statusMode) {
                            OfflineStatusMode.OFFLINE_READY -> Color(0xFFD1FAE5)
                            OfflineStatusMode.SYNC_AVAILABLE -> Color(0xFFFEF3C7)
                            OfflineStatusMode.ONLINE_ENHANCEMENT -> Color(0xFFE0F2FE)
                        },
                        modifier = Modifier.testTag("offline_status_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        when (statusMode) {
                                            OfflineStatusMode.OFFLINE_READY -> StatusGreen
                                            OfflineStatusMode.SYNC_AVAILABLE -> StatusAmber
                                            OfflineStatusMode.ONLINE_ENHANCEMENT -> Color(0xFF0284C7)
                                        },
                                        CircleShape
                                    )
                            )
                            Text(
                                text = when (statusMode) {
                                    OfflineStatusMode.OFFLINE_READY -> "OFFLINE READY"
                                    OfflineStatusMode.SYNC_AVAILABLE -> "SYNC READY"
                                    OfflineStatusMode.ONLINE_ENHANCEMENT -> "ONLINE OPT"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = when (statusMode) {
                                    OfflineStatusMode.OFFLINE_READY -> Color(0xFF065F46)
                                    OfflineStatusMode.SYNC_AVAILABLE -> Color(0xFF92400E)
                                    OfflineStatusMode.ONLINE_ENHANCEMENT -> Color(0xFF0369A1)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // OFFLINE DEMO SWITCH & ZERO DEPENDENCY BANNER
            Surface(
                color = if (isOfflineDemoMode) colorScheme.primaryContainer.copy(alpha = 0.4f) else colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.WifiOff,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = colorScheme.primary
                        )
                        Text(
                            text = if (isOfflineDemoMode)
                                "OFFLINE DEMO ACTIVE • Zero Internet Dependency"
                            else
                                "ONLINE SIMULATION ACTIVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp
                            ),
                            color = colorScheme.onSurface
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isOfflineDemoMode) "OFFLINE" else "ONLINE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                            color = if (isOfflineDemoMode) colorScheme.primary else colorScheme.onSurfaceVariant
                        )
                        Switch(
                            checked = isOfflineDemoMode,
                            onCheckedChange = { onToggleDemoMode() },
                            modifier = Modifier
                                .height(22.dp)
                                .testTag("offline_demo_toggle")
                        )
                    }
                }
            }
        }
    }
}
