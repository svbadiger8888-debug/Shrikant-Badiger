package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AuditLog
import com.example.data.model.BusinessProfile
import com.example.data.model.CustomerParty
import com.example.data.model.Expense
import com.example.data.model.InventoryItem
import com.example.data.model.Invoice
import com.example.data.model.LedgerTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface VidyaVijayDao {

    // --- Business Profile ---
    @Query("SELECT * FROM business_profile WHERE id = 1 LIMIT 1")
    fun getBusinessProfile(): Flow<BusinessProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: BusinessProfile)

    // --- Parties / Students ---
    @Query("SELECT * FROM parties ORDER BY currentBalance DESC, name ASC")
    fun getAllParties(): Flow<List<CustomerParty>>

    @Query("SELECT * FROM parties WHERE id = :partyId LIMIT 1")
    fun getPartyById(partyId: Long): Flow<CustomerParty?>

    @Query("SELECT * FROM parties WHERE id = :partyId LIMIT 1")
    suspend fun getPartyByIdSync(partyId: Long): CustomerParty?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParty(party: CustomerParty): Long

    @Update
    suspend fun updateParty(party: CustomerParty)

    @Delete
    suspend fun deleteParty(party: CustomerParty)

    @Query("UPDATE parties SET currentBalance = :newBalance, updatedAt = :time WHERE id = :partyId")
    suspend fun updatePartyBalance(partyId: Long, newBalance: Double, time: Long = System.currentTimeMillis())

    // --- Ledger Transactions ---
    @Query("SELECT * FROM ledger_transactions WHERE isDeleted = 0 ORDER BY dateMillis DESC")
    fun getAllTransactions(): Flow<List<LedgerTransaction>>

    @Query("SELECT * FROM ledger_transactions WHERE partyId = :partyId AND isDeleted = 0 ORDER BY dateMillis DESC")
    fun getTransactionsForParty(partyId: Long): Flow<List<LedgerTransaction>>

    @Query("SELECT * FROM ledger_transactions WHERE partyId = :partyId AND isDeleted = 0 ORDER BY dateMillis ASC")
    suspend fun getTransactionsForPartySync(partyId: Long): List<LedgerTransaction>

    @Query("SELECT * FROM ledger_transactions WHERE dateMillis >= :startOfDay AND isDeleted = 0 ORDER BY dateMillis DESC")
    fun getTodayTransactions(startOfDay: Long): Flow<List<LedgerTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: LedgerTransaction): Long

    @Query("UPDATE ledger_transactions SET isDeleted = 1 WHERE id = :transactionId")
    suspend fun softDeleteTransaction(transactionId: Long)

    @Delete
    suspend fun deleteTransaction(transaction: LedgerTransaction)

    // --- Invoices ---
    @Query("SELECT * FROM invoices ORDER BY dateMillis DESC")
    fun getAllInvoices(): Flow<List<Invoice>>

    @Query("SELECT * FROM invoices WHERE partyId = :partyId ORDER BY dateMillis DESC")
    fun getInvoicesForParty(partyId: Long): Flow<List<Invoice>>

    @Query("SELECT * FROM invoices WHERE id = :invoiceId LIMIT 1")
    suspend fun getInvoiceById(invoiceId: Long): Invoice?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: Invoice): Long

    @Delete
    suspend fun deleteInvoice(invoice: Invoice)

    // --- Inventory ---
    @Query("SELECT * FROM inventory_items ORDER BY name ASC")
    fun getAllInventoryItems(): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory_items WHERE currentStock <= minStockAlert ORDER BY currentStock ASC")
    fun getLowStockItems(): Flow<List<InventoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryItem(item: InventoryItem): Long

    @Update
    suspend fun updateInventoryItem(item: InventoryItem)

    @Delete
    suspend fun deleteInventoryItem(item: InventoryItem)

    @Query("UPDATE inventory_items SET currentStock = currentStock + :delta WHERE id = :itemId")
    suspend fun adjustStock(itemId: Long, delta: Int)

    // --- Expenses ---
    @Query("SELECT * FROM expenses ORDER BY dateMillis DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE dateMillis >= :fromMillis AND dateMillis <= :toMillis ORDER BY dateMillis DESC")
    fun getExpensesRange(fromMillis: Long, toMillis: Long): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Delete
    suspend fun deleteExpense(expense: Expense)

    // --- Audit Logs ---
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentAuditLogs(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLog): Long
}
