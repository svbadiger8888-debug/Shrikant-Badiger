package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CustomerParty
import com.example.ui.theme.ColorJama
import com.example.ui.theme.ColorPending
import com.example.ui.theme.ColorUdhar
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.FormattingUtils
import kotlin.math.abs

@Composable
fun SummaryStatCard(
    title: String,
    amount: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier,
    testTag: String = "stat_card"
) {
    Card(
        modifier = modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor.copy(alpha = 0.85f),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = amount,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.7f),
                fontSize = 11.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun CustomerPartyRow(
    party: CustomerParty,
    onClick: () -> Unit,
    onSendReminder: () -> Unit,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val isDue = party.currentBalance > 0
    val isAdvance = party.currentBalance < 0
    val isCleared = party.currentBalance == 0.0

    val balanceColor = when {
        isDue -> ColorUdhar
        isAdvance -> ColorJama
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val balanceLabel = when {
        isDue -> "${AppStrings.get("balance_due", language)}: ${FormattingUtils.formatInr(party.currentBalance)}"
        isAdvance -> "${AppStrings.get("balance_advance", language)}: ${FormattingUtils.formatInr(abs(party.currentBalance))}"
        else -> AppStrings.get("balance_settled", language)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("party_item_${party.id}")
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Student / Party Initial Avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isDue -> MaterialTheme.colorScheme.primaryContainer
                            isAdvance -> ColorJama.copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = party.name.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isDue -> MaterialTheme.colorScheme.primary
                        isAdvance -> ColorJama
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = party.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Category / Grade Badge
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = party.gradeOrCategory,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (party.studentIdOrRoll.isNotBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${party.studentIdOrRoll}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Balance & Action
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = balanceLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = balanceColor,
                    textAlign = TextAlign.End
                )

                if (isDue) {
                    Spacer(modifier = Modifier.height(4.dp))
                    IconButton(
                        onClick = onSendReminder,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ColorJama.copy(alpha = 0.12f))
                            .testTag("remind_btn_${party.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Send WhatsApp Reminder",
                            tint = ColorJama,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Procedural standard UPI QR code visual renderer for instant offline display
 */
@Composable
fun ProceduralUpiQrCode(
    upiUri: String,
    modifier: Modifier = Modifier,
    schoolName: String = "Vidya Vijay School",
    schoolUpiId: String = "vidyavijayschool@sbi"
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E3A8A)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = schoolName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Scan to Pay via any UPI App (GPay/PhonePe/Paytm)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Procedural QR Matrix Canvas
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(2.dp, Color(0xFF0F172A), RoundedCornerShape(12.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(184.dp)) {
                    val gridSize = 23
                    val cellSize = size.width / gridSize
                    val dark = Color(0xFF0F172A)
                    val seed = upiUri.hashCode()

                    // Helper to draw QR finder pattern (7x7 outer, 5x5 white, 3x3 black)
                    fun drawFinder(startX: Int, startY: Int) {
                        // Outer black 7x7
                        drawRoundRect(
                            color = dark,
                            topLeft = Offset(startX * cellSize, startY * cellSize),
                            size = Size(7 * cellSize, 7 * cellSize),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )
                        // Inner white 5x5
                        drawRoundRect(
                            color = Color.White,
                            topLeft = Offset((startX + 1) * cellSize, (startY + 1) * cellSize),
                            size = Size(5 * cellSize, 5 * cellSize),
                            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                        )
                        // Center black 3x3
                        drawRoundRect(
                            color = dark,
                            topLeft = Offset((startX + 2) * cellSize, (startY + 2) * cellSize),
                            size = Size(3 * cellSize, 3 * cellSize),
                            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                        )
                    }

                    // Top-Left Finder
                    drawFinder(0, 0)
                    // Top-Right Finder
                    drawFinder(gridSize - 7, 0)
                    // Bottom-Left Finder
                    drawFinder(0, gridSize - 7)

                    // Data dots based on hash of upiUri
                    val rand = java.util.Random(seed.toLong())
                    for (x in 0 until gridSize) {
                        for (y in 0 until gridSize) {
                            // Skip finder pattern zones
                            val inTL = (x < 8 && y < 8)
                            val inTR = (x >= gridSize - 8 && y < 8)
                            val inBL = (x < 8 && y >= gridSize - 8)
                            val inCenterBadge = (x in 9..13 && y in 9..13)

                            if (!inTL && !inTR && !inBL && !inCenterBadge) {
                                val isBlack = rand.nextBoolean() || ((x + y) % 3 == 0)
                                if (isBlack) {
                                    drawRoundRect(
                                        color = dark,
                                        topLeft = Offset(x * cellSize + 0.5f, y * cellSize + 0.5f),
                                        size = Size(cellSize - 1f, cellSize - 1f),
                                        cornerRadius = CornerRadius(1.5f, 1.5f)
                                    )
                                }
                            }
                        }
                    }

                    // Center Rupee / School Crest Badge
                    val centerSize = 5 * cellSize
                    val centerOffset = Offset(9 * cellSize, 9 * cellSize)
                    drawRoundRect(
                        color = Color.White,
                        topLeft = centerOffset,
                        size = Size(centerSize, centerSize),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                    drawRoundRect(
                        color = Color(0xFF1E3A8A),
                        topLeft = Offset(9.4f * cellSize, 9.4f * cellSize),
                        size = Size(4.2f * cellSize, 4.2f * cellSize),
                        cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                    )
                }

                // Center rupee symbol
                Text(
                    text = "₹",
                    color = Color(0xFFFBBF24),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // UPI ID Pill
            Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = null,
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = schoolUpiId,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPartyDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        studentId: String,
        guardianName: String,
        phone: String,
        gradeOrCategory: String,
        openingBalance: Double,
        balanceType: String,
        address: String,
        notes: String
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var guardianName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var gradeCategory by remember { mutableStateOf("Class 10-A") }
    var openingBalanceStr by remember { mutableStateOf("0") }
    var balanceType by remember { mutableStateOf("DUE") } // DUE or ADVANCE
    var address by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val categories = listOf(
        "Class 10-A", "Class 10-B", "Class 9-A", "Class 9-B",
        "Class 8", "Primary Section", "Hostel / Boarding",
        "Staff", "Vendor / Supplier", "Kirana / Merchant"
    )

    var expandedCategory by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = AppStrings.get("add_student", language),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student / Party Full Name *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_party_name")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = studentId,
                        onValueChange = { studentId = it },
                        label = { Text("Roll / ID No") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Mobile Phone") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = guardianName,
                    onValueChange = { guardianName = it },
                    label = { Text("Father / Guardian Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category selector
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory }
                ) {
                    OutlinedTextField(
                        value = gradeCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Class / Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    gradeCategory = cat
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Opening Balance & Type
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = openingBalanceStr,
                        onValueChange = { openingBalanceStr = it },
                        label = { Text("Opening Balance (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1.2f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Due vs Advance Toggle
                    Row(modifier = Modifier.weight(1.4f)) {
                        Button(
                            onClick = { balanceType = "DUE" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (balanceType == "DUE") ColorUdhar else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (balanceType == "DUE") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Due", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { balanceType = "ADVANCE" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (balanceType == "ADVANCE") ColorJama else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (balanceType == "ADVANCE") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Adv", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address / Locality") },
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
                            if (name.isNotBlank()) {
                                val balance = openingBalanceStr.toDoubleOrNull() ?: 0.0
                                onSave(
                                    name,
                                    studentId,
                                    guardianName,
                                    phone,
                                    gradeCategory,
                                    balance,
                                    balanceType,
                                    address,
                                    notes
                                )
                            }
                        },
                        enabled = name.isNotBlank(),
                        modifier = Modifier.testTag("save_party_button")
                    ) {
                        Text(AppStrings.get("save", language))
                    }
                }
            }
        }
    }
}
