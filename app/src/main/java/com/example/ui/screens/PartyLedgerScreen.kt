package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomerParty
import com.example.data.model.LedgerTransaction
import com.example.ui.components.AddTransactionDialog
import com.example.ui.components.WhatsAppReminderDialog
import com.example.ui.theme.ColorJama
import com.example.ui.theme.ColorUdhar
import com.example.ui.viewmodel.BillingViewModel
import com.example.util.AppStrings
import com.example.util.FormattingUtils
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyLedgerScreen(
    partyId: Long,
    viewModel: BillingViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val parties by viewModel.allParties.collectAsState()
    val party = parties.find { it.id == partyId }
    val transactionsFlow = remember(partyId) { viewModel.getTransactionsForParty(partyId) }
    val transactions by transactionsFlow.collectAsState()
    val profile by viewModel.businessProfile.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()

    var showTransactionDialog by remember { mutableStateOf(false) }
    var txnTypeToRecord by remember { mutableStateOf("GOT") } // "GAVE" or "GOT"
    var showReminderDialog by remember { mutableStateOf(false) }
    var transactionToDelete by remember { mutableStateOf<LedgerTransaction?>(null) }
    var showDeletePartyConfirm by remember { mutableStateOf(false) }

    val schoolName = profile?.name ?: "Vidya Vijay School"
    val schoolUpiId = profile?.upiId ?: "vidyavijayschool@sbi"

    if (party == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Student / Party record not found")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onBack) { Text("Go Back") }
            }
        }
        return
    }

    val isDue = party.currentBalance > 0
    val isAdvance = party.currentBalance < 0
    val balanceColor = when {
        isDue -> ColorUdhar
        isAdvance -> ColorJama
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = party.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${party.gradeOrCategory} • ${if (party.phone.isNotBlank()) party.phone else "No phone"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (party.phone.isNotBlank()) {
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${party.phone}"))
                            context.startActivity(intent)
                        }) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = "Call")
                        }
                    }
                    IconButton(onClick = { showDeletePartyConfirm = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Student Record",
                            tint = ColorUdhar
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // Big "+ You Gave (Due)" and "+ You Got (Jama)" action buttons
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            txnTypeToRecord = "GAVE"
                            showTransactionDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ColorUdhar),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("btn_you_gave_due")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "YOU GAVE (DUE)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = {
                            txnTypeToRecord = "GOT"
                            showTransactionDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ColorJama),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("btn_you_got_paid")
                    ) {
                        Icon(imageVector = Icons.Default.Payment, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "YOU GOT (PAID)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Running Balance Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isDue -> Color(0xFFFEF2F2)
                            isAdvance -> Color(0xFFECFDF5)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = when {
                                isDue -> "NET OUTSTANDING (STUDENT OWES)"
                                isAdvance -> "NET ADVANCE (PAID TO SCHOOL)"
                                else -> "ACCOUNT FULLY CLEARED"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = balanceColor.copy(alpha = 0.85f),
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = FormattingUtils.formatInr(abs(party.currentBalance)),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = balanceColor
                        )

                        if (party.guardianName.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Parent: ${party.guardianName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Reminder & Share Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (isDue) {
                                Button(
                                    onClick = { showReminderDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("WhatsApp Reminder", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    // Generate and share statement text
                                    val stmt = buildStatementText(party, transactions, schoolName)
                                    FormattingUtils.shareText(context, stmt, "Share Statement of ${party.name}")
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share Statement", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Ledger Transactions History Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transaction History (${transactions.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Opening: ${FormattingUtils.formatInr(party.openingBalance)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (transactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No transactions recorded yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Use the buttons below to record fee due or payment received.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(transactions, key = { it.id }) { txn ->
                    TransactionItemCard(
                        transaction = txn,
                        onDelete = { transactionToDelete = txn }
                    )
                }
            }
        }
    }

    // Add Transaction Dialog
    if (showTransactionDialog) {
        AddTransactionDialog(
            party = party,
            initialType = txnTypeToRecord,
            language = currentLanguage,
            onDismiss = { showTransactionDialog = false },
            onSave = { type, amount, mode, ref, cat, notes ->
                viewModel.recordTransaction(
                    partyId = party.id,
                    type = type,
                    amount = amount,
                    dateMillis = System.currentTimeMillis(),
                    paymentMode = mode,
                    referenceNo = ref,
                    categoryTag = cat,
                    notes = notes
                )
                showTransactionDialog = false
            }
        )
    }

    // WhatsApp Reminder Dialog
    if (showReminderDialog) {
        WhatsAppReminderDialog(
            party = party,
            schoolName = schoolName,
            schoolUpiId = schoolUpiId,
            language = currentLanguage,
            onDismiss = { showReminderDialog = false }
        )
    }

    // Delete Transaction Confirmation Dialog
    transactionToDelete?.let { txn ->
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Void Transaction?") },
            text = {
                Text("Are you sure you want to delete this ₹${txn.amount} (${txn.categoryTag}) entry? This will update the student's running balance.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTransaction(txn.id, party.id)
                        transactionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorUdhar)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) { Text("Cancel") }
            }
        )
    }

    // Delete Party Confirmation Dialog
    if (showDeletePartyConfirm) {
        AlertDialog(
            onDismissRequest = { showDeletePartyConfirm = false },
            title = { Text("Delete Student Record?") },
            text = {
                Text("Do you want to delete ${party.name} and all related ledger entries?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteParty(party) {
                            showDeletePartyConfirm = false
                            onBack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorUdhar)
                ) {
                    Text("Delete Record")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeletePartyConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun TransactionItemCard(
    transaction: LedgerTransaction,
    onDelete: () -> Unit
) {
    val isUdhar = transaction.type == "GAVE"
    val accentColor = if (isUdhar) ColorUdhar else ColorJama

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isUdhar) "DUE" else "PAID",
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.categoryTag,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${FormattingUtils.formatDateTime(transaction.dateMillis)} • ${transaction.paymentMode}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (transaction.referenceNo.isNotBlank()) {
                    Text(
                        text = "Ref: ${transaction.referenceNo}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
                if (transaction.notes.isNotBlank()) {
                    Text(
                        text = transaction.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = (if (isUdhar) "+ " else "- ") + FormattingUtils.formatInr(transaction.amount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun buildStatementText(
    party: CustomerParty,
    transactions: List<LedgerTransaction>,
    schoolName: String
): String {
    val sb = StringBuilder()
    sb.appendLine("===============================")
    sb.appendLine(schoolName)
    sb.appendLine("STUDENT FEE KHATA STATEMENT")
    sb.appendLine("===============================")
    sb.appendLine("Student: ${party.name}")
    sb.appendLine("Class/Roll: ${party.gradeOrCategory} / ${party.studentIdOrRoll}")
    sb.appendLine("Parent: ${party.guardianName}")
    sb.appendLine("Phone: ${party.phone}")
    sb.appendLine("Current Outstanding: ${FormattingUtils.formatInr(party.currentBalance)}")
    sb.appendLine("-------------------------------")
    sb.appendLine("TRANSACTIONS:")
    transactions.forEach { t ->
        val tag = if (t.type == "GAVE") "DUE (+)" else "PAID (-)"
        sb.appendLine("${FormattingUtils.formatDate(t.dateMillis)} | $tag ${FormattingUtils.formatInr(t.amount)} | ${t.categoryTag} [${t.paymentMode}]")
    }
    sb.appendLine("===============================")
    sb.appendLine("Generated by Vidya Vijay School Billing App")
    return sb.toString()
}
