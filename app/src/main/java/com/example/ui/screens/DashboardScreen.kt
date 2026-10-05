package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomerParty
import com.example.ui.components.AddPartyDialog
import com.example.ui.components.CustomerPartyRow
import com.example.ui.components.SummaryStatCard
import com.example.ui.components.WhatsAppReminderDialog
import com.example.ui.theme.ColorJama
import com.example.ui.theme.ColorPending
import com.example.ui.theme.ColorUdhar
import com.example.ui.viewmodel.BillingViewModel
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.FormattingUtils

@Composable
fun DashboardScreen(
    viewModel: BillingViewModel,
    onNavigateToPartyLedger: (Long) -> Unit,
    onNavigateToInvoices: () -> Unit,
    onNavigateToUpiQr: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.businessProfile.collectAsState()
    val summary by viewModel.dashboardSummary.collectAsState()
    val filteredParties by viewModel.filteredParties.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()

    var showAddPartyDialog by remember { mutableStateOf(false) }
    var partyForReminder by remember { mutableStateOf<CustomerParty?>(null) }

    val schoolName = profile?.name ?: "Vidya Vijay School"
    val schoolUpiId = profile?.upiId ?: "vidyavijayschool@sbi"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Top School Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E3A8A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = schoolName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Text(
                            text = "${profile?.codeOrAffiliation ?: "CBSE Affil. 830492"} • ${profile?.activeAcademicYear ?: "2026-2027"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    // Role Chip
                    Surface(
                        color = Color(0xFF1E3A8A),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = profile?.currentRole ?: "Principal",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Summary KPI Stats Grid
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryStatCard(
                        title = AppStrings.get("today_collection", currentLanguage),
                        amount = FormattingUtils.formatInr(summary.todayCollection),
                        subtitle = "Today's fee receipts",
                        icon = Icons.Default.ArrowDownward,
                        containerColor = Color(0xFFECFDF5),
                        contentColor = Color(0xFF065F46),
                        accentColor = ColorJama,
                        modifier = Modifier.weight(1f),
                        testTag = "stat_today_collection"
                    )

                    SummaryStatCard(
                        title = AppStrings.get("total_pending_due", currentLanguage),
                        amount = FormattingUtils.formatInr(summary.totalPendingUdhar),
                        subtitle = "Unpaid fee dues",
                        icon = Icons.Default.ArrowUpward,
                        containerColor = Color(0xFFFEF2F2),
                        contentColor = Color(0xFF991B1B),
                        accentColor = ColorUdhar,
                        modifier = Modifier.weight(1f),
                        testTag = "stat_pending_dues"
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryStatCard(
                        title = AppStrings.get("total_advance_got", currentLanguage),
                        amount = FormattingUtils.formatInr(summary.totalAdvanceJama),
                        subtitle = "Prepaid / Advance",
                        icon = Icons.Default.AttachMoney,
                        containerColor = Color(0xFFEFF6FF),
                        contentColor = Color(0xFF1E40AF),
                        accentColor = Color(0xFF2563EB),
                        modifier = Modifier.weight(1f)
                    )

                    SummaryStatCard(
                        title = "Monthly Expenses",
                        amount = FormattingUtils.formatInr(summary.totalMonthlyExpenses),
                        subtitle = "Staff, buses & utilities",
                        icon = Icons.Default.Receipt,
                        containerColor = Color(0xFFFFFBEB),
                        contentColor = Color(0xFF92400E),
                        accentColor = ColorPending,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Actions Row
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = AppStrings.get("quick_actions", currentLanguage),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showAddPartyDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_add_student_quick")
                    ) {
                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(AppStrings.get("add_student", currentLanguage))
                    }

                    Button(
                        onClick = onNavigateToInvoices,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E3A8A)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(AppStrings.get("create_bill", currentLanguage))
                    }

                    Button(
                        onClick = onNavigateToUpiQr,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0F766E)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.QrCode2, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(AppStrings.get("upi_collection", currentLanguage))
                    }
                }
            }
        }

        // Search Bar & Filter Chips
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text(AppStrings.get("search_hint", currentLanguage), fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_party_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filters = listOf(
                        "ALL" to AppStrings.get("all", currentLanguage),
                        "DUES_ONLY" to AppStrings.get("dues_only", currentLanguage),
                        "ADVANCE_ONLY" to AppStrings.get("advance_only", currentLanguage),
                        "CLEARED" to AppStrings.get("cleared_only", currentLanguage),
                        "CLASS10" to "Class 10",
                        "CLASS9" to "Class 9",
                        "HOSTEL" to "Hostel",
                        "VENDOR" to "Vendors"
                    )

                    filters.forEach { (key, label) ->
                        FilterChip(
                            selected = selectedFilter == key,
                            onClick = { viewModel.setSelectedFilter(key) },
                            label = { Text(label, fontSize = 12.sp) }
                        )
                    }
                }
            }
        }

        // Students / Customer Party List Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Students & Parties (${filteredParties.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Tap to view ledger",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Students & Parties List
        if (filteredParties.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No students or parties match this filter",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try clearing the search or add a new student using the button above.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filteredParties, key = { it.id }) { party ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    CustomerPartyRow(
                        party = party,
                        onClick = { onNavigateToPartyLedger(party.id) },
                        onSendReminder = { partyForReminder = party },
                        language = currentLanguage
                    )
                }
            }
        }
    }

    // Add Party Dialog
    if (showAddPartyDialog) {
        AddPartyDialog(
            language = currentLanguage,
            onDismiss = { showAddPartyDialog = false },
            onSave = { name, sId, guardian, phone, cat, balance, bType, addr, notes ->
                viewModel.addParty(
                    name = name,
                    studentId = sId,
                    guardianName = guardian,
                    phone = phone,
                    gradeOrCategory = cat,
                    openingBalance = balance,
                    balanceType = bType,
                    address = addr,
                    notes = notes
                )
                showAddPartyDialog = false
            }
        )
    }

    // WhatsApp Reminder Dialog
    partyForReminder?.let { party ->
        WhatsAppReminderDialog(
            party = party,
            schoolName = schoolName,
            schoolUpiId = schoolUpiId,
            language = currentLanguage,
            onDismiss = { partyForReminder = null }
        )
    }
}
