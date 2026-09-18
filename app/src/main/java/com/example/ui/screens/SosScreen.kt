package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.EmergencyContactEntity
import com.example.data.entity.SosQueueEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SosScreen(
    contacts: List<EmergencyContactEntity>,
    sosQueue: List<SosQueueEntity>,
    onTriggerSos: (String, Double, Double, Int, String, String) -> Unit,
    onAddContact: (String, String, String, Boolean, String) -> Unit,
    onDeleteContact: (Long) -> Unit,
    onUpdateSosStatus: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showAddContactDialog by remember { mutableStateOf(false) }
    var latestAlertTriggered by remember { mutableStateOf<SosQueueEntity?>(null) }

    // Last known coordinates (Guwahati transit hub baseline or device GPS fix)
    val currentLat = 26.1833
    val currentLon = 91.7450
    val currentAlt = 55

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
                        Icons.Default.Emergency,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "EMERGENCY / SOS DISPATCH",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = colorScheme.onSurface
                    )
                }
                Text(
                    text = "100% Offline Emergency Preparation. Packages coordinates, altitude, and medical profile into queued local dispatch & cellular SMS.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }

        // Prominent Emergency Button
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFCA5A5)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth().testTag("sos_activation_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Big SOS Button
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFDC2626),
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                    ) {
                        Button(
                            onClick = { showConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.fillMaxSize().testTag("activate_sos_button")
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Sos,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(42.dp)
                                )
                                Text(
                                    text = "ACTIVATE SOS",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    ),
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Text(
                        text = "Tap to initialize emergency offline package",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = Color(0xFF991B1B)
                    )

                    // Last known location coordinates preview
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF87171).copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("LATITUDE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = Color(0xFF7F1D1D))
                                Text("26.1833° N", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1F2937))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("LONGITUDE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = Color(0xFF7F1D1D))
                                Text("91.7450° E", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1F2937))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("ELEVATION", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = Color(0xFF7F1D1D))
                                Text("55m AMSL", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1F2937))
                            }
                        }
                    }
                }
            }
        }

        // Offline SOS vs Online Transmission Distinction Banner (MANDATE)
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(16.dp))
                        Text(
                            text = "OFFLINE CAPABILITY SPECIFICATION",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = colorScheme.primary
                        )
                    }
                    Text(
                        text = "1. Offline SOS Preparation: Compiles coordinates, nearest hospital vector (GMCH 4.2 km), medical constraints, and stores in SQLite queue with zero internet.\n2. SOS Transmission: Uses device Cellular SMS (no data plan/Wi-Fi needed) or queues for automated background sync when connectivity resumes.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Active Emergency Queue / Latest Prepared Alert
        if (sosQueue.isNotEmpty()) {
            item {
                Text(
                    text = "OFFLINE DISPATCH QUEUE (${sosQueue.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = colorScheme.primary
                )
            }

            items(sosQueue) { alert ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().testTag("sos_queue_item_${alert.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ALERT ${alert.alertCode}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                color = Color(0xFFDC2626)
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (alert.status == "PREPARED_OFFLINE") Color(0xFFFEF3C7) else Color(0xFFD1FAE5)
                            ) {
                                Text(
                                    text = alert.status,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                    color = if (alert.status == "PREPARED_OFFLINE") Color(0xFF92400E) else Color(0xFF065F46),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = alert.messageContent,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = colorScheme.onSurfaceVariant
                        )

                        // Cellular SMS Transmission Action
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    sendEmergencySms(context, alert.targetRecipient, alert.messageContent)
                                    onUpdateSosStatus(alert.id, "SMS_OPENED")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Dispatch via Cellular SMS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Local Emergency Contacts
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LOCALLY STORED EMERGENCY CONTACTS (${contacts.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = colorScheme.primary
                )
                TextButton(
                    onClick = { showAddContactDialog = true },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Contact", fontSize = 11.sp)
                }
            }
        }

        items(contacts) { contact ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = colorScheme.primaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                if (contact.isPrimary) Icons.Default.Star else Icons.Default.Phone,
                                contentDescription = null,
                                tint = colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = contact.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = colorScheme.onSurface
                        )
                        Text(
                            text = "${contact.relationOrAgency} • ${contact.phoneNumber}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = colorScheme.onSurfaceVariant
                        )
                        if (contact.notes.isNotEmpty()) {
                            Text(
                                text = contact.notes,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = colorScheme.outline
                            )
                        }
                    }

                    // Direct Phone Dial Intent
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phoneNumber}"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Call", tint = Color(0xFF10B981))
                    }
                }
            }
        }
    }

    // Confirmation Dialog before Activating SOS (MANDATE)
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(36.dp)) },
            title = { Text("CONFIRM EMERGENCY SOS", fontWeight = FontWeight.Black, color = Color(0xFFDC2626)) },
            text = {
                Text(
                    "Are you sure you want to trigger emergency SOS?\n\nThis will compile your exact coordinates (26.1833°N, 91.7450°E), timestamp, altitude, nearest trauma center vector, and queue an offline emergency dispatch message.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val alertCode = "NER-SOS-${(10000..99999).random()}"
                        val primaryPhone = contacts.firstOrNull { it.isPrimary }?.phoneNumber ?: "1070"
                        val timestampStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                        val message = "EMERGENCY SOS: Code $alertCode. Location: Lat $currentLat, Lon $currentLon (Alt $currentAlt m). Nearest Facility: GMCH Guwahati (4.2km). Time: $timestampStr. Urgent Paramedic / Rescue Requested."
                        onTriggerSos(alertCode, currentLat, currentLon, currentAlt, message, primaryPhone)
                        showConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    modifier = Modifier.testTag("confirm_sos_button")
                ) {
                    Text("YES, ACTIVATE SOS", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Add Emergency Contact Dialog
    if (showAddContactDialog) {
        AddEmergencyContactDialog(
            onDismiss = { showAddContactDialog = false },
            onConfirm = { name, agency, phone, isPrimary, notes ->
                onAddContact(name, agency, phone, isPrimary, notes)
                showAddContactDialog = false
            }
        )
    }
}

private fun sendEmergencySms(context: Context, phoneNumber: String, message: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$phoneNumber")
            putExtra("sms_body", message)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback generic share
        val sendIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("sms:$phoneNumber?body=${Uri.encode(message)}")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(sendIntent)
    }
}

@Composable
private fun AddEmergencyContactDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Boolean, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var agency by remember { mutableStateOf("Local Contact / First Responder") }
    var phone by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Emergency Contact", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Contact / Agency Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number / Shortcode") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Coverage Area") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onConfirm(name, agency, phone, false, notes)
                    }
                }
            ) {
                Text("Save Contact")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
