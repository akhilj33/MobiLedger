package com.example.mobiledger.data.sources.room.transaction

import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.MonthlyTransactionSummaryEntity
import com.example.mobiledger.domain.entities.TransactionEntity

interface TransactionDb {
    suspend fun fetchMonthlySummary(monthYear: String): AppResult<MonthlyTransactionSummaryEntity>
    suspend fun saveMonthlySummary(monthYear: String, monthlySummary: MonthlyTransactionSummaryEntity)
    suspend fun hasMonthlySummary(monthYear: String): Boolean
    suspend fun saveTransaction(transactionEntity: TransactionEntity)
}

class TransactionDbImpl(private val transactionDao: TransactionDao, private val monthlySummaryDao: MonthlySummaryDao) : TransactionDb {
    override suspend fun fetchMonthlySummary(monthYear: String): AppResult<MonthlyTransactionSummaryEntity> {
        val monthlySummaryEntity = monthlySummaryDao.fetchMonthlySummary(monthYear)?.let {
            mapToMonthlySummaryEntity(it)
        }
        return if (monthlySummaryEntity != null) AppResult.Success(monthlySummaryEntity)
        else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
    }

    override suspend fun saveMonthlySummary(monthYear: String, monthlySummary: MonthlyTransactionSummaryEntity) {
        val monthlySummaryRoomItem = mapToMonthlySummaryRoomItem(monthYear, monthlySummary)
        monthlySummaryDao.saveMonthlySummary(monthlySummaryRoomItem)
    }

    override suspend fun hasMonthlySummary(monthYear: String): Boolean {
        val monthlySummaryCount = monthlySummaryDao.hasMonthlySummary(monthYear)
        return !(monthlySummaryCount == null || monthlySummaryCount == 0)
    }

    override suspend fun saveTransaction(transactionEntity: TransactionEntity) {
        val transactionsRoomItem = mapToTransactionRoomItemRoomItem(transactionEntity)
        transactionDao.saveTransaction(transactionsRoomItem)
    }
}

private fun mapToMonthlySummaryEntity(monthlySummaryRoomItem: MonthlySummaryRoomItem): MonthlyTransactionSummaryEntity {
    monthlySummaryRoomItem.apply {
        return MonthlyTransactionSummaryEntity(
            noOfTransaction,
            noOfIncomeTransaction,
            noOfExpenseTransaction,
            totalBalance,
            totalIncome,
            totalExpense
        )
    }
}

private fun mapToMonthlySummaryRoomItem(monthYear: String, monthlySummary: MonthlyTransactionSummaryEntity): MonthlySummaryRoomItem {
    monthlySummary.apply {
        return MonthlySummaryRoomItem(
            monthYear,
            noOfTransaction,
            noOfIncomeTransaction,
            noOfExpenseTransaction,
            totalBalance,
            totalIncome,
            totalExpense
        )
    }
}

private fun mapToTransactionRoomItemRoomItem(transactionEntity: TransactionEntity): TransactionRoomItem {
    transactionEntity.apply {
        return TransactionRoomItem(id, amount, category, description, transactionType, transactionTime)
    }
}