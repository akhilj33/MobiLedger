package com.example.mobiledger.domain.usecases

import com.example.mobiledger.data.repository.TransactionRepository
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.MonthlyTransactionSummaryEntity
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.presentation.budget.MonthlyCategorySummary

interface TransactionUseCase {
    suspend fun getMonthlySummaryEntity(monthYear: String): AppResult<MonthlyTransactionSummaryEntity>
    suspend fun addMonthlySummaryToFirebase(monthYear: String, monthlySummaryEntity: MonthlyTransactionSummaryEntity): AppResult<Unit>
    suspend fun updateMonthlySummary(monthYear: String, monthlySummaryEntity: MonthlyTransactionSummaryEntity): AppResult<Unit>
    suspend fun addUserTransactionToFirebase(monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit>
    suspend fun getTransactionListByMonth(monthYear: String): AppResult<List<TransactionEntity>>
    suspend fun deleteTransaction(transactionId: String, monthYear: String): AppResult<Unit>
    suspend fun addCategoryTransaction(monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit>
    suspend fun getMonthlyCategorySummary(monthYear: String, category: String): AppResult<MonthlyCategorySummary?>
    suspend fun updateMonthlyCategoryBudgetData(
        monthYear: String,
        category: String,
        monthlyCategorySummary: MonthlyCategorySummary
    ): AppResult<Unit>

    suspend fun updateExpenseInBudget(monthYear: String, monthlyCategorySummary: MonthlyCategorySummary): AppResult<Unit>
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

    override suspend fun getTransactionListByMonth(monthYear: String): AppResult<List<TransactionEntity>> {
        return transactionRepository.getTransactionListByMonth(monthYear)
    }

    override suspend fun deleteTransaction(transactionId: String, monthYear: String): AppResult<Unit> {
        return transactionRepository.deleteTransaction(transactionId, monthYear)
    }

    override suspend fun addCategoryTransaction(monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit> {
        return transactionRepository.addCategoryTransaction(monthYear, transactionEntity)
    }

    override suspend fun getMonthlyCategorySummary(monthYear: String, category: String): AppResult<MonthlyCategorySummary?> {
        return transactionRepository.getMonthlyCategorySummary(monthYear, category)
    }

    override suspend fun updateMonthlyCategoryBudgetData(
        monthYear: String,
        category: String,
        monthlyCategorySummary: MonthlyCategorySummary
    ): AppResult<Unit> {
        return transactionRepository.updateMonthlyCategoryBudget(monthYear, category, monthlyCategorySummary)
    }

    override suspend fun updateExpenseInBudget(
        monthYear: String,
        monthlyCategorySummary: MonthlyCategorySummary
    ): AppResult<Unit> = transactionRepository.updateExpenseInBudget(monthYear, monthlyCategorySummary)
}
