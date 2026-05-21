package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DailyUpdateEntity
import com.example.ui.DevHubViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DailyFeedScreen(viewModel: DevHubViewModel) {
    val dailyUpdates by viewModel.dailyUpdatesStream.collectAsState()
    val activeStack by viewModel.activeStack.collectAsState()
    val isGeneratingInsight by viewModel.isGeneratingInsight.collectAsState()
    val aiInsight by viewModel.aiInsight.collectAsState()

    var showSimulateDialog by remember { mutableStateOf(false) }
    var simTitle by remember { mutableStateOf("") }
    var simCategory by remember { mutableStateOf("AI Launch") }
    var simDescription by remember { mutableStateOf("") }
    var simImpact by remember { mutableStateOf("") }

    val simCategories = listOf("AI Launch", "Enterprise Beta", "SDK Release", "API Update")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Geometric Balance Hero Banner: Daily AI Pulse
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = Color(0xFFD0BCFE),
                            contentColor = Color(0xFF381E72),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "NEW RELEASE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "• Today 09:42 AM",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Gemini 1.5 Pro: Advanced Context Caching for Enterprise",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily AI Pulse",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                        Button(
                            onClick = { uriHandler.openUri("https://ai.google.dev/gemini-api/docs/caching") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFD0BCFE),
                                contentColor = Color(0xFF381E72)
                            ),
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                            modifier = Modifier.heightIn(max = 32.dp)
                        ) {
                            Text("Read Docs", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // AI Dev-Rel Assistant & Interactive Analyst Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = "AI Assistance",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Interactive DevAI Analyst & Consultant",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Customize your profile to receive automated, exclusive architectural recommendations, api doc insights, and daily launch analysis based on your tech stack.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    TextField(
                        value = activeStack,
                        onValueChange = { viewModel.activeStack.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tech_stack_input"),
                        label = { Text("Active Tech Stack / Ecosystem", fontSize = 11.sp) },
                        placeholder = { Text("e.g., Kotlin, GCP VMs, Node.js, Python") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.generateDeveloperInsight(6) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("generate_insights_btn"),
                        enabled = !isGeneratingInsight,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isGeneratingInsight) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Querying Gemini API...")
                        } else {
                            Icon(Icons.Default.TipsAndUpdates, contentDescription = "Query model")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Fetch Tailored Daily Updates & Insights")
                        }
                    }

                    // Display AI Output
                    aiInsight?.let { insight ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "TAILORED TECHNICAL SPEC SHEET",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    IconButton(
                                        onClick = { viewModel.generateDeveloperInsight(6) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Refresh,
                                            contentDescription = "Regenerate",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = insight,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        Icons.Default.BookmarkBorder,
                                        contentDescription = "Disclaimer",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Powered by Gemini 3.5 Flash directly via REST API.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section Title: Launches List & Alerts Panel
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Google AI & Developer Timeline Launches",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Updates & releases broadcasted live",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Push Alert Simulator Floating Button
                FilledTonalButton(
                    onClick = { showSimulateDialog = true },
                    modifier = Modifier.testTag("simulate_alert_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = "Simulate")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Simulate Launch", fontSize = 11.sp)
                }
            }
        }

        // Daily Updates Render List
        if (dailyUpdates.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No Google launches documented yet. Press Simulate to test alerts!",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            items(dailyUpdates, key = { it.id }) { update ->
                LaunchTimelineCard(update = update, onMarkRead = {
                    viewModel.markUpdateAsRead(update.id)
                })
            }
        }
    }

    // Simulation PopUp dialog
    if (showSimulateDialog) {
        AlertDialog(
            onDismissRequest = { showSimulateDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CrisisAlert,
                        contentDescription = "Form",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simulate New Release Alert")
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Create a custom mock launch announcement. It will generate an on-device push notification alert and append directly into your live list.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = simTitle,
                        onValueChange = { simTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Release Title") },
                        placeholder = { Text("e.g., Maps SDK 3D Mesh Engine Launch") }
                    )

                    // Category dropdown like buttons
                    Text("Select Release Category:")
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        simCategories.forEach { category ->
                            val isSel = simCategory == category
                            FilterChip(
                                selected = isSel,
                                onClick = { simCategory = category },
                                label = { Text(category) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = simDescription,
                        onValueChange = { simDescription = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Overview & Features Description") },
                        placeholder = { Text("Highly responsive vector renders...") }
                    )

                    OutlinedTextField(
                        value = simImpact,
                        onValueChange = { simImpact = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("API Infrastructure Technical Impact") },
                        placeholder = { Text("Trims cellular GPU decoding workload by 40%.") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (simTitle.isNotEmpty() && simDescription.isNotEmpty()) {
                            viewModel.simulateAlert(
                                title = simTitle,
                                category = simCategory,
                                description = simDescription,
                                techImpact = simImpact
                            )
                            showSimulateDialog = false
                            simTitle = ""
                            simDescription = ""
                            simImpact = ""
                        }
                    },
                    modifier = Modifier.testTag("submit_mock_alert_btn")
                ) {
                    Text("Post & Broadcast")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSimulateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun LaunchTimelineCard(update: DailyUpdateEntity, onMarkRead: () -> Unit) {
    val categoryColor = when (update.category) {
        "AI Tool Release", "AI Launch" -> Color(0xFF8AB4F8)
        "Beta Launch", "Enterprise Beta" -> Color(0xFFFDE293)
        "Major Security Update", "SDK Release" -> Color(0xFF81C995)
        else -> Color(0xFFE8EAED)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (update.isRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        border = BorderStroke(
            1.dp,
            if (update.isRead) MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Tag
                Surface(
                    color = categoryColor.copy(alpha = 0.15f),
                    contentColor = categoryColor,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = update.category,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Timing & Read Flag
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = update.releaseDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (!update.isRead) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = update.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (update.isRead) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = update.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Technical details panel
            if (update.techImpact.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Bolt,
                            contentDescription = "Impact",
                            tint = Color(0xFFFDE293),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "API / INFRASTRUCTURE IMPACT FOR DEV TEAMS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 9.sp
                            )
                            Text(
                                text = update.techImpact,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Click Actions: Mark as Read
            if (!update.isRead) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onMarkRead,
                        modifier = Modifier.sizeIn(minHeight = 32.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Read", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Acknowledge Alerts", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
