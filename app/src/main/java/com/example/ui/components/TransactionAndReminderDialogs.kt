package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CustomerParty
import com.example.ui.theme.ColorJama
import com.example.ui.theme.ColorUdhar
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.FormattingUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    party: CustomerParty,
    initialType: String, // "GAVE" (Udhar / Fee Due) or "GOT" (Jama / Fee Paid)
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (
        type: String,
        amount: Double,
        paymentMode: String,
        referenceNo: String,
        categoryTag: String,
        notes: String
    ) -> Unit
) {
    var type by remember { mutableStateOf(initialType) }
    var amountStr by remember { mutableStateOf("") }
    var paymentMode by remember { mutableStateOf(if (type == "GOT") "UPI" else "CASH") }
    var referenceNo by remember { mutableStateOf("") }
    var categoryTag by remember { mutableStateOf("Tuition Fee") }
    var notes by remember { mutableStateOf("") }

    val paymentModes = listOf("CASH", "UPI", "BANK_TRANSFER", "CHEQUE", "CARD", "OTHER")
    val feeCategories = listOf(
        "Tuition Fee", "Term Fee", "Transport Fee", "Books & Uniform",
        "Examination Fee", "Admission Fee", "Hostel Fee", "Late Fine", "General Bill"
    )

    var expandedMode by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }

    val isUdhar = type == "GAVE"

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = if (isUdhar) "Record Fee Due (Udhar)" else "Record Fee Payment (Jama)",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isUdhar) ColorUdhar else ColorJama
                        )
                        Text(
                            text = "${party.name} (${party.gradeOrCategory})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Type Toggle (You Gave vs You Got)
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { type = "GAVE" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isUdhar) ColorUdhar else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isUdhar) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("You Gave (Due)", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { type = "GOT" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isUdhar) ColorJama else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (!isUdhar) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("You Got (Paid)", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Amount
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount (₹) *") },
                    placeholder = { Text("e.g. 3500") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_txn_amount")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Payment Mode Selector
                ExposedDropdownMenuBox(
                    expanded = expandedMode,
                    onExpandedChange = { expandedMode = !expandedMode }
                ) {
                    OutlinedTextField(
                        value = paymentMode,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Payment Mode") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMode) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedMode,
                        onDismissRequest = { expandedMode = false }
                    ) {
                        paymentModes.forEach { mode ->
                            DropdownMenuItem(
                                text = { Text(mode) },
                                onClick = {
                                    paymentMode = mode
                                    expandedMode = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Fee Category Selector
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory }
                ) {
                    OutlinedTextField(
                        value = categoryTag,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fee Head / Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        feeCategories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    categoryTag = cat
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = referenceNo,
                    onValueChange = { referenceNo = it },
                    label = { Text("Reference / UPI UTR / Receipt No") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(AppStrings.get("cancel", language))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amt = amountStr.toDoubleOrNull() ?: 0.0
                            if (amt > 0) {
                                onSave(type, amt, paymentMode, referenceNo, categoryTag, notes)
                            }
                        },
                        enabled = (amountStr.toDoubleOrNull() ?: 0.0) > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isUdhar) ColorUdhar else ColorJama
                        ),
                        modifier = Modifier.testTag("save_txn_button")
                    ) {
                        Text(AppStrings.get("save", language))
                    }
                }
            }
        }
    }
}

@Composable
fun WhatsAppReminderDialog(
    party: CustomerParty,
    schoolName: String,
    schoolUpiId: String,
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val message = remember(party, schoolName, schoolUpiId, language) {
        FormattingUtils.composeReminderMessage(
            schoolName = schoolName,
            studentName = party.name,
            studentId = party.studentIdOrRoll,
            dueAmount = party.currentBalance,
            upiId = schoolUpiId,
            language = language
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF25D366), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "WhatsApp Reminder",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${party.name} • ${party.phone}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Message Preview Card
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF1B5E20),
                        modifier = Modifier.padding(14.dp),
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = {
                            val upiUri = FormattingUtils.buildUpiUri(
                                schoolUpiId,
                                schoolName,
                                party.currentBalance,
                                "Fees for ${party.name}"
                            )
                            FormattingUtils.openUpiPayment(context, upiUri)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Test UPI", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            FormattingUtils.shareText(
                                context,
                                message,
                                "Send Reminder to ${party.name}"
                            )
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        modifier = Modifier
                            .weight(1.4f)
                            .testTag("send_whatsapp_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send via WhatsApp", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
