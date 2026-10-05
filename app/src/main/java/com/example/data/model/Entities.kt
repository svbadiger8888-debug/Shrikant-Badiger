package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "business_profile")
data class BusinessProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    val name: String = "Vidya Vijay School",
    val tagline: String = "Excellence in Education & Character",
    val codeOrAffiliation: String = "CBSE Affiliation No: 830492",
    val gstin: String = "29AAAAA0000A1Z5",
    val phone: String = "+91 94812 34567",
    val email: String = "info@vidyavijayschool.edu.in",
    val address: String = "Near Vidya Nagar Circle, Hubballi-Dharwad, Karnataka 580021",
    val upiId: String = "vidyavijayschool@sbi",
    val activeAcademicYear: String = "2026-2027",
    val currencySymbol: String = "₹",
    val currentRole: String = "Principal / Admin" // Principal / Admin, Accountant / Manager, Staff / Clerk
)

@Entity(
    tableName = "parties",
    indices = [
        Index(value = ["phone"]),
        Index(value = ["gradeOrCategory"]),
        Index(value = ["currentBalance"])
    ]
)
data class CustomerParty(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val businessId: Long = 1L,
    val name: String,
    val studentIdOrRoll: String = "",
    val guardianName: String = "",
    val phone: String = "",
    val email: String = "",
    val gstin: String = "",
    val gradeOrCategory: String = "Class 10-A", // e.g. Class 10-A, Class 9, Primary, Hostel, Staff, Vendor, Retail
    val openingBalance: Double = 0.0,
    val balanceType: String = "DUE", // DUE (Udhar) or ADVANCE (Jama)
    val currentBalance: Double = 0.0, // Positive = owes money (Due/Udhar), Negative = advance paid (Jama)
    val address: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

typealias Customer = CustomerParty

@Entity(
    tableName = "ledger_transactions",
    indices = [
        Index(value = ["partyId"]),
        Index(value = ["dateMillis"]),
        Index(value = ["type"])
    ]
)
data class LedgerTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val businessId: Long = 1L,
    val partyId: Long,
    val partyName: String,
    val partyPhone: String = "",
    val type: String, // "GAVE" (Udhar / Fee Due) or "GOT" (Jama / Fee Paid)
    val amount: Double,
    val dateMillis: Long = System.currentTimeMillis(),
    val paymentMode: String = "CASH", // CASH, UPI, BANK_TRANSFER, CHEQUE, CARD, OTHER
    val referenceNo: String = "", // UPI UTR, Cheque No, Receipt No
    val categoryTag: String = "Tuition Fee", // Tuition Fee, Transport, Books, Uniform, Hostel, Exam, Vendor Bill, Other
    val notes: String = "",
    val billInvoiceId: Long? = null,
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val editedAt: Long? = null
)

typealias Transaction = LedgerTransaction

@Entity(tableName = "invoices")
data class Invoice(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceNumber: String,
    val partyId: Long,
    val partyName: String,
    val partyPhone: String = "",
    val partyCategory: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val isGst: Boolean = false,
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val taxRatePercent: Double = 0.0,
    val taxAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val paidAmount: Double = 0.0,
    val dueAmount: Double = 0.0,
    val status: String = "PAID", // PAID, PARTIAL, PENDING
    val notes: String = "",
    val itemsJson: String = "" // Serialized list of items
)

data class InvoiceLineItem(
    val title: String,
    val hsnSac: String = "999293",
    val quantity: Int = 1,
    val unitRate: Double = 0.0,
    val discount: Double = 0.0,
    val taxPercent: Double = 0.0,
    val total: Double = 0.0
)

@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val sku: String = "",
    val category: String = "Books", // Books, Uniforms, Stationery, Lab Kits, General
    val unit: String = "Pcs", // Pcs, Set, Box, Pair
    val costPrice: Double = 0.0,
    val sellingPrice: Double = 0.0,
    val currentStock: Int = 0,
    val minStockAlert: Int = 5
)

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String = "Salaries & Wages", // Salaries, Utilities, Transport, Maintenance, Events, Misc
    val amount: Double,
    val dateMillis: Long = System.currentTimeMillis(),
    val paymentMode: String = "CASH",
    val reference: String = "",
    val paidTo: String = "",
    val notes: String = ""
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val action: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis(),
    val userRole: String = "Principal"
)
