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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PerkEntity
import com.example.ui.DevHubViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PerksScreen(viewModel: DevHubViewModel) {
    val matchedPerks by viewModel.matchedPerks.collectAsState()
    val activeStack by viewModel.activeStack.collectAsState()
    val companySize by viewModel.companySize.collectAsState()

    val companySizeOptions = listOf(
        "Individual Developer",
        "Early Prototype Stage",
        "Startup (< 5 people)",
        "Enterprise Group"
    )

    val claimCount = matchedPerks.count { it.first.claimedStatus == "CLAIMED" }
    val totalCreditsWorth = matchedPerks.filter { it.first.claimedStatus == "CLAIMED" }.sumOf {
        when (it.first.id) {
            "perk_gcp_startup" -> 200000
            "perk_firebase_spark" -> 1500
            "perk_gemini_trial" -> 1200
            "perk_maps_credits" -> 2400
            "perk_cloud_boost" -> 660
            else -> 0
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dynamic Matcher Panel - Dashboard
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Professional Perks Match Center",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "We aggregate elite Cloud infra and API testing credits normally locked behind corporate startup desks.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "REDEMPTION DASHBOARD",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.WorkspacePremium,
                                    contentDescription = "Tier",
                                    tint = Color(0xFFFDE293),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$claimCount Active Trials Connected",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "EST. TRIAL VALUE SAVED",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "$${String.format("%,d", totalCreditsWorth)} / yr",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Interactive Eligibility Form Elements
                    Text(
                        text = "Active Workspace Configuration",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Organization Scale & Legal Status", style = MaterialTheme.typography.labelMedium)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        companySizeOptions.forEach { option ->
                            val isSel = companySize == option
                            FilterChip(
                                selected = isSel,
                                onClick = { viewModel.companySize.value = option },
                                label = { Text(option, fontSize = 11.sp) },
                                modifier = Modifier.testTag("perks_chip_${option.lowercase().replace(" ", "_")}")
                            )
                        }
                    }
                }
            }
        }

        // Section Title: Match Results
        item {
            Column {
                Text(
                    text = "Aggregated Enterprise Tiers & Sandbox Plans",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Screening matched for stack: '$activeStack'",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Render matched perks catalog
        items(matchedPerks, key = { it.first.id }) { (perk, isMatch) ->
            PerkCard(
                perk = perk,
                isMatchedScore = isMatch,
                onClaimToggle = {
                    val targetStatus = if (perk.claimedStatus == "CLAIMED") "ELIGIBLE" else "CLAIMED"
                    viewModel.updatePerkStatus(perk.id, targetStatus)
                }
            )
        }
    }
}

@Composable
fun PerkCard(perk: PerkEntity, isMatchedScore: Boolean, onClaimToggle: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val isRedeemed = perk.claimedStatus == "CLAIMED"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("perk_card_${perk.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isRedeemed) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
            else if (isMatchedScore) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        ),
        border = BorderStroke(
            1.dp,
            if (isRedeemed) MaterialTheme.colorScheme.primary
            else if (isMatchedScore) MaterialTheme.colorScheme.outline
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Perks Provider Info & Match Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = perk.provider.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = perk.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Match Status badge
                Surface(
                    color = if (isRedeemed) Color(0xFF81C995).copy(alpha = 0.15f)
                    else if (isMatchedScore) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                    contentColor = if (isRedeemed) Color(0xFF81C995)
                    else if (isMatchedScore) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isRedeemed) "Connected" else if (isMatchedScore) "100% Match" else "Partial Match",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Benefits Metrics Box
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CardGiftcard,
                    contentDescription = "Value worth",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = perk.valueDescription,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = "Availability Period: ${perk.trialPeriod}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Detailed Overview Panel
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Program Details",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = perk.benefitDetails,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            // Legal / Eligibility check rules
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Eligibility Criteria Checklist:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Default.Verified,
                    contentDescription = "Eligible check",
                    tint = if (isMatchedScore) Color(0xFF81C995) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier
                        .size(16.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = perk.eligibilityCriteria,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Action Row: Official Registration & Match State Toggle
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { uriHandler.openUri(perk.docUrl) },
                    modifier = Modifier.testTag("official_program_btn_${perk.id}")
                ) {
                    Text("Official Form portal", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.OpenInNew, contentDescription = "External link", modifier = Modifier.size(12.dp))
                }

                Button(
                    onClick = onClaimToggle,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRedeemed) Color(0xFF81C995) else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("claim_btn_${perk.id}")
                ) {
                    Icon(
                        imageVector = if (isRedeemed) Icons.Default.CheckCircle else Icons.Default.Add,
                        contentDescription = "Claim status modifier",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isRedeemed) "Connected" else "Activate Trial Perks", fontSize = 12.sp)
                }
            }
        }
    }
}
