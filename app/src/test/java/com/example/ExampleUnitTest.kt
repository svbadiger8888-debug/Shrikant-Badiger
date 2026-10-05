package com.example

import com.example.util.AppLanguage
import com.example.util.FormattingUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testUpiUriGeneration() {
    val upiUri = FormattingUtils.buildUpiUri(
      upiId = "vidyavijayschool@sbi",
      payeeName = "Vidya Vijay School",
      amount = 3500.0,
      note = "Term 1 School Fee"
    )

    assertTrue(upiUri.startsWith("upi://pay?"))
    assertTrue(upiUri.contains("pa=vidyavijayschool%40sbi"))
    assertTrue(upiUri.contains("am=3500.00"))
    assertTrue(upiUri.contains("cu=INR"))
  }

  @Test
  fun testReminderMessageComposition() {
    val englishMsg = FormattingUtils.composeReminderMessage(
      schoolName = "Vidya Vijay School",
      studentName = "Aarav Sharma",
      studentId = "VVS-26-101",
      dueAmount = 4500.0,
      upiId = "vidyavijayschool@sbi",
      language = AppLanguage.ENGLISH
    )
    assertTrue(englishMsg.contains("Vidya Vijay School"))
    assertTrue(englishMsg.contains("Aarav Sharma"))
    assertTrue(englishMsg.contains("vidyavijayschool@sbi"))

    val hindiMsg = FormattingUtils.composeReminderMessage(
      schoolName = "Vidya Vijay School",
      studentName = "Aarav Sharma",
      studentId = "VVS-26-101",
      dueAmount = 4500.0,
      upiId = "vidyavijayschool@sbi",
      language = AppLanguage.HINDI
    )
    assertTrue(hindiMsg.contains("आदरणीय अभिभावक"))
  }

  @Test
  fun testBalanceCalculationLogic() {
    // Opening balance 5000 due
    var currentBalance = 5000.0
    // Got 1500 (Jama) -> balance should decrease
    currentBalance -= 1500.0
    assertEquals(3500.0, currentBalance, 0.001)

    // Gave 2000 (More Due) -> balance should increase
    currentBalance += 2000.0
    assertEquals(5500.0, currentBalance, 0.001)
  }

  @Test
  fun testCustomerAndTransactionEntities() {
    val customer: com.example.data.model.Customer = com.example.data.model.Customer(
      id = 1L,
      businessId = 1L,
      name = "Rahul Sharma",
      phone = "9876543210",
      gradeOrCategory = "Class 10-A",
      openingBalance = 5000.0,
      balanceType = "DUE",
      currentBalance = 5000.0
    )
    assertEquals("Rahul Sharma", customer.name)
    assertEquals(5000.0, customer.currentBalance, 0.001)

    val transaction: com.example.data.model.Transaction = com.example.data.model.Transaction(
      id = 101L,
      businessId = 1L,
      partyId = customer.id,
      partyName = customer.name,
      partyPhone = customer.phone,
      type = "GOT",
      amount = 2000.0,
      paymentMode = "UPI",
      referenceNo = "UTR123456",
      categoryTag = "Tuition Fee"
    )
    assertEquals("GOT", transaction.type)
    assertEquals(2000.0, transaction.amount, 0.001)
    assertEquals(1L, transaction.partyId)
  }
}
