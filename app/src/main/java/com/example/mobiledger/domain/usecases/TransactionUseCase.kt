package com.example.mobiledger.domain.usecases

import com.example.mobiledger.data.repository.TransactionRepository
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.MonthlyTransactionSummaryEntity
import com.example.mobiledger.domain.entities.TransactionEntity

interface TransactionUseCase {
    suspend fun getMonthlySummaryEntity(monthYear: String): AppResult<MonthlyTransactionSummaryEntity>
    suspend fun addMonthlySummaryToFirebase(monthYear: String, monthlySummaryEntity: MonthlyTransactionSummaryEntity): AppResult<Unit>
    suspend fun updateMonthlySummary(monthYear: String, monthlySummaryEntity: MonthlyTransactionSummaryEntity): AppResult<Unit>
    suspend fun addUserTransactionToFirebase(monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit>
}

class TransactionUseCaseImpl(private val transactionRepository: TransactionRepository) : TransactionUseCase {
    override suspend fun getMonthlySummaryEntity(monthYear: String): AppResult<MonthlyTransactionSummaryEntity> {
        return transactionRepository.getMonthlySummaryEntity(monthYear)
    }

    override suspend fun addMonthlySummaryToFirebase(
        monthYear: String, monthlySummaryEntity: MonthlyTransactionSummaryEntity
    ): AppResult<Unit> {
        return transactionRepository.addMonthlySummaryToFirebase(monthYear, monthlySummaryEntity)
    }

    override suspend fun updateMonthlySummary(monthYear: String, monthlySummaryEntity: MonthlyTransactionSummaryEntity): AppResult<Unit> {
        return transactionRepository.updateMonthlySummary(monthYear, monthlySummaryEntity)
    }

    override suspend fun addUserTransactionToFirebase(monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit> {
        return transactionRepository.addUserTransactionToFirebase(monthYear, transactionEntity)
    }
}
