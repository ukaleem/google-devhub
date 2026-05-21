package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BountyEntity
import com.example.ui.DevHubViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BountiesScreen(viewModel: DevHubViewModel) {
    val bounties by viewModel.bountiesStream.collectAsState()
    val context = LocalContext.current

    // Local State for selected filter
    var selectedDifficultyFilter by remember { mutableStateOf("All") }
    var expandedBountyId by remember { mutableStateOf<String?>(null) }

    // State for PR Submission Form
    val prInputMap = remember { mutableStateMapOf<String, String>() }
    val notesInputMap = remember { mutableStateMapOf<String, String>() }

    // Computations from Room Flow
    val totalEarned = bounties.filter { it.status == "COMPLETED" }.sumOf { it.reward }
    val completedCount = bounties.count { it.status == "COMPLETED" }
    val activeCount = bounties.count { it.status == "CLAIMED" || it.status == "SUBMITTED" }

    val filteredList = bounties.filter { bounty ->
        selectedDifficultyFilter == "All" || bounty.difficulty.equals(selectedDifficultyFilter, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Geometric Dashboard Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("bounty_dashboard_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "OPEN SOURCE REWARDS MODULE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Developer Wallet",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$${String.format("%.2f", totalEarned)} USD",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.testTag("bounty_total_earned")
                        )
                    }
                    Surface(
                        color = Color(0xFFD0BCFE),
                        contentColor = Color(0xFF381E72),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = "Developer Earning icon", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Earn Easily", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Completed Column
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Completed & Paid",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "$completedCount Projects",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // In Progress / Submitted Column
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Active Participations",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "$activeCount Codebases",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Section Title & Dynamic Filter Row
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Participate & Earn",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Pick active developer micro-tasks. Claim, submit, and earn directly into your wallet.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Scrollable Pill Filters
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bounty_filters_row"),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                val filtersList = listOf("All", "Easy", "Medium", "Hard")
                items(filtersList) { filterLabel ->
                    val isSelected = selectedDifficultyFilter == filterLabel
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedDifficultyFilter = filterLabel },
                        label = { Text(filterLabel) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.height(36.dp)
                    )
                }
            }
        }

        // Bounties Lists
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.Code,
                        contentDescription = "Empty Bounties",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Bounties Found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "There are no active open source projects matching '$selectedDifficultyFilter' difficulty.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList, key = { it.id }) { bounty ->
                    val isExpanded = expandedBountyId == bounty.id

                    BountyItemCard(
                        bounty = bounty,
                        isExpanded = isExpanded,
                        onCardClick = {
                            expandedBountyId = if (isExpanded) null else bounty.id
                        },
                        onClaimClick = {
                            viewModel.claimBounty(bounty.id)
                            Toast.makeText(context, "Bounty Claimed! Start coding.", Toast.LENGTH_SHORT).show()
                        },
                        prUrl = prInputMap[bounty.id] ?: "",
                        onPrUrlChange = { prInputMap[bounty.id] = it },
                        notes = notesInputMap[bounty.id] ?: "",
                        onNotesChange = { notesInputMap[bounty.id] = it },
                        onSubmitClick = { url, note ->
                            if (url.isEmpty()) {
                                Toast.makeText(context, "Please provide a Pull Request URL", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.submitBountyPR(bounty.id, url, note)
                                Toast.makeText(context, "Pull Request Submitted! Awaiting maintainer review.", Toast.LENGTH_LONG).show()
                            }
                        },
                        onApproveClick = {
                            viewModel.rewardBountyInstantly(bounty.id)
                            Toast.makeText(context, "Code Approved! $${String.format("%.2f", bounty.reward)} added to your wallet.", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BountyItemCard(
    bounty: BountyEntity,
    isExpanded: Boolean,
    onCardClick: () -> Unit,
    onClaimClick: () -> Unit,
    prUrl: String,
    onPrUrlChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    onSubmitClick: (String, String) -> Unit,
    onApproveClick: () -> Unit
) {
    val difficultyColor = when (bounty.difficulty.lowercase()) {
        "easy" -> Color(0xFF4CAF50)
        "medium" -> Color(0xFF2196F3)
        "hard" -> Color(0xFFFF9800)
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bounty_card_${bounty.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (bounty.status) {
                "COMPLETED" -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                "SUBMITTED" -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f)
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
            }
        ),
        border = BorderStroke(
            width = 1.dp,
            color = when (bounty.status) {
                "COMPLETED" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                "SUBMITTED" -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCardClick() }
                .padding(16.dp)
        ) {
            // Header Row: Category Badge & Reward Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = bounty.project.uppercase(),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        color = difficultyColor.copy(alpha = 0.15f),
                        contentColor = difficultyColor,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = bounty.difficulty.uppercase(),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "$${String.format("%.2f", bounty.reward)}",
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("bounty_reward_${bounty.id}")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bounty Title
            Text(
                text = bounty.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Brief Description
            Text(
                text = bounty.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Skills & Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Tag
                Surface(
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = "🏷️ ${bounty.category}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Status Badge
                Surface(
                    color = when (bounty.status) {
                        "OPEN" -> Color(0xFFE8F5E9)
                        "CLAIMED" -> Color(0xFFFFF3E0)
                        "SUBMITTED" -> Color(0xFFE3F2FD)
                        "COMPLETED" -> Color(0xFFEDE7F6)
                        else -> Color.DarkGray
                    },
                    contentColor = when (bounty.status) {
                        "OPEN" -> Color(0xFF2E7D32)
                        "CLAIMED" -> Color(0xFFEF6C00)
                        "SUBMITTED" -> Color(0xFF1565C0)
                        "COMPLETED" -> Color(0xFF673AB7)
                        else -> Color.White
                    },
                    shape = RoundedCornerShape(50),
                ) {
                    val statusText = when (bounty.status) {
                        "OPEN" -> "AVAILABLE"
                        "CLAIMED" -> "IN PROGRESS"
                        "SUBMITTED" -> "UNDER REVIEW"
                        "COMPLETED" -> "PAID"
                        else -> bounty.status
                    }
                    Text(
                        text = statusText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .testTag("bounty_status_${bounty.id}")
                    )
                }
            }

            // Expanded Work Area
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "REQUIREMENTS TO SOLVE:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Requirements bullets
                    bounty.requirements.split(",").forEach { req ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(text = "• ", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            Text(text = req.trim(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "SKILLS NEEDED:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(text = bounty.skills, style = MaterialTheme.typography.bodySmall, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)

                    Spacer(modifier = Modifier.height(16.dp))

                    // ACTION FLOW
                    when (bounty.status) {
                        "OPEN" -> {
                            Button(
                                onClick = onClaimClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 48.dp)
                                    .testTag("claim_bounty_btn_${bounty.id}"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Claim This Project & Start Coding", fontWeight = FontWeight.Bold)
                            }
                        }
                        "CLAIMED" -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "PR Submit Workspace",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                TextField(
                                    value = prUrl,
                                    onValueChange = onPrUrlChange,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("bounty_pr_input_${bounty.id}"),
                                    placeholder = { Text("https://github.com/project/pull/123") },
                                    label = { Text("GitHub Pull Request URL") },
                                    singleLine = true,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                TextField(
                                    value = notes,
                                    onValueChange = onNotesChange,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("bounty_notes_input_${bounty.id}"),
                                    placeholder = { Text("Describe changes, improvements, and fixes...") },
                                    label = { Text("Engineering Notes / Explanations") },
                                    maxLines = 3,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = { onSubmitClick(prUrl, notes) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 48.dp)
                                        .testTag("submit_pr_btn_${bounty.id}"),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Submit My Solution", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        "SUBMITTED" -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(14.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Awaiting Maintainer Code-Review",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "PR: ${bounty.prUrl}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                                if (bounty.submissionNotes.isNotEmpty()) {
                                    Text(
                                        text = "Notes: ${bounty.submissionNotes}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                                Spacer(modifier = Modifier.height(10.dp))

                                // Simulator maintainer approval button
                                Button(
                                    onClick = onApproveClick,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 48.dp)
                                        .testTag("approve_bounty_btn_${bounty.id}"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF2E7D32),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Simulate Code Approval & Payout", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        "COMPLETED" -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
                                    .padding(14.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Approved icon", tint = Color(0xFF2E7D32))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Contribution Approved & Balance Disbursed!",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Solution accepted inside remote repository: ${bounty.project}.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF2E7D32)
                                )
                                if (bounty.prUrl.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "PR: ${bounty.prUrl}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF2E7D32),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
