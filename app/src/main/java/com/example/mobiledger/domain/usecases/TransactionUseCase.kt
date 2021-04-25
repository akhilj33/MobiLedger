package com.example.mobiledger.domain.usecases

import com.example.mobiledger.data.repository.TransactionRepository
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.MonthlyTransactionSummaryEntity
import com.example.mobiledger.domain.entities.TransactionEntity

interface TransactionUseCase {
    suspend fun getMonthlyTransactionSummaryFromDb(uid: String, monthYear: String): AppResult<MonthlyTransactionSummaryEntity?>
    suspend fun addUserTransactionToFirebase(
        uid: String,
        monthYear: String,
        transactionId: String,
        transaction: TransactionEntity,
        monthlyTransactionSummaryEntity: MonthlyTransactionSummaryEntity?
    ): Boolean
}

class TransactionUseCaseImpl(private val transactionRepository: TransactionRepository) : TransactionUseCase {
    override suspend fun getMonthlyTransactionSummaryFromDb(uid: String, monthYear: String): AppResult<MonthlyTransactionSummaryEntity?> {
        return transactionRepository.getMonthlyTransactionFromFirebaseDb(uid, monthYear)
    }

    override suspend fun addUserTransactionToFirebase(
        uid: String,
        monthYear: String,
        transactionId: String,
        transaction: TransactionEntity,
        monthlyTransactionSummaryEntity: MonthlyTransactionSummaryEntity?
    ): Boolean {
        return transactionRepository.addUserTransactionToFirebase(
            uid,
            monthYear,
            transactionId,
            transaction,
            monthlyTransactionSummaryEntity
        )
    }
}
