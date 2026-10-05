package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomerParty
import com.example.ui.theme.ColorJama
import com.example.ui.theme.ColorPending
import com.example.ui.theme.ColorUdhar
import com.example.ui.viewmodel.BillingViewModel
import com.example.util.FormattingUtils

@Composable
fun ReportsScreen(
    viewModel: BillingViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val parties by viewModel.allParties.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()
    val profile by viewModel.businessProfile.collectAsState()

    val schoolName = profile?.name ?: "Vidya Vijay School"

    // Outstanding Dues Aging calculation (0-30 days, 31-60 days, 60+ days)
    val now = System.currentTimeMillis()
    val day = 86400000L

    var aging0To30 = 0.0
    var aging31To60 = 0.0
    var aging60Plus = 0.0

    parties.filter { it.currentBalance > 0 }.forEach { party ->
        val partyTxns = transactions.filter { it.partyId == party.id && it.type == "GAVE" }
        val oldestDueTime = partyTxns.minOfOrNull { it.dateMillis } ?: party.createdAt
        val ageDays = ((now - oldestDueTime) / day).coerceAtLeast(0)

        when {
            ageDays <= 30 -> aging0To30 += party.currentBalance
            ageDays <= 60 -> aging31To60 += party.currentBalance
            else -> aging60Plus += party.currentBalance
        }
    }

    val totalDues = aging0To30 + aging31To60 + aging60Plus
    val topDefaulters = parties.filter { it.currentBalance > 0 }.sortedByDescending { it.currentBalance }.take(5)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("reports_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Reports & Analytics",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Fee collections, aging dues and audits",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = {
                        val csv = generateCsvExport(parties, schoolName)
                        FormattingUtils.shareText(context, csv, "Export Vidya Vijay Khata Data")
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export CSV")
                }
            }
        }

        // Outstanding Aging Report Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "OUTSTANDING AGING ANALYSIS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Total: ${FormattingUtils.formatInr(totalDues)}",
                            fontWeight = FontWeight.Bold,
                            color = ColorUdhar
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 0-30 Days
                    AgingBarRow(
                        label = "0 - 30 Days (Current)",
                        amount = aging0To30,
                        total = totalDues,
                        barColor = Color(0xFF3B82F6)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 31-60 Days
                    AgingBarRow(
                        label = "31 - 60 Days (Follow-up)",
                        amount = aging31To60,
                        total = totalDues,
                        barColor = ColorPending
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 60+ Days
                    AgingBarRow(
                        label = "60+ Days (Critical Due)",
                        amount = aging60Plus,
                        total = totalDues,
                        barColor = ColorUdhar
                    )
                }
            }
        }

        // Top Defaulters / Highest Balances
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "TOP OUTSTANDING DUES (BY STUDENT)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (topDefaulters.isEmpty()) {
                        Text("No outstanding dues pending! Excellent fee collection.", color = ColorJama)
                    } else {
                        topDefaulters.forEachIndexed { idx, p ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("${idx + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(p.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Text("${p.gradeOrCategory} • ${p.phone}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Text(
                                    text = FormattingUtils.formatInr(p.currentBalance),
                                    fontWeight = FontWeight.Bold,
                                    color = ColorUdhar,
                                    fontSize = 14.sp
                                )
                            }
                            if (idx < topDefaulters.size - 1) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }

        // Quick Export / Full Statement Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Backup & Data Export",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Export the entire ledger of Vidya Vijay School as a CSV spreadsheet or share financial summaries.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val csv = generateCsvExport(parties, schoolName)
                            FormattingUtils.shareText(context, csv, "Vidya Vijay School Ledger Export")
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Complete Khata Summary")
                    }
                }
            }
        }
    }
}

@Composable
private fun AgingBarRow(
    label: String,
    amount: Double,
    total: Double,
    barColor: Color
) {
    val fraction = if (total > 0) (amount / total).toFloat().coerceIn(0f, 1f) else 0f

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            Text(FormattingUtils.formatInr(amount), fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(barColor)
            )
        }
    }
}

private fun generateCsvExport(parties: List<CustomerParty>, schoolName: String): String {
    val sb = StringBuilder()
    sb.appendLine("Vidya Vijay School - Student Ledger Database Export")
    sb.appendLine("School: $schoolName")
    sb.appendLine("Exported on: ${FormattingUtils.formatDateTime(System.currentTimeMillis())}")
    sb.appendLine()
    sb.appendLine("Student / Party ID,Student Name,Roll No,Class / Category,Parent Name,Phone,Current Balance (INR),Balance Status")
    parties.forEach { p ->
        val status = if (p.currentBalance > 0) "DUE" else if (p.currentBalance < 0) "ADVANCE" else "CLEARED"
        sb.appendLine("\"${p.id}\",\"${p.name}\",\"${p.studentIdOrRoll}\",\"${p.gradeOrCategory}\",\"${p.guardianName}\",\"${p.phone}\",${p.currentBalance},\"$status\"")
    }
    return sb.toString()
}
