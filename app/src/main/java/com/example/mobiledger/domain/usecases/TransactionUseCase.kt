package com.example.mobiledger.domain.usecases

import com.example.mobiledger.data.repository.TransactionRepository
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.MonthlyTransactionSummaryEntity
import com.example.mobiledger.domain.entities.TransactionEntity

interface TransactionUseCase {
    suspend fun getMonthlyTransactionSummaryFromDb(monthYear: String): AppResult<MonthlyTransactionSummaryEntity?>
    suspend fun addUserTransactionToFirebase(
        monthYear: String,
        transactionEntity: TransactionEntity,
        monthlyTransactionSummaryEntity: MonthlyTransactionSummaryEntity?
    ): AppResult<Unit>
}

class TransactionUseCaseImpl(private val transactionRepository: TransactionRepository) : TransactionUseCase {
    override suspend fun getMonthlyTransactionSummaryFromDb(monthYear: String): AppResult<MonthlyTransactionSummaryEntity?> {
        return transactionRepository.getMonthlyTransactionFromFirebaseDb(monthYear)
    }

    override suspend fun addUserTransactionToFirebase(
        monthYear: String,
        transactionEntity: TransactionEntity,
        monthlyTransactionSummaryEntity: MonthlyTransactionSummaryEntity?
    ): AppResult<Unit> {
        return transactionRepository.addUserTransactionToFirebase(
            monthYear,
            transactionEntity,
            monthlyTransactionSummaryEntity
        )
    }
}
