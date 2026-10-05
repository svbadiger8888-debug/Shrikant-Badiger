package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AuditLog
import com.example.data.model.BusinessProfile
import com.example.data.model.CustomerParty
import com.example.data.model.Expense
import com.example.data.model.InventoryItem
import com.example.data.model.Invoice
import com.example.data.model.LedgerTransaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BusinessProfile::class,
        CustomerParty::class,
        LedgerTransaction::class,
        Invoice::class,
        InventoryItem::class,
        Expense::class,
        AuditLog::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dao(): VidyaVijayDao
    abstract fun customerDao(): CustomerDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vidya_vijay_school_billing.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.dao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: VidyaVijayDao) {
            // 1. School Business Profile
            dao.insertOrUpdateProfile(
                BusinessProfile(
                    id = 1,
                    name = "Vidya Vijay School",
                    tagline = "Excellence in Education & Character",
                    codeOrAffiliation = "CBSE Affil. No: 830492 | School Code: 45120",
                    gstin = "29AAAAA0000A1Z5",
                    phone = "+91 94812 34567",
                    email = "accounts@vidyavijayschool.edu.in",
                    address = "Station Road, Vidya Nagar, Hubballi-Dharwad, Karnataka 580021",
                    upiId = "vidyavijayschool@sbi",
                    activeAcademicYear = "2026-2027",
                    currencySymbol = "₹",
                    currentRole = "Principal / Admin"
                )
            )

            // 2. Initial Students / Parents / Parties
            val p1 = dao.insertParty(
                CustomerParty(
                    name = "Aarav Rajesh Sharma",
                    studentIdOrRoll = "VVS-26-101",
                    guardianName = "Rajesh Sharma",
                    phone = "9845123456",
                    email = "rajesh.sharma@example.com",
                    gradeOrCategory = "Class 10-A",
                    openingBalance = 5000.0,
                    balanceType = "DUE",
                    currentBalance = 3500.0,
                    address = "Plot 14, Bhavani Nagar, Hubballi",
                    notes = "Term 1 Tuition fee pending"
                )
            )

            val p2 = dao.insertParty(
                CustomerParty(
                    name = "Ananya Suresh Patil",
                    studentIdOrRoll = "VVS-26-088",
                    guardianName = "Suresh Patil",
                    phone = "9448112233",
                    email = "spatil.adv@gmail.com",
                    gradeOrCategory = "Class 9-B",
                    openingBalance = 0.0,
                    balanceType = "DUE",
                    currentBalance = 12000.0,
                    address = "Flat 302, Green Meadows, Dharwad",
                    notes = "Annual Bus Transport + Lab Fee due"
                )
            )

            val p3 = dao.insertParty(
                CustomerParty(
                    name = "Rohan Vinod Kulkarni",
                    studentIdOrRoll = "VVS-26-142",
                    guardianName = "Vinod Kulkarni",
                    phone = "9731298765",
                    email = "vinod.k@kulkarni.in",
                    gradeOrCategory = "Hostel / Class 10",
                    openingBalance = 0.0,
                    balanceType = "ADVANCE",
                    currentBalance = -2500.0, // Advance paid
                    address = "Hostel Room B-12 (Home: Belagavi)",
                    notes = "Hostel mess advance submitted"
                )
            )

            val p4 = dao.insertParty(
                CustomerParty(
                    name = "Deepak Kirana & Stationery Store",
                    studentIdOrRoll = "VEND-882",
                    guardianName = "Deepak Sahu",
                    phone = "9880099887",
                    email = "deepak.stationers@gmail.com",
                    gradeOrCategory = "Vendor / Supplier",
                    openingBalance = 15000.0,
                    balanceType = "DUE",
                    currentBalance = 4500.0,
                    address = "Main Market Road, Hubballi",
                    notes = "School notebooks & examination answer sheets supply"
                )
            )

            val p5 = dao.insertParty(
                CustomerParty(
                    name = "Kavya Ramesh Deshmukh",
                    studentIdOrRoll = "VVS-26-045",
                    guardianName = "Ramesh Deshmukh",
                    phone = "9900122334",
                    email = "deshmukh.r@yahoo.com",
                    gradeOrCategory = "Primary / Class 4",
                    openingBalance = 0.0,
                    balanceType = "DUE",
                    currentBalance = 0.0, // Fully paid
                    address = "Keshwapur, Hubballi",
                    notes = "All fees cleared"
                )
            )

            // 3. Transactions for student ledger
            val now = System.currentTimeMillis()
            val day = 86400000L

            dao.insertTransaction(
                LedgerTransaction(
                    partyId = p1,
                    partyName = "Aarav Rajesh Sharma",
                    partyPhone = "9845123456",
                    type = "GAVE",
                    amount = 5000.0,
                    dateMillis = now - (3 * day),
                    paymentMode = "CASH",
                    referenceNo = "FEE-26-012",
                    categoryTag = "Tuition Fee",
                    notes = "Term 1 School Tuition Fee"
                )
            )
            dao.insertTransaction(
                LedgerTransaction(
                    partyId = p1,
                    partyName = "Aarav Rajesh Sharma",
                    partyPhone = "9845123456",
                    type = "GOT",
                    amount = 1500.0,
                    dateMillis = now - (1 * day),
                    paymentMode = "UPI",
                    referenceNo = "UPI-UTR-9082736152",
                    categoryTag = "Tuition Fee",
                    notes = "Paid via PhonePe by mother"
                )
            )

            dao.insertTransaction(
                LedgerTransaction(
                    partyId = p2,
                    partyName = "Ananya Suresh Patil",
                    partyPhone = "9448112233",
                    type = "GAVE",
                    amount = 12000.0,
                    dateMillis = now - (5 * day),
                    paymentMode = "BANK_TRANSFER",
                    referenceNo = "TRN-998271",
                    categoryTag = "Transport & Lab",
                    notes = "Bus Route #4 (Dharwad to School) fee"
                )
            )

            dao.insertTransaction(
                LedgerTransaction(
                    partyId = p3,
                    partyName = "Rohan Vinod Kulkarni",
                    partyPhone = "9731298765",
                    type = "GOT",
                    amount = 2500.0,
                    dateMillis = now - (2 * day),
                    paymentMode = "UPI",
                    referenceNo = "UPI-GPay-4432190",
                    categoryTag = "Hostel Fee",
                    notes = "Advance mess payment"
                )
            )

            // 4. Sample Invoices
            val sampleItemsJson1 = """
                [
                  {"title":"Tuition Fee - Quarter 1","hsnSac":"999293","quantity":1,"unitRate":4500.0,"discount":0.0,"taxPercent":0.0,"total":4500.0},
                  {"title":"Computer & Science Lab Fee","hsnSac":"999293","quantity":1,"unitRate":1000.0,"discount":500.0,"taxPercent":0.0,"total":500.0}
                ]
            """.trimIndent()

            dao.insertInvoice(
                Invoice(
                    invoiceNumber = "VVS/26-27/001",
                    partyId = p1,
                    partyName = "Aarav Rajesh Sharma",
                    partyPhone = "9845123456",
                    partyCategory = "Class 10-A",
                    dateMillis = now - (3 * day),
                    isGst = false,
                    subtotal = 5500.0,
                    discount = 500.0,
                    taxRatePercent = 0.0,
                    taxAmount = 0.0,
                    totalAmount = 5000.0,
                    paidAmount = 1500.0,
                    dueAmount = 3500.0,
                    status = "PARTIAL",
                    notes = "Term 1 Tuition Bill with Merit concession",
                    itemsJson = sampleItemsJson1
                )
            )

            // 5. Inventory Items (Books, Uniforms, Stationery)
            dao.insertInventoryItem(
                InventoryItem(
                    name = "NCERT Class 10 Textbook Complete Set",
                    sku = "BK-CLS10-NCERT",
                    category = "Books",
                    unit = "Set",
                    costPrice = 1200.0,
                    sellingPrice = 1450.0,
                    currentStock = 42,
                    minStockAlert = 10
                )
            )
            dao.insertInventoryItem(
                InventoryItem(
                    name = "School Navy Blue Blazer with Crest (Size 34)",
                    sku = "UNIF-BLZ-34",
                    category = "Uniforms",
                    unit = "Pcs",
                    costPrice = 750.0,
                    sellingPrice = 950.0,
                    currentStock = 8,
                    minStockAlert = 15 // triggers low stock alert!
                )
            )
            dao.insertInventoryItem(
                InventoryItem(
                    name = "Official Vidya Vijay Student Diary & Academic Calendar",
                    sku = "STAT-DIARY-26",
                    category = "Stationery",
                    unit = "Pcs",
                    costPrice = 60.0,
                    sellingPrice = 100.0,
                    currentStock = 180,
                    minStockAlert = 30
                )
            )
            dao.insertInventoryItem(
                InventoryItem(
                    name = "Physics & Chemistry Practical Lab Record Book",
                    sku = "LAB-REC-PC",
                    category = "Stationery",
                    unit = "Pcs",
                    costPrice = 85.0,
                    sellingPrice = 120.0,
                    currentStock = 3, // Low stock!
                    minStockAlert = 20
                )
            )

            // 6. Expenses
            dao.insertExpense(
                Expense(
                    title = "Faculty & Staff Monthly Payroll (Primary Section)",
                    category = "Salaries & Wages",
                    amount = 145000.0,
                    dateMillis = now - (4 * day),
                    paymentMode = "BANK_TRANSFER",
                    reference = "NEFT-SAL-0926",
                    paidTo = "Staff Payroll Account",
                    notes = "12 teachers and 4 support staff"
                )
            )
            dao.insertExpense(
                Expense(
                    title = "HESCOM Campus Electricity & Pump Power",
                    category = "Electricity & Utilities",
                    amount = 18450.0,
                    dateMillis = now - (2 * day),
                    paymentMode = "UPI",
                    reference = "UPI-HESCOM-7782",
                    paidTo = "HESCOM Electric Board",
                    notes = "Main campus August electricity bill"
                )
            )
            dao.insertExpense(
                Expense(
                    title = "School Buses 1 to 4 Diesel Refuel",
                    category = "Transport & Fuel",
                    amount = 12800.0,
                    dateMillis = now - (1 * day),
                    paymentMode = "CASH",
                    reference = "PETROL-PUMP-INV-441",
                    paidTo = "HP Auto Fuel Station",
                    notes = "Fleet weekly diesel filling"
                )
            )

            // 7. Initial Audit Log
            dao.insertAuditLog(
                AuditLog(
                    action = "System Initialized",
                    details = "Vidya Vijay School billing ledger database configured with academic year 2026-2027",
                    timestamp = now,
                    userRole = "Principal"
                )
            )
        }
    }
}
