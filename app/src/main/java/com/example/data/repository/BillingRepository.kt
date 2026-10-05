package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.VidyaVijayDao
import com.example.data.model.AuditLog
import com.example.data.model.BusinessProfile
import com.example.data.model.CustomerParty
import com.example.data.model.Expense
import com.example.data.model.InventoryItem
import com.example.data.model.Invoice
import com.example.data.model.LedgerTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BillingRepository(private val dao: VidyaVijayDao) {

    val businessProfile: Flow<BusinessProfile?> = dao.getBusinessProfile()
    val allParties: Flow<List<CustomerParty>> = dao.getAllParties()
    val allTransactions: Flow<List<LedgerTransaction>> = dao.getAllTransactions()
    val allInvoices: Flow<List<Invoice>> = dao.getAllInvoices()
    val allInventoryItems: Flow<List<InventoryItem>> = dao.getAllInventoryItems()
    val lowStockItems: Flow<List<InventoryItem>> = dao.getLowStockItems()
    val allExpenses: Flow<List<Expense>> = dao.getAllExpenses()
    val auditLogs: Flow<List<AuditLog>> = dao.getRecentAuditLogs()

    fun getParty(partyId: Long): Flow<CustomerParty?> = dao.getPartyById(partyId)

    fun getTransactionsForParty(partyId: Long): Flow<List<LedgerTransaction>> =
        dao.getTransactionsForParty(partyId)

    fun getInvoicesForParty(partyId: Long): Flow<List<Invoice>> =
        dao.getInvoicesForParty(partyId)

    suspend fun saveBusinessProfile(profile: BusinessProfile) = withContext(Dispatchers.IO) {
        dao.insertOrUpdateProfile(profile)
        dao.insertAuditLog(
            AuditLog(
                action = "Profile Updated",
                details = "Updated school details for ${profile.name}",
                userRole = profile.currentRole
            )
        )
    }

    suspend fun addParty(party: CustomerParty, role: String = "Principal"): Long = withContext(Dispatchers.IO) {
        val initialBalance = if (party.balanceType == "DUE") party.openingBalance else -party.openingBalance
        val partyWithBalance = party.copy(currentBalance = initialBalance)
        val id = dao.insertParty(partyWithBalance)
        dao.insertAuditLog(
            AuditLog(
                action = "Party Added",
                details = "Created record for ${party.name} (${party.gradeOrCategory}) with balance ₹${party.openingBalance}",
                userRole = role
            )
        )
        id
    }

    suspend fun updateParty(party: CustomerParty, role: String = "Principal") = withContext(Dispatchers.IO) {
        dao.updateParty(party)
        recalculatePartyBalance(party.id)
        dao.insertAuditLog(
            AuditLog(
                action = "Party Updated",
                details = "Modified information for ${party.name}",
                userRole = role
            )
        )
    }

    suspend fun deleteParty(party: CustomerParty, role: String = "Principal") = withContext(Dispatchers.IO) {
        dao.deleteParty(party)
        dao.insertAuditLog(
            AuditLog(
                action = "Party Deleted",
                details = "Deleted record of ${party.name}",
                userRole = role
            )
        )
    }

    suspend fun recordTransaction(
        partyId: Long,
        type: String, // "GAVE" (Udhar / Fee Due) or "GOT" (Jama / Fee Paid)
        amount: Double,
        dateMillis: Long,
        paymentMode: String,
        referenceNo: String,
        categoryTag: String,
        notes: String,
        role: String = "Principal"
    ): Long = withContext(Dispatchers.IO) {
        val party = dao.getPartyByIdSync(partyId) ?: return@withContext 0L
        val txn = LedgerTransaction(
            partyId = partyId,
            partyName = party.name,
            partyPhone = party.phone,
            type = type,
            amount = amount,
            dateMillis = dateMillis,
            paymentMode = paymentMode,
            referenceNo = referenceNo,
            categoryTag = categoryTag,
            notes = notes
        )
        val txnId = dao.insertTransaction(txn)
        recalculatePartyBalance(partyId)

        val actionDesc = if (type == "GAVE") "Fee Due / Udhar recorded: ₹$amount" else "Fee Payment / Jama recorded: ₹$amount ($paymentMode)"
        dao.insertAuditLog(
            AuditLog(
                action = "Transaction Entry",
                details = "$actionDesc for ${party.name} [Ref: $referenceNo]",
                userRole = role
            )
        )
        txnId
    }

    suspend fun deleteTransaction(transactionId: Long, partyId: Long, role: String = "Principal") = withContext(Dispatchers.IO) {
        dao.softDeleteTransaction(transactionId)
        recalculatePartyBalance(partyId)
        dao.insertAuditLog(
            AuditLog(
                action = "Transaction Deleted",
                details = "Voided transaction #$transactionId for Party #$partyId",
                userRole = role
            )
        )
    }

    suspend fun recalculatePartyBalance(partyId: Long) = withContext(Dispatchers.IO) {
        val party = dao.getPartyByIdSync(partyId) ?: return@withContext
        val txns = dao.getTransactionsForPartySync(partyId)
        var balance = if (party.balanceType == "DUE") party.openingBalance else -party.openingBalance
        for (t in txns) {
            if (t.type == "GAVE") {
                balance += t.amount // customer owes more
            } else {
                balance -= t.amount // customer paid
            }
        }
        dao.updatePartyBalance(partyId, balance)
    }

    suspend fun createInvoice(invoice: Invoice, recordLedgerEntry: Boolean = true, role: String = "Principal"): Long = withContext(Dispatchers.IO) {
        val id = dao.insertInvoice(invoice)
        if (recordLedgerEntry && invoice.partyId > 0) {
            // Record invoice due
            dao.insertTransaction(
                LedgerTransaction(
                    partyId = invoice.partyId,
                    partyName = invoice.partyName,
                    partyPhone = invoice.partyPhone,
                    type = "GAVE",
                    amount = invoice.totalAmount,
                    dateMillis = invoice.dateMillis,
                    paymentMode = "BILL",
                    referenceNo = invoice.invoiceNumber,
                    categoryTag = "Bill / Fee Invoice",
                    notes = "Generated Invoice #${invoice.invoiceNumber}",
                    billInvoiceId = id
                )
            )
            // If already partially or fully paid at billing time
            if (invoice.paidAmount > 0) {
                dao.insertTransaction(
                    LedgerTransaction(
                        partyId = invoice.partyId,
                        partyName = invoice.partyName,
                        partyPhone = invoice.partyPhone,
                        type = "GOT",
                        amount = invoice.paidAmount,
                        dateMillis = invoice.dateMillis,
                        paymentMode = "CASH",
                        referenceNo = "RCPT-${invoice.invoiceNumber}",
                        categoryTag = "Fee Receipt",
                        notes = "Payment received against Invoice #${invoice.invoiceNumber}",
                        billInvoiceId = id
                    )
                )
            }
            recalculatePartyBalance(invoice.partyId)
        }
        dao.insertAuditLog(
            AuditLog(
                action = "Invoice Generated",
                details = "Invoice #${invoice.invoiceNumber} for ${invoice.partyName} of ₹${invoice.totalAmount}",
                userRole = role
            )
        )
        id
    }

    suspend fun saveInventoryItem(item: InventoryItem, role: String = "Principal") = withContext(Dispatchers.IO) {
        if (item.id == 0L) {
            dao.insertInventoryItem(item)
        } else {
            dao.updateInventoryItem(item)
        }
        dao.insertAuditLog(
            AuditLog(
                action = "Inventory Saved",
                details = "Item ${item.name} stock ${item.currentStock} ${item.unit}",
                userRole = role
            )
        )
    }

    suspend fun adjustInventoryStock(itemId: Long, delta: Int, reason: String = "Adjustment", role: String = "Principal") = withContext(Dispatchers.IO) {
        dao.adjustStock(itemId, delta)
        dao.insertAuditLog(
            AuditLog(
                action = "Stock Adjusted",
                details = "Stock delta $delta for Item #$itemId ($reason)",
                userRole = role
            )
        )
    }

    suspend fun deleteInventoryItem(item: InventoryItem, role: String = "Principal") = withContext(Dispatchers.IO) {
        dao.deleteInventoryItem(item)
    }

    suspend fun addExpense(expense: Expense, role: String = "Principal"): Long = withContext(Dispatchers.IO) {
        val id = dao.insertExpense(expense)
        dao.insertAuditLog(
            AuditLog(
                action = "Expense Recorded",
                details = "Paid ₹${expense.amount} for ${expense.title} via ${expense.paymentMode}",
                userRole = role
            )
        )
        id
    }

    suspend fun deleteExpense(expense: Expense, role: String = "Principal") = withContext(Dispatchers.IO) {
        dao.deleteExpense(expense)
    }

    suspend fun resetDatabase() = withContext(Dispatchers.IO) {
        AppDatabase.populateInitialData(dao)
    }
}
