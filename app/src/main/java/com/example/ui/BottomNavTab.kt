package com.example.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavTab(
    val route: String,
    val titleKey: String,
    val icon: ImageVector
) {
    object Dashboard : BottomNavTab("dashboard", "nav_dashboard", Icons.Default.Dashboard)
    object Invoices : BottomNavTab("invoices", "nav_invoices", Icons.Default.Receipt)
    object Stock : BottomNavTab("stock", "nav_inventory", Icons.Default.Inventory2)
    object Expenses : BottomNavTab("expenses", "nav_expenses", Icons.Default.MoneyOff)
    object Upi : BottomNavTab("upi", "nav_upi", Icons.Default.QrCode2)
    object Reports : BottomNavTab("reports", "nav_reports", Icons.Default.Assessment)
    object Settings : BottomNavTab("settings", "nav_settings", Icons.Default.Settings)
}
