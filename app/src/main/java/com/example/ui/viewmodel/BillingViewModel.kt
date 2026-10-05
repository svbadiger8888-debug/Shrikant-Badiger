package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AuditLog
import com.example.data.model.BusinessProfile
import com.example.data.model.CustomerParty
import com.example.data.model.Expense
import com.example.data.model.InventoryItem
import com.example.data.model.Invoice
import com.example.data.model.LedgerTransaction
import com.example.data.repository.BillingRepository
import com.example.util.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class DashboardSummary(
    val todayCollection: Double = 0.0,
    val todayDuesGiven: Double = 0.0,
    val totalPendingUdhar: Double = 0.0,
    val totalAdvanceJama: Double = 0.0,
    val totalCustomersCount: Int = 0,
    val totalMonthlyExpenses: Double = 0.0,
    val lowStockCount: Int = 0
)

class BillingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BillingRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = BillingRepository(database.dao())
    }

    val businessProfile: StateFlow<BusinessProfile?> = repository.businessProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allParties: StateFlow<List<CustomerParty>> = repository.allParties
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<LedgerTransaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allInvoices: StateFlow<List<Invoice>> = repository.allInvoices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allInventoryItems: StateFlow<List<InventoryItem>> = repository.allInventoryItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockItems: StateFlow<List<InventoryItem>> = repository.lowStockItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExpenses: StateFlow<List<Expense>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLog>> = repository.auditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI state filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("ALL") // ALL, DUES_ONLY, ADVANCE_ONLY, CLEARED, CLASS10, HOSTEL, VENDOR
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _selectedPartyId = MutableStateFlow<Long?>(null)
    val selectedPartyId: StateFlow<Long?> = _selectedPartyId.asStateFlow()

    // Filtered Parties Flow
    val filteredParties: StateFlow<List<CustomerParty>> = combine(
        allParties,
        searchQuery,
        selectedFilter
    ) { parties, query, filter ->
        parties.filter { party ->
            val matchesQuery = query.isBlank() ||
                party.name.contains(query, ignoreCase = true) ||
                party.studentIdOrRoll.contains(query, ignoreCase = true) ||
                party.phone.contains(query, ignoreCase = true) ||
                party.guardianName.contains(query, ignoreCase = true) ||
                party.gradeOrCategory.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                "DUES_ONLY" -> party.currentBalance > 0
                "ADVANCE_ONLY" -> party.currentBalance < 0
                "CLEARED" -> party.currentBalance == 0.0
                "CLASS10" -> party.gradeOrCategory.contains("10", ignoreCase = true)
                "CLASS9" -> party.gradeOrCategory.contains("9", ignoreCase = true)
                "HOSTEL" -> party.gradeOrCategory.contains("Hostel", ignoreCase = true)
                "VENDOR" -> party.gradeOrCategory.contains("Vendor", ignoreCase = true)
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dashboard aggregated summary
    val dashboardSummary: StateFlow<DashboardSummary> = combine(
        allParties,
        allTransactions,
        allExpenses,
        lowStockItems
    ) { parties, txns, expenses, lowStock ->
        val startOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val todayTxns = txns.filter { it.dateMillis >= startOfToday }
        val todayGot = todayTxns.filter { it.type == "GOT" }.sumOf { it.amount }
        val todayGave = todayTxns.filter { it.type == "GAVE" }.sumOf { it.amount }

        var totalDues = 0.0
        var totalAdvance = 0.0
        for (p in parties) {
            if (p.currentBalance > 0) {
                totalDues += p.currentBalance
            } else if (p.currentBalance < 0) {
                totalAdvance += -p.currentBalance
            }
        }

        val totalExpenses = expenses.sumOf { it.amount }

        DashboardSummary(
            todayCollection = todayGot,
            todayDuesGiven = todayGave,
            totalPendingUdhar = totalDues,
            totalAdvanceJama = totalAdvance,
            totalCustomersCount = parties.size,
            totalMonthlyExpenses = totalExpenses,
            lowStockCount = lowStock.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardSummary())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }

    fun setSelectedPartyId(partyId: Long?) {
        _selectedPartyId.value = partyId
    }

    fun getTransactionsForParty(partyId: Long): StateFlow<List<LedgerTransaction>> {
        return repository.getTransactionsForParty(partyId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun getInvoicesForParty(partyId: Long): StateFlow<List<Invoice>> {
        return repository.getInvoicesForParty(partyId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun addParty(
        name: String,
        studentId: String,
        guardianName: String,
        phone: String,
        gradeOrCategory: String,
        openingBalance: Double,
        balanceType: String,
        address: String,
        notes: String,
        onComplete: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            val role = businessProfile.value?.currentRole ?: "Principal"
            val party = CustomerParty(
                name = name,
                studentIdOrRoll = studentId,
                guardianName = guardianName,
                phone = phone,
                gradeOrCategory = gradeOrCategory,
                openingBalance = openingBalance,
                balanceType = balanceType,
                address = address,
                notes = notes
            )
            val id = repository.addParty(party, role)
            onComplete(id)
        }
    }

    fun updateParty(party: CustomerParty, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val role = businessProfile.value?.currentRole ?: "Principal"
            repository.updateParty(party, role)
            onComplete()
        }
    }

    fun deleteParty(party: CustomerParty, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val role = businessProfile.value?.currentRole ?: "Principal"
            repository.deleteParty(party, role)
            if (_selectedPartyId.value == party.id) {
                _selectedPartyId.value = null
            }
            onComplete()
        }
    }

    fun recordTransaction(
        partyId: Long,
        type: String, // "GAVE" or "GOT"
        amount: Double,
        dateMillis: Long,
        paymentMode: String,
        referenceNo: String,
        categoryTag: String,
        notes: String,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val role = businessProfile.value?.currentRole ?: "Principal"
            repository.recordTransaction(
                partyId = partyId,
                type = type,
                amount = amount,
                dateMillis = dateMillis,
                paymentMode = paymentMode,
                referenceNo = referenceNo,
                categoryTag = categoryTag,
                notes = notes,
                role = role
            )
            onComplete()
        }
    }

    fun deleteTransaction(transactionId: Long, partyId: Long) {
        viewModelScope.launch {
            val role = businessProfile.value?.currentRole ?: "Principal"
            repository.deleteTransaction(transactionId, partyId, role)
        }
    }

    fun createInvoice(
        partyId: Long,
        partyName: String,
        partyPhone: String,
        partyCategory: String,
        itemsJson: String,
        subtotal: Double,
        discount: Double,
        taxPercent: Double,
        taxAmount: Double,
        totalAmount: Double,
        paidAmount: Double,
        isGst: Boolean,
        notes: String,
        onComplete: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            val count = allInvoices.value.size + 1
            val invoiceNo = "VVS/26-27/%04d".format(count)
            val due = (totalAmount - paidAmount).coerceAtLeast(0.0)
            val status = when {
                due <= 0.0 -> "PAID"
                paidAmount > 0.0 -> "PARTIAL"
                else -> "PENDING"
            }

            val invoice = Invoice(
                invoiceNumber = invoiceNo,
                partyId = partyId,
                partyName = partyName,
                partyPhone = partyPhone,
                partyCategory = partyCategory,
                dateMillis = System.currentTimeMillis(),
                isGst = isGst,
                subtotal = subtotal,
                discount = discount,
                taxRatePercent = taxPercent,
                taxAmount = taxAmount,
                totalAmount = totalAmount,
                paidAmount = paidAmount,
                dueAmount = due,
                status = status,
                notes = notes,
                itemsJson = itemsJson
            )

            val role = businessProfile.value?.currentRole ?: "Principal"
            val id = repository.createInvoice(invoice, recordLedgerEntry = true, role = role)
            onComplete(id)
        }
    }

    fun saveInventoryItem(item: InventoryItem, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val role = businessProfile.value?.currentRole ?: "Principal"
            repository.saveInventoryItem(item, role)
            onComplete()
        }
    }

    fun adjustInventoryStock(itemId: Long, delta: Int, reason: String = "Adjustment") {
        viewModelScope.launch {
            val role = businessProfile.value?.currentRole ?: "Principal"
            repository.adjustInventoryStock(itemId, delta, reason, role)
        }
    }

    fun deleteInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            val role = businessProfile.value?.currentRole ?: "Principal"
            repository.deleteInventoryItem(item, role)
        }
    }

    fun addExpense(
        title: String,
        category: String,
        amount: Double,
        dateMillis: Long,
        paymentMode: String,
        reference: String,
        paidTo: String,
        notes: String,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val role = businessProfile.value?.currentRole ?: "Principal"
            val expense = Expense(
                title = title,
                category = category,
                amount = amount,
                dateMillis = dateMillis,
                paymentMode = paymentMode,
                reference = reference,
                paidTo = paidTo,
                notes = notes
            )
            repository.addExpense(expense, role)
            onComplete()
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            val role = businessProfile.value?.currentRole ?: "Principal"
            repository.deleteExpense(expense, role)
        }
    }

    fun updateBusinessProfile(profile: BusinessProfile, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.saveBusinessProfile(profile)
            onComplete()
        }
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetDatabase()
        }
    }
}
