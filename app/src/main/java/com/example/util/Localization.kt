package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    HINDI("hi", "Hindi", "हिंदी"),
    KANNADA("kn", "Kannada", "ಕನ್ನಡ"),
    MARATHI("mr", "Marathi", "मराठी"),
    GUJARATI("gu", "Gujarati", "ગુજરાતી")
}

object AppStrings {
    private val translations = mapOf(
        AppLanguage.ENGLISH to mapOf(
            "app_title" to "Vidya Vijay School",
            "app_subtitle" to "Smart Fee Billing & Digital Khata",
            "nav_dashboard" to "Dashboard",
            "nav_ledger" to "Ledger / Khata",
            "nav_invoices" to "Fee Invoices",
            "nav_inventory" to "Books & Stock",
            "nav_expenses" to "Expenses",
            "nav_reports" to "Reports",
            "nav_upi" to "UPI QR",
            "nav_settings" to "Settings",

            "today_collection" to "Today's Collection",
            "total_pending_due" to "Total Fee Dues (Udhar)",
            "total_advance_got" to "Advance Received (Jama)",
            "net_outstanding" to "Net Outstanding",
            "quick_actions" to "Quick Actions",
            "add_student" to "Add Student / Party",
            "record_fee" to "Receive Fee",
            "create_bill" to "Create Bill",
            "upi_collection" to "UPI QR Code",

            "you_gave" to "You Gave (Fee Due / Udhar)",
            "you_got" to "You Got (Fee Paid / Jama)",
            "balance_due" to "Due to School",
            "balance_advance" to "Advance Paid",
            "balance_settled" to "Cleared / Settled",
            "send_reminder" to "WhatsApp Reminder",
            "share_statement" to "Share Statement",
            "view_bills" to "View Invoices",

            "all" to "All",
            "dues_only" to "Pending Dues",
            "advance_only" to "Advance",
            "cleared_only" to "Zero Balance",
            "search_hint" to "Search by Student Name, Roll No or Phone...",

            "cash" to "Cash",
            "upi" to "UPI",
            "cheque" to "Cheque",
            "bank_transfer" to "Bank Transfer",
            "card" to "Card",
            "other" to "Other",

            "amount" to "Amount",
            "date" to "Date",
            "reference_no" to "Reference / Receipt No / UPI UTR",
            "notes" to "Notes / Remarks",
            "category" to "Category / Fee Head",
            "save" to "Save & Update",
            "cancel" to "Cancel",
            "delete" to "Delete",
            "export_csv" to "Export Data",
            "aging_analysis" to "Outstanding Aging Analysis",
            "academic_year" to "Academic Session",
            "user_role" to "Active Role",
            "language" to "App Language"
        ),
        AppLanguage.HINDI to mapOf(
            "app_title" to "विद्या विजय स्कूल",
            "app_subtitle" to "स्मार्ट फीस बिलिंग और डिजिटल खाता",
            "nav_dashboard" to "डैशबोर्ड",
            "nav_ledger" to "खाता बही",
            "nav_invoices" to "फीस बिल",
            "nav_inventory" to "किताबें व स्टॉक",
            "nav_expenses" to "खर्च (Expenses)",
            "nav_reports" to "रिपोर्ट्स",
            "nav_upi" to "यूपीआई क्यूआर",
            "nav_settings" to "सेटिंग्स",

            "today_collection" to "आज का कलेक्शन (जमा)",
            "total_pending_due" to "कुल बकाया फीस (उधार)",
            "total_advance_got" to "एडवांस फीस (जमा)",
            "net_outstanding" to "कुल बकाया राशि",
            "quick_actions" to "त्वरित कार्य",
            "add_student" to "छात्र / पार्टी जोड़ें",
            "record_fee" to "फीस जमा करें",
            "create_bill" to "बिल बनाएं",
            "upi_collection" to "QR से भुगतान",

            "you_gave" to "आपने दिए (उधार / फीस देय)",
            "you_got" to "आपको मिले (जमा / फीस प्राप्त)",
            "balance_due" to "बकाया (उधार)",
            "balance_advance" to "अग्रिम (जमा)",
            "balance_settled" to "पूर्ण चुकता",
            "send_reminder" to "व्हाट्सएप तगादा / रिमाइंडर",
            "share_statement" to "खाता स्टेटमेंट शेयर करें",
            "view_bills" to "बिल देखें",

            "all" to "सभी",
            "dues_only" to "केवल बकाया",
            "advance_only" to "केवल एडवांस",
            "cleared_only" to "शून्य बकाया",
            "search_hint" to "छात्र का नाम, रोल नंबर या फोन खोजें...",

            "cash" to "नकद (Cash)",
            "upi" to "यूपीआई (UPI)",
            "cheque" to "चेक (Cheque)",
            "bank_transfer" to "बैंक ट्रांसफर",
            "card" to "कार्ड",
            "other" to "अन्य",

            "amount" to "रकम (राशि)",
            "date" to "तारीख",
            "reference_no" to "रसीद नंबर / UPI UTR",
            "notes" to "विवरण / नोट",
            "category" to "फीस का प्रकार",
            "save" to "सुरक्षित करें",
            "cancel" to "रद्द करें",
            "delete" to "हटाएं",
            "export_csv" to "डेटा निर्यात",
            "aging_analysis" to "बकाया अवधि विश्लेषण",
            "academic_year" to "शैक्षणिक सत्र",
            "user_role" to "सक्रिय भूमिका",
            "language" to "भाषा बदलें"
        ),
        AppLanguage.KANNADA to mapOf(
            "app_title" to "ವಿದ್ಯಾ ವಿಜಯ ಶಾಲೆ",
            "app_subtitle" to "ಶಾಲಾ ಶುಲ್ಕ ಬಿಲ್ಲಿಂಗ್ ಮತ್ತು ಡಿಜಿಟಲ್ ಖಾತೆ",
            "nav_dashboard" to "ಡ್ಯಾಶ್‌ಬೋರ್ಡ್",
            "nav_ledger" to "ಖಾತೆ ಪುಸ್ತಕ",
            "nav_invoices" to "ಶುಲ್ಕ ರಸೀದಿಗಳು",
            "nav_inventory" to "ಪುಸ್ತಕ ಮತ್ತು ದಾಸ್ತಾನು",
            "nav_expenses" to "ಖರ್ಚುಗಳು",
            "nav_reports" to "ವರದಿಗಳು",
            "nav_upi" to "UPI ಕ್ಯೂಆರ್",
            "nav_settings" to "ಸೆಟ್ಟಿಂಗ್ಸ್",

            "today_collection" to "ಇಂದಿನ ಸಂಗ್ರಹ (ಜಮಾ)",
            "total_pending_due" to "ಬಾಕಿ ಶುಲ್ಕ (ಉದ್ದಾರ)",
            "total_advance_got" to "ಮುಂಗಡ ಶುಲ್ಕ",
            "net_outstanding" to "ಒಟ್ಟು ಬಾಕಿ ಮೊತ್ತ",
            "quick_actions" to "ತ್ವರಿತ ಕ್ರಿಯೆಗಳು",
            "add_student" to "ವಿದ್ಯಾರ್ಥಿ ಸೇರಿಸಿ",
            "record_fee" to "ಶುಲ್ಕ ಜಮಾ ಮಾಡಿ",
            "create_bill" to "ರಶೀದಿ ಸೃಷ್ಟಿಸಿ",
            "upi_collection" to "ಕ್ಯೂಆರ್ ಕೋಡ್",

            "you_gave" to "ನೀವು ನೀಡಿದ್ದು (ಉದ್ದಾರ / ಬಾಕಿ)",
            "you_got" to "ನಿಮಗೆ ಬಂದಿದ್ದು (ಜಮಾ / ಪಾವತಿ)",
            "balance_due" to "ಬಾಕಿ ಇದೆ (ಉದ್ದಾರ)",
            "balance_advance" to "ಮುಂಗಡ ಪಾವತಿ",
            "balance_settled" to "ಚುಕ್ತಾ ಆಗಿದೆ",
            "send_reminder" to "ವಾಟ್ಸಾಪ್ ರಿಮೈಂಡರ್ ಕಳುಹಿಸಿ",
            "share_statement" to "ಖಾತೆ ವಿವರ ಹಂಚಿಕೊಳ್ಳಿ",
            "view_bills" to "ರಶೀದಿಗಳನ್ನು ನೋಡಿ",

            "all" to "ಎಲ್ಲವೂ",
            "dues_only" to "ಬಾಕಿ ಮಾತ್ರ",
            "advance_only" to "ಮುಂಗಡ",
            "cleared_only" to "ಚುಕ್ತಾ",
            "search_hint" to "ವಿದ್ಯಾರ್ಥಿ ಹೆಸರು, ರೋಲ್ ನಂ ಅಥವಾ ಫೋನ್ ಹುಡುಕಿ...",

            "cash" to "ನಗದು (Cash)",
            "upi" to "ಯುಪಿಐ (UPI)",
            "cheque" to "ಚೆಕ್ (Cheque)",
            "bank_transfer" to "ಬ್ಯಾಂಕ್ ವರ್ಗಾವಣೆ",
            "card" to "ಕಾರ್ಡ್",
            "other" to "ಇತರೆ",

            "amount" to "ಮೊತ್ತ",
            "date" to "ದಿನಾಂಕ",
            "reference_no" to "ರಸೀದಿ ನಂ / ಯುಪಿಐ ಯುಟಿಆರ್",
            "notes" to "ಟಿಪ್ಪಣಿಗಳು",
            "category" to "ಶುಲ್ಕ ವರ್ಗ",
            "save" to "ಉಳಿಸಿ",
            "cancel" to "ರದ್ದುಮಾಡಿ",
            "delete" to "ಅಳಿಸಿ",
            "export_csv" to "ಡೇಟಾ ರಫ್ತು",
            "aging_analysis" to "ಬಾಕಿ ದಿನಗಳ ವಿಶ್ಲೇಷಣೆ",
            "academic_year" to "ಶೈಕ್ಷಣಿಕ ವರ್ಷ",
            "user_role" to "ಪಾತ್ರ",
            "language" to "ಭಾಷೆ"
        ),
        AppLanguage.MARATHI to mapOf(
            "app_title" to "विद्या विजय शाळा",
            "app_subtitle" to "स्मार्ट फी बिलिंग आणि डिजिटल खातेवही",
            "nav_dashboard" to "डॅशबोर्ड",
            "nav_ledger" to "खातेवही (खतावणी)",
            "nav_invoices" to "फी पावत्या व बिल",
            "nav_inventory" to "पुस्तके व साठा",
            "nav_expenses" to "खर्च",
            "nav_reports" to "अहवाल (Reports)",
            "nav_upi" to "UPI QR",
            "nav_settings" to "सेटिंग्ज",

            "today_collection" to "आजची वसुली (जमा)",
            "total_pending_due" to "एकूण बाकी फी (उधार)",
            "total_advance_got" to "ऍडव्हान्स फी",
            "net_outstanding" to "एकूण येणे बाकी",
            "quick_actions" to "जलद कृती",
            "add_student" to "विद्यार्थी जोडा",
            "record_fee" to "फी जमा करा",
            "create_bill" to "बिल बनवा",
            "upi_collection" to "QR कोड",

            "you_gave" to "दिले (उधारी / देय)",
            "you_got" to "मिळाले (जमा / प्राप्त)",
            "balance_due" to "बाकी रक्कम",
            "balance_advance" to "अग्रिम रक्कम",
            "balance_settled" to "पूर्ण भरणा",
            "send_reminder" to "व्हॉट्सॲप आठवण (Reminder)",
            "share_statement" to "हिशोब पाठवा",
            "view_bills" to "पावत्या पहा",

            "all" to "सर्व",
            "dues_only" to "फक्त बाकी",
            "advance_only" to "फक्त ऍडव्हान्स",
            "cleared_only" to "शून्य बाकी",
            "search_hint" to "नाव, रोल नंबर किंवा फोन शोधा...",

            "cash" to "रोख (Cash)",
            "upi" to "यूपीआय",
            "cheque" to "धनादेश (Cheque)",
            "bank_transfer" to "बँक ट्रान्सफर",
            "card" to "कार्ड",
            "other" to "इतर",

            "amount" to "रक्कम",
            "date" to "तारीख",
            "reference_no" to "पावती क्र. / UTR",
            "notes" to "नोंद",
            "category" to "फी प्रकार",
            "save" to "जतन करा",
            "cancel" to "रद्द करा",
            "delete" to "हटवा",
            "export_csv" to "डेटा निर्यात",
            "aging_analysis" to "थकबाकी कालावधी अहवाल",
            "academic_year" to "शैक्षणिक वर्ष",
            "user_role" to "पद",
            "language" to "भाषा"
        ),
        AppLanguage.GUJARATI to mapOf(
            "app_title" to "વિદ્યા વિજય શાળા",
            "app_subtitle" to "સ્માર્ટ ફી બિલિંગ અને ખાતાવહી",
            "nav_dashboard" to "ડેશબોર્ડ",
            "nav_ledger" to "ખાતાવહી",
            "nav_invoices" to "ફી બિલ",
            "nav_inventory" to "પુસ્તકો અને સ્ટોક",
            "nav_expenses" to "ખર્ચ",
            "nav_reports" to "અહેવાલ",
            "nav_upi" to "UPI QR",
            "nav_settings" to "સેટિંગ્સ",

            "today_collection" to "આજની જમા રકમ",
            "total_pending_due" to "કુલ બાકી ફી (ઉધાર)",
            "total_advance_got" to "એડવાન્સ ફી",
            "net_outstanding" to "કુલ લેણી રકમ",
            "quick_actions" to "ઝડપી ક્રિયાઓ",
            "add_student" to "વિદ્યાર્થી ઉમેરો",
            "record_fee" to "ફી જમા કરો",
            "create_bill" to "બિલ બનાવો",
            "upi_collection" to "QR કોડ",

            "you_gave" to "તમે આપ્યા (ઉધાર / બાકી)",
            "you_got" to "તમને મળ્યા (જમા / પ્રાપ્ત)",
            "balance_due" to "બાકી રકમ (ઉધાર)",
            "balance_advance" to "એડવાન્સ જમા",
            "balance_settled" to "ચૂકતે થયેલ",
            "send_reminder" to "વોટ્સએપ રીમાઇન્ડર",
            "share_statement" to "સ્ટેટમેન્ટ શેર કરો",
            "view_bills" to "બિલ જુઓ",

            "all" to "બધા",
            "dues_only" to "માત્ર બાકી",
            "advance_only" to "માત્ર એડવાન્સ",
            "cleared_only" to "શૂન્ય બાકી",
            "search_hint" to "વિદ્યાર્થીનું નામ, રોલ નંબર અથવા ફોન શોધો...",

            "cash" to "રોકડ (Cash)",
            "upi" to "યુપીઆઈ (UPI)",
            "cheque" to "ચેક",
            "bank_transfer" to "બેંક ટ્રાન્સફર",
            "card" to "કાર્ડ",
            "other" to "અન્ય",

            "amount" to "રકમ",
            "date" to "તારીખ",
            "reference_no" to "રસીદ નંબર / UTR",
            "notes" to "નોંધ",
            "category" to "ફી કેટેગરી",
            "save" to "સાચવો",
            "cancel" to "રદ કરો",
            "delete" to "કાઢી નાખો",
            "export_csv" to "ડેટા એક્સપોર્ટ",
            "aging_analysis" to "બાકી વિશ્લેષણ",
            "academic_year" to "શૈક્ષણિક વર્ષ",
            "user_role" to "હોદ્દો",
            "language" to "ભાષા"
        )
    )

    fun get(key: String, language: AppLanguage): String {
        return translations[language]?.get(key)
            ?: translations[AppLanguage.ENGLISH]?.get(key)
            ?: key
    }
}

object FormattingUtils {
    private val inrFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }

    fun formatInr(amount: Double): String {
        return try {
            val formatted = inrFormatter.format(amount)
            if (formatted.startsWith("₹")) formatted else "₹ $formatted"
        } catch (_: Exception) {
            "₹ %.2f".format(amount)
        }
    }

    fun formatDate(timeMillis: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        return sdf.format(Date(timeMillis))
    }

    fun formatDateTime(timeMillis: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)
        return sdf.format(Date(timeMillis))
    }

    /**
     * Builds standard NPCI UPI Intent URI for seamless payment via GPay / PhonePe / Paytm / BHIM
     */
    fun buildUpiUri(
        upiId: String,
        payeeName: String,
        amount: Double? = null,
        note: String = "Vidya Vijay School Fee"
    ): String {
        fun safeEncode(s: String): String = try {
            java.net.URLEncoder.encode(s, "UTF-8").replace("+", "%20")
        } catch (_: Exception) {
            s.replace(" ", "%20")
        }
        val cleanNote = safeEncode(note)
        val cleanName = safeEncode(payeeName)
        val cleanUpi = safeEncode(upiId)
        val amountParam = if (amount != null && amount > 0) "&am=%.2f".format(amount) else ""
        return "upi://pay?pa=$cleanUpi&pn=$cleanName$amountParam&cu=INR&tn=$cleanNote"
    }

    /**
     * Composes polite payment reminder message for WhatsApp / SMS with UPI Link
     */
    fun composeReminderMessage(
        schoolName: String,
        studentName: String,
        studentId: String,
        dueAmount: Double,
        upiId: String,
        language: AppLanguage
    ): String {
        val formattedAmount = formatInr(dueAmount)
        val upiLink = buildUpiUri(upiId, schoolName, dueAmount, "Fees for $studentName")

        return when (language) {
            AppLanguage.HINDI -> """
                आदरणीय अभिभावक,
                
                यह संदेश *${schoolName}* की ओर से है।
                छात्र: *${studentName}* (${studentId})
                बकाया फीस: *${formattedAmount}*
                
                कृपया यथाशीघ्र फीस का भुगतान करें।
                UPI आईडी: ${upiId}
                सीधे भुगतान लिंक:
                ${upiLink}
                
                धन्यवाद,
                ${schoolName} लेखा विभाग
            """.trimIndent()

            AppLanguage.KANNADA -> """
                ಗೌರವಾನ್ವಿತ ಪೋಷಕರೇ,
                
                ಇದು *${schoolName}* ವತಿಯಿಂದ ಜ್ಞಾಪನಾ ಪತ್ರ.
                ವಿದ್ಯಾರ್ಥಿ: *${studentName}* (${studentId})
                ಬಾಕಿ ಶುಲ್ಕ ಮೊತ್ತ: *${formattedAmount}*
                
                ದಯವಿಟ್ಟು ಶಾಲಾ ಶುಲ್ಕವನ್ನು ಶೀಘ್ರವಾಗಿ ಪಾವತಿಸಿ.
                UPI ID: ${upiId}
                ನೇರ ಪಾವತಿ ಲಿಂಕ್:
                ${upiLink}
                
                ಧನ್ಯವಾದಗಳು,
                ${schoolName} ಖಾತೆ ವಿಭಾಗ
            """.trimIndent()

            AppLanguage.MARATHI -> """
                आदरणीय पालक,
                
                *${schoolName}* कडून विनम्र स्मरणपत्र:
                विद्यार्थी: *${studentName}* (${studentId})
                थकबाकी फी: *${formattedAmount}*
                
                कृपया शाळेची फी लवकरात लवकर भरावी ही विनंती.
                UPI ID: ${upiId}
                पेमेंट लिंक:
                ${upiLink}
                
                धन्यवाद,
                ${schoolName}
            """.trimIndent()

            AppLanguage.GUJARATI -> """
                આદરણીય વાલીશ્રી,
                
                *${schoolName}* તરફથી નમ્ર સ્મૃતિપત્ર:
                વિદ્યાર્થી: *${studentName}* (${studentId})
                બાકી ફી: *${formattedAmount}*
                
                કૃપા કરીને શાળા ફી વહેલી તકે જમા કરાવો.
                UPI ID: ${upiId}
                પેમેન્ટ લિંક:
                ${upiLink}
                
                આભાર,
                ${schoolName}
            """.trimIndent()

            else -> """
                Dear Parent / Guardian,
                
                Greetings from *${schoolName}*.
                Student: *${studentName}* (${studentId})
                Outstanding Fee Due: *${formattedAmount}*
                
                Kindly clear the pending school fees at your earliest convenience.
                School UPI ID: ${upiId}
                Pay instantly via UPI:
                ${upiLink}
                
                Thank you,
                Accounts Department, ${schoolName}
            """.trimIndent()
        }
    }

    fun shareText(context: Context, text: String, title: String = "Share via") {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, title)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun openUpiPayment(context: Context, upiUriString: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(upiUriString)
            }
            val chooser = Intent.createChooser(intent, "Pay via UPI App")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (_: Exception) {
            // Fallback to sharing payment details
            shareText(context, "Pay to: $upiUriString", "Share Payment Link")
        }
    }
}
