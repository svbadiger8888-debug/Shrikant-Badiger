package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Customer
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {

    @Query("SELECT * FROM parties ORDER BY currentBalance DESC, name ASC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM parties WHERE id = :customerId LIMIT 1")
    fun getCustomerById(customerId: Long): Flow<Customer?>

    @Query("SELECT * FROM parties WHERE id = :customerId LIMIT 1")
    suspend fun getCustomerByIdSync(customerId: Long): Customer?

    @Query("SELECT * FROM parties WHERE phone = :phone LIMIT 1")
    suspend fun getCustomerByPhone(phone: String): Customer?

    @Query("""
        SELECT * FROM parties 
        WHERE name LIKE '%' || :query || '%' 
           OR phone LIKE '%' || :query || '%' 
           OR studentIdOrRoll LIKE '%' || :query || '%' 
           OR guardianName LIKE '%' || :query || '%'
        ORDER BY currentBalance DESC, name ASC
    """)
    fun searchCustomers(query: String): Flow<List<Customer>>

    @Query("SELECT * FROM parties WHERE gradeOrCategory = :tag ORDER BY name ASC")
    fun getCustomersByTag(tag: String): Flow<List<Customer>>

    @Query("SELECT * FROM parties WHERE currentBalance > 0 ORDER BY currentBalance DESC")
    fun getCustomersWithDues(): Flow<List<Customer>>

    @Query("SELECT * FROM parties WHERE currentBalance < 0 ORDER BY currentBalance ASC")
    fun getCustomersWithAdvance(): Flow<List<Customer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomers(customers: List<Customer>): List<Long>

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Query("UPDATE parties SET currentBalance = :newBalance, updatedAt = :time WHERE id = :customerId")
    suspend fun updateCustomerBalance(
        customerId: Long,
        newBalance: Double,
        time: Long = System.currentTimeMillis()
    )

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    @Query("DELETE FROM parties WHERE id = :customerId")
    suspend fun deleteCustomerById(customerId: Long)

    @Query("SELECT SUM(currentBalance) FROM parties WHERE currentBalance > 0")
    fun getTotalDuesSum(): Flow<Double?>

    @Query("SELECT SUM(ABS(currentBalance)) FROM parties WHERE currentBalance < 0")
    fun getTotalAdvanceSum(): Flow<Double?>
}
