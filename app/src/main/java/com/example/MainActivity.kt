package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExpensesScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.InvoiceScreen
import com.example.ui.screens.PartyLedgerScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.UpiCollectionScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.BillingViewModel
import com.example.util.AppLanguage
import com.example.util.AppStrings

class MainActivity : ComponentActivity() {

    private val viewModel: BillingViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

enum class NavigationSection {
    DASHBOARD,
    INVOICES,
    STOCK,
    EXPENSES,
    UPI_AND_REPORTS,
    SETTINGS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(viewModel: BillingViewModel) {
    val profile by viewModel.businessProfile.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val selectedPartyId by viewModel.selectedPartyId.collectAsState()
    val lowStockItems by viewModel.lowStockItems.collectAsState()

    var currentSection by remember { mutableStateOf(NavigationSection.DASHBOARD) }
    var upiOrReportsTab by remember { mutableStateOf(0) } // 0 = UPI QR, 1 = Reports
    var showLanguageDialog by remember { mutableStateOf(false) }

    val schoolName = profile?.name ?: "Vidya Vijay School"

    // If an individual student Khata ledger is opened
    selectedPartyId?.let { partyId ->
        PartyLedgerScreen(
            partyId = partyId,
            viewModel = viewModel,
            onBack = { viewModel.setSelectedPartyId(null) }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E3A8A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = schoolName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = "Academic Year: ${profile?.activeAcademicYear ?: "2026-2027"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Quick Language Switcher
                    IconButton(
                        onClick = { showLanguageDialog = true },
                        modifier = Modifier.testTag("btn_language_switch")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Change Language",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Settings Icon
                    IconButton(
                        onClick = { currentSection = NavigationSection.SETTINGS },
                        modifier = Modifier.testTag("btn_settings_nav")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = if (currentSection == NavigationSection.SETTINGS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                NavigationBarItem(
                    selected = currentSection == NavigationSection.DASHBOARD,
                    onClick = { currentSection = NavigationSection.DASHBOARD },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Khata", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                )

                NavigationBarItem(
                    selected = currentSection == NavigationSection.INVOICES,
                    onClick = { currentSection = NavigationSection.INVOICES },
                    icon = { Icon(Icons.Default.Receipt, contentDescription = "Bills") },
                    label = { Text("Bills", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                )

                NavigationBarItem(
                    selected = currentSection == NavigationSection.STOCK,
                    onClick = { currentSection = NavigationSection.STOCK },
                    icon = {
                        if (lowStockItems.isNotEmpty()) {
                            BadgedBox(
                                badge = {
                                    Badge { Text("${lowStockItems.size}") }
                                }
                            ) {
                                Icon(Icons.Default.Inventory2, contentDescription = "Stock")
                            }
                        } else {
                            Icon(Icons.Default.Inventory2, contentDescription = "Stock")
                        }
                    },
                    label = { Text("Stock", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                )

                NavigationBarItem(
                    selected = currentSection == NavigationSection.EXPENSES,
                    onClick = { currentSection = NavigationSection.EXPENSES },
                    icon = { Icon(Icons.Default.MoneyOff, contentDescription = "Expenses") },
                    label = { Text("Expenses", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                )

                NavigationBarItem(
                    selected = currentSection == NavigationSection.UPI_AND_REPORTS,
                    onClick = { currentSection = NavigationSection.UPI_AND_REPORTS },
                    icon = { Icon(Icons.Default.QrCode2, contentDescription = "UPI & Reports") },
                    label = { Text("UPI & Stats", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentSection,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { section ->
                when (section) {
                    NavigationSection.DASHBOARD -> {
                        DashboardScreen(
                            viewModel = viewModel,
                            onNavigateToPartyLedger = { partyId ->
                                viewModel.setSelectedPartyId(partyId)
                            },
                            onNavigateToInvoices = {
                                currentSection = NavigationSection.INVOICES
                            },
                            onNavigateToUpiQr = {
                                currentSection = NavigationSection.UPI_AND_REPORTS
                                upiOrReportsTab = 0
                            }
                        )
                    }

                    NavigationSection.INVOICES -> {
                        BackHandler { currentSection = NavigationSection.DASHBOARD }
                        InvoiceScreen(viewModel = viewModel)
                    }

                    NavigationSection.STOCK -> {
                        BackHandler { currentSection = NavigationSection.DASHBOARD }
                        InventoryScreen(viewModel = viewModel)
                    }

                    NavigationSection.EXPENSES -> {
                        BackHandler { currentSection = NavigationSection.DASHBOARD }
                        ExpensesScreen(viewModel = viewModel)
                    }

                    NavigationSection.UPI_AND_REPORTS -> {
                        BackHandler { currentSection = NavigationSection.DASHBOARD }
                        Column(modifier = Modifier.fillMaxSize()) {
                            TabRow(selectedTabIndex = upiOrReportsTab) {
                                Tab(
                                    selected = upiOrReportsTab == 0,
                                    onClick = { upiOrReportsTab = 0 },
                                    text = { Text("UPI QR Code", fontWeight = FontWeight.Bold) },
                                    icon = { Icon(Icons.Default.QrCode2, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                )
                                Tab(
                                    selected = upiOrReportsTab == 1,
                                    onClick = { upiOrReportsTab = 1 },
                                    text = { Text("Reports & Aging", fontWeight = FontWeight.Bold) },
                                    icon = { Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                )
                            }
                            if (upiOrReportsTab == 0) {
                                UpiCollectionScreen(viewModel = viewModel)
                            } else {
                                ReportsScreen(viewModel = viewModel)
                            }
                        }
                    }

                    NavigationSection.SETTINGS -> {
                        BackHandler { currentSection = NavigationSection.DASHBOARD }
                        SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    // Language Quick Switcher Dialog
    if (showLanguageDialog) {
        Dialog(onDismissRequest = { showLanguageDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppStrings.get("language", currentLanguage),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showLanguageDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    AppLanguage.values().forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentLanguage == lang,
                                onClick = {
                                    viewModel.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(lang.displayName, fontWeight = FontWeight.SemiBold)
                                Text(lang.nativeName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
