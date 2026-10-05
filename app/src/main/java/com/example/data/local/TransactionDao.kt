package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM ledger_transactions WHERE isDeleted = 0 ORDER BY dateMillis DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM ledger_transactions WHERE partyId = :customerId AND isDeleted = 0 ORDER BY dateMillis DESC")
    fun getTransactionsForCustomer(customerId: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM ledger_transactions WHERE partyId = :customerId AND isDeleted = 0 ORDER BY dateMillis ASC")
    suspend fun getTransactionsForCustomerSync(customerId: Long): List<Transaction>

    @Query("SELECT * FROM ledger_transactions WHERE dateMillis >= :startMillis AND dateMillis <= :endMillis AND isDeleted = 0 ORDER BY dateMillis DESC")
    fun getTransactionsByDateRange(startMillis: Long, endMillis: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM ledger_transactions WHERE dateMillis >= :startOfDayMillis AND isDeleted = 0 ORDER BY dateMillis DESC")
    fun getTodayTransactions(startOfDayMillis: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM ledger_transactions WHERE type = :type AND isDeleted = 0 ORDER BY dateMillis DESC")
    fun getTransactionsByType(type: String): Flow<List<Transaction>>

    @Query("SELECT * FROM ledger_transactions WHERE paymentMode = :paymentMode AND isDeleted = 0 ORDER BY dateMillis DESC")
    fun getTransactionsByPaymentMode(paymentMode: String): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<Transaction>): List<Long>

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Query("UPDATE ledger_transactions SET isDeleted = 1, editedAt = :editTime WHERE id = :transactionId")
    suspend fun softDeleteTransaction(
        transactionId: Long,
        editTime: Long = System.currentTimeMillis()
    )

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("DELETE FROM ledger_transactions WHERE id = :transactionId")
    suspend fun deleteTransactionById(transactionId: Long)

    @Query("SELECT SUM(amount) FROM ledger_transactions WHERE partyId = :customerId AND type = 'GAVE' AND isDeleted = 0")
    fun getTotalDuesGivenSum(customerId: Long): Flow<Double?>

    @Query("SELECT SUM(amount) FROM ledger_transactions WHERE partyId = :customerId AND type = 'GOT' AND isDeleted = 0")
    fun getTotalPaymentsGotSum(customerId: Long): Flow<Double?>

    @Query("SELECT SUM(amount) FROM ledger_transactions WHERE type = 'GOT' AND dateMillis >= :startOfDayMillis AND isDeleted = 0")
    fun getTodayCollectionSum(startOfDayMillis: Long): Flow<Double?>
}
