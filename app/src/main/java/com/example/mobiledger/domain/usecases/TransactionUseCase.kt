package com.example.mobiledger.domain.usecases

import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.repository.TransactionRepository
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.MonthlyTransactionSummaryEntity
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.entities.isEmpty
import com.example.mobiledger.domain.enums.EditCategoryTransactionType
import com.example.mobiledger.domain.enums.TransactionType
import com.example.mobiledger.presentation.budget.MonthlyCategorySummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface TransactionUseCase {
    suspend fun getMonthlySummaryEntity(monthYear: String, isPTR: Boolean = false): AppResult<MonthlyTransactionSummaryEntity>
    suspend fun addMonthlySummaryToFirebase(monthYear: String, monthlySummaryEntity: MonthlyTransactionSummaryEntity): AppResult<Unit>
    suspend fun updateMonthlySummary(monthYear: String, monthlySummaryEntity: MonthlyTransactionSummaryEntity): AppResult<Unit>
    suspend fun addUserTransactionToFirebase(monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit>
    suspend fun getTransactionListByMonth(monthYear: String, isPTR: Boolean = false): AppResult<List<TransactionEntity>>
    suspend fun deleteTransaction(transactionId: String, monthYear: String): AppResult<Unit>
    suspend fun updateMonthlySummerData(
        monthYear: String, transactionType: TransactionType, amountChanged: Long,
        editCategoryTransactionType: EditCategoryTransactionType
    ): AppResult<Unit>

    suspend fun updateExpenseInBudget(monthYear: String, monthlyCategorySummary: MonthlyCategorySummary): AppResult<Unit>
    suspend fun updateMonthlyTransaction(monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit>
    suspend fun updateOrAddTransactionSummary(monthYear: String, newTransactionEntity: TransactionEntity, isPTR: Boolean = false): AppResult<Unit>
}

class TransactionUseCaseImpl(private val transactionRepository: TransactionRepository) : TransactionUseCase {
    override suspend fun getMonthlySummaryEntity(monthYear: String, isPTR: Boolean): AppResult<MonthlyTransactionSummaryEntity> {
        return transactionRepository.getMonthlySummaryEntity(monthYear, isPTR)
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

    override suspend fun getTransactionListByMonth(monthYear: String, isPTR: Boolean): AppResult<List<TransactionEntity>> {
        return transactionRepository.getTransactionListByMonth(monthYear, isPTR)
    }

    override suspend fun deleteTransaction(transactionId: String, monthYear: String): AppResult<Unit> {
        return transactionRepository.deleteTransaction(transactionId, monthYear)
    }

    override suspend fun updateMonthlySummerData(
        monthYear: String,
        transactionType: TransactionType,
        amountChanged: Long,
        editCategoryTransactionType: EditCategoryTransactionType
    ): AppResult<Unit> {
        return transactionRepository.updateMonthlySummerData(monthYear, transactionType, amountChanged, editCategoryTransactionType)
    }

    override suspend fun updateExpenseInBudget(
        monthYear: String,
        monthlyCategorySummary: MonthlyCategorySummary
    ): AppResult<Unit> = transactionRepository.updateExpenseInBudget(monthYear, monthlyCategorySummary)

    override suspend fun updateMonthlyTransaction(monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit> {
        return transactionRepository.addUserTransactionToFirebase(monthYear, transactionEntity)
    }

    /**
     * Responsibilities
     * 1- If month is already present, It updates monthly summary data
     * 2- If month is not present, It adds monthly category data
     */
    override suspend fun updateOrAddTransactionSummary(monthYear: String, newTransactionEntity: TransactionEntity, isPTR: Boolean): AppResult<Unit> {
        return withContext(Dispatchers.IO) {
            when (val result = getMonthlySummaryEntity(monthYear, isPTR)) {
                is AppResult.Success -> {
                    if (result.data.isEmpty()) addMonthlySummaryToFirebase(monthYear, getUpdatedMonthlySummary(newTransactionEntity))
                    else updateMonthlySummerData(monthYear, newTransactionEntity.transactionType, newTransactionEntity.amount, EditCategoryTransactionType.ADD)
                }
                is AppResult.Failure -> AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
        }
    }
}

    private suspend fun getUpdatedMonthlySummary(
        transactionEntity: TransactionEntity
    ): MonthlyTransactionSummaryEntity {
        return withContext(Dispatchers.IO) {

            val noOfTransaction = 1L
            var noOfIncome = 0L
            var noOfExpense = 0L
            var totalIncome = 0L
            var totalExpense = 0L
            if (transactionEntity.transactionType == TransactionType.Income) {
                noOfIncome += 1
                totalIncome = transactionEntity.amount
            } else if (transactionEntity.transactionType == TransactionType.Expense) {
                noOfExpense = 1
                totalExpense = transactionEntity.amount
            }
            val totalBalance = totalIncome - totalExpense
            MonthlyTransactionSummaryEntity(noOfTransaction, noOfIncome, noOfExpense, totalBalance, totalIncome, totalExpense)
        }
    }
