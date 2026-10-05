package com.example.ui.navigation

sealed class AppScreen(val route: String) {
    object Dashboard : AppScreen("dashboard")
    object PartyLedger : AppScreen("party_ledger")
    object Invoices : AppScreen("invoices")
    object Inventory : AppScreen("inventory")
    object Expenses : AppScreen("expenses")
    object Reports : AppScreen("reports")
    object UpiQr : AppScreen("upi_qr")
    object Settings : AppScreen("settings")
}
