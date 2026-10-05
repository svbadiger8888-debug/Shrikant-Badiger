package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CustomerParty
import com.example.data.model.Invoice
import com.example.data.model.InvoiceLineItem
import com.example.ui.theme.ColorJama
import com.example.ui.theme.ColorPending
import com.example.ui.theme.ColorUdhar
import com.example.ui.viewmodel.BillingViewModel
import com.example.util.FormattingUtils

@Composable
fun InvoiceScreen(
    viewModel: BillingViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val invoices by viewModel.allInvoices.collectAsState()
    val parties by viewModel.allParties.collectAsState()
    val profile by viewModel.businessProfile.collectAsState()

    var showCreateInvoiceDialog by remember { mutableStateOf(false) }
    var invoiceToPreview by remember { mutableStateOf<Invoice?>(null) }

    val schoolName = profile?.name ?: "Vidya Vijay School"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("invoices_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Fee Invoices & Bills",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "GST & Non-GST School Fee Receipts (${invoices.size})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showCreateInvoiceDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_create_invoice")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Bill")
                }
            }
        }

        if (invoices.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No invoices generated yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Generate professional school fee bills with itemized tuition, transport, books, and uniforms.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { showCreateInvoiceDialog = true }) {
                            Text("Create First Bill")
                        }
                    }
                }
            }
        } else {
            items(invoices, key = { it.id }) { invoice ->
                InvoiceCardItem(
                    invoice = invoice,
                    onPreview = { invoiceToPreview = invoice },
                    onShare = {
                        val text = formatInvoiceShareText(invoice, schoolName)
                        FormattingUtils.shareText(context, text, "Share Invoice #${invoice.invoiceNumber}")
                    }
                )
            }
        }
    }

    // Create Invoice Dialog
    if (showCreateInvoiceDialog) {
        CreateInvoiceDialog(
            parties = parties,
            onDismiss = { showCreateInvoiceDialog = false },
            onSave = { party, items, subtotal, discount, taxPct, taxAmt, total, paid, isGst, notes ->
                // serialize line items
                val itemsJson = serializeLineItems(items)
                viewModel.createInvoice(
                    partyId = party.id,
                    partyName = party.name,
                    partyPhone = party.phone,
                    partyCategory = party.gradeOrCategory,
                    itemsJson = itemsJson,
                    subtotal = subtotal,
                    discount = discount,
                    taxPercent = taxPct,
                    taxAmount = taxAmt,
                    totalAmount = total,
                    paidAmount = paid,
                    isGst = isGst,
                    notes = notes
                )
                showCreateInvoiceDialog = false
            }
        )
    }

    // Invoice Print / Share Preview Dialog
    invoiceToPreview?.let { inv ->
        InvoicePreviewDialog(
            invoice = inv,
            schoolName = schoolName,
            affiliationNo = profile?.codeOrAffiliation ?: "CBSE Affil: 830492",
            schoolAddress = profile?.address ?: "",
            schoolPhone = profile?.phone ?: "",
            onDismiss = { invoiceToPreview = null }
        )
    }
}

@Composable
private fun InvoiceCardItem(
    invoice: Invoice,
    onPreview: () -> Unit,
    onShare: () -> Unit
) {
    val statusColor = when (invoice.status) {
        "PAID" -> ColorJama
        "PARTIAL" -> ColorPending
        else -> ColorUdhar
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPreview() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Receipt, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = invoice.invoiceNumber,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = FormattingUtils.formatDate(invoice.dateMillis),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = invoice.status,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = invoice.partyName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = invoice.partyCategory,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = FormattingUtils.formatInr(invoice.totalAmount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (invoice.dueAmount > 0) {
                        Text(
                            text = "Due: ${FormattingUtils.formatInr(invoice.dueAmount)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = ColorUdhar,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onPreview,
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View Bill", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onShare,
                    modifier = Modifier.height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", fontSize = 12.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInvoiceDialog(
    parties: List<CustomerParty>,
    onDismiss: () -> Unit,
    onSave: (
        party: CustomerParty,
        items: List<InvoiceLineItem>,
        subtotal: Double,
        discount: Double,
        taxPercent: Double,
        taxAmount: Double,
        total: Double,
        paid: Double,
        isGst: Boolean,
        notes: String
    ) -> Unit
) {
    var selectedParty by remember { mutableStateOf(parties.firstOrNull()) }
    var expandedPartyMenu by remember { mutableStateOf(false) }
    var isGst by remember { mutableStateOf(false) }
    var discountStr by remember { mutableStateOf("0") }
    var taxPercentStr by remember { mutableStateOf("0") }
    var paidStr by remember { mutableStateOf("0") }
    var notes by remember { mutableStateOf("") }

    val lineItems = remember {
        mutableStateListOf(
            InvoiceLineItem("Tuition Fee - Quarter 1", "999293", 1, 4500.0, 0.0, 0.0, 4500.0),
            InvoiceLineItem("Computer & Science Lab", "999293", 1, 800.0, 0.0, 0.0, 800.0)
        )
    }

    var newItemTitle by remember { mutableStateOf("") }
    var newItemRate by remember { mutableStateOf("") }

    val subtotal = lineItems.sumOf { it.total }
    val discount = discountStr.toDoubleOrNull() ?: 0.0
    val taxableAmount = (subtotal - discount).coerceAtLeast(0.0)
    val taxPct = if (isGst) (taxPercentStr.toDoubleOrNull() ?: 0.0) else 0.0
    val taxAmt = taxableAmount * (taxPct / 100.0)
    val grandTotal = taxableAmount + taxAmt
    val paidAmount = paidStr.toDoubleOrNull() ?: 0.0

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Create School Fee Bill",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }

                // Student Selector
                item {
                    Text("Select Student / Party *", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = expandedPartyMenu,
                        onExpandedChange = { expandedPartyMenu = !expandedPartyMenu }
                    ) {
                        OutlinedTextField(
                            value = selectedParty?.name ?: "Select student",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPartyMenu) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedPartyMenu,
                            onDismissRequest = { expandedPartyMenu = false }
                        ) {
                            parties.forEach { p ->
                                DropdownMenuItem(
                                    text = { Text("${p.name} (${p.gradeOrCategory})") },
                                    onClick = {
                                        selectedParty = p
                                        expandedPartyMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                // GST Toggle
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("GST Invoice", fontWeight = FontWeight.SemiBold)
                            Text(
                                text = if (isGst) "Includes CGST/SGST" else "Exempted / Educational bill",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(checked = isGst, onCheckedChange = { isGst = it })
                    }
                }

                // Line items
                item {
                    Text("Bill Items / Fee Heads", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                items(lineItems.indices.toList()) { index ->
                    val item = lineItems[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = item.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(text = "${item.quantity} x ₹${item.unitRate}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(text = FormattingUtils.formatInr(item.total), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        IconButton(
                            onClick = { lineItems.removeAt(index) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // Add Item Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newItemTitle,
                            onValueChange = { newItemTitle = it },
                            placeholder = { Text("Fee Item / Book / Uniform", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1.5f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedTextField(
                            value = newItemRate,
                            onValueChange = { newItemRate = it },
                            placeholder = { Text("Rate ₹", fontSize = 11.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                val rate = newItemRate.toDoubleOrNull() ?: 0.0
                                if (newItemTitle.isNotBlank() && rate > 0) {
                                    lineItems.add(
                                        InvoiceLineItem(
                                            title = newItemTitle,
                                            unitRate = rate,
                                            quantity = 1,
                                            total = rate
                                        )
                                    )
                                    newItemTitle = ""
                                    newItemRate = ""
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                        }
                    }
                }

                // Calculations
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal:")
                        Text(FormattingUtils.formatInr(subtotal), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = discountStr,
                            onValueChange = { discountStr = it },
                            label = { Text("Discount / Concession (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        if (isGst) {
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = taxPercentStr,
                                onValueChange = { taxPercentStr = it },
                                label = { Text("GST %") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Grand Total:", fontWeight = FontWeight.Bold)
                        Text(FormattingUtils.formatInr(grandTotal), fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = paidStr,
                        onValueChange = { paidStr = it },
                        label = { Text("Payment Received Now (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Bill Notes (e.g. Term 1 Fee)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                selectedParty?.let { p ->
                                    if (grandTotal > 0) {
                                        onSave(
                                            p,
                                            lineItems,
                                            subtotal,
                                            discount,
                                            taxPct,
                                            taxAmt,
                                            grandTotal,
                                            paidAmount,
                                            isGst,
                                            notes
                                        )
                                    }
                                }
                            },
                            enabled = selectedParty != null && grandTotal > 0,
                            modifier = Modifier.testTag("submit_invoice_btn")
                        ) {
                            Text("Generate Bill")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InvoicePreviewDialog(
    invoice: Invoice,
    schoolName: String,
    affiliationNo: String,
    schoolAddress: String,
    schoolPhone: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                // School Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = schoolName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E3A8A)
                        )
                        Text(
                            text = affiliationNo,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF475569)
                        )
                        Text(
                            text = schoolAddress,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B),
                            fontSize = 10.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
                    }
                }

                HorizontalDivider(color = Color(0xFF1E3A8A), thickness = 2.dp, modifier = Modifier.padding(vertical = 10.dp))

                // Invoice metadata
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Billed To:", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text(invoice.partyName, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("${invoice.partyCategory} • ${invoice.partyPhone}", fontSize = 11.sp, color = Color.DarkGray)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Bill No: ${invoice.invoiceNumber}", fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                        Text("Date: ${FormattingUtils.formatDate(invoice.dateMillis)}", fontSize = 11.sp, color = Color.DarkGray)
                        Text(if (invoice.isGst) "TAX INVOICE" else "FEE RECEIPT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Items Table Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Description", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                            Text("Amount", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                        }
                        HorizontalDivider(color = Color(0xFFE2E8F0))

                        val parsedItems = parseLineItems(invoice.itemsJson)
                        parsedItems.forEach { itm ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(itm.title, fontSize = 12.sp, color = Color.DarkGray)
                                Text(FormattingUtils.formatInr(itm.total), fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        HorizontalDivider(color = Color(0xFFE2E8F0))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Grand Total:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                            Text(FormattingUtils.formatInr(invoice.totalAmount), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = Color(0xFF1E3A8A))
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Paid Amount:", fontSize = 12.sp, color = Color(0xFF059669))
                            Text(FormattingUtils.formatInr(invoice.paidAmount), fontSize = 12.sp, color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                        }

                        if (invoice.dueAmount > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Balance Due:", fontSize = 12.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                                Text(FormattingUtils.formatInr(invoice.dueAmount), fontSize = 12.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Close")
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            val text = formatInvoiceShareText(invoice, schoolName)
                            FormattingUtils.shareText(context, text, "Share Receipt")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        modifier = Modifier.weight(1.5f)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share via WhatsApp")
                    }
                }
            }
        }
    }
}

private fun serializeLineItems(items: List<InvoiceLineItem>): String {
    val entries = items.joinToString(",") { item ->
        "{\"title\":\"${item.title}\",\"quantity\":${item.quantity},\"unitRate\":${item.unitRate},\"total\":${item.total}}"
    }
    return "[$entries]"
}

private fun parseLineItems(json: String): List<InvoiceLineItem> {
    if (json.isBlank()) return listOf(InvoiceLineItem("Tuition & Academic Fee", quantity = 1, unitRate = 0.0, total = 0.0))
    // Basic resilient parser
    val list = mutableListOf<InvoiceLineItem>()
    val regex = "\"title\":\"([^\"]+)\"[^}]*\"total\":([0-9.]+)".toRegex()
    val matches = regex.findAll(json)
    for (m in matches) {
        val title = m.groupValues[1]
        val total = m.groupValues[2].toDoubleOrNull() ?: 0.0
        list.add(InvoiceLineItem(title = title, total = total, unitRate = total))
    }
    return if (list.isEmpty()) listOf(InvoiceLineItem("School Fee Bill", total = 0.0)) else list
}

private fun formatInvoiceShareText(invoice: Invoice, schoolName: String): String {
    return """
        *${schoolName}*
        *OFFICIAL FEE RECEIPT / BILL*
        -------------------------------
        Bill No: ${invoice.invoiceNumber}
        Date: ${FormattingUtils.formatDate(invoice.dateMillis)}
        Student: ${invoice.partyName} (${invoice.partyCategory})
        Total Bill Amount: ${FormattingUtils.formatInr(invoice.totalAmount)}
        Paid Amount: ${FormattingUtils.formatInr(invoice.paidAmount)}
        Pending Balance: ${FormattingUtils.formatInr(invoice.dueAmount)}
        Status: ${invoice.status}
        -------------------------------
        Thank you for your timely payment!
    """.trimIndent()
}
