package com.example.mobiledger.data.sources.room.transaction

import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.MonthlyTransactionSummaryEntity
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.enums.EditCategoryTransactionType
import com.example.mobiledger.domain.enums.TransactionType

interface TransactionDb {
    suspend fun fetchMonthlySummary(monthYear: String): AppResult<MonthlyTransactionSummaryEntity>
    suspend fun saveMonthlySummary(monthYear: String, monthlySummary: MonthlyTransactionSummaryEntity)
    suspend fun hasMonthlySummary(monthYear: String): Boolean
    suspend fun updateMonthlySummary(monthYear: String,
                                     transactionType: TransactionType,
                                     amountChanged: Long, editCategoryTransactionType: EditCategoryTransactionType)
    suspend fun saveTransaction(monthYear: String, transactionEntity: TransactionEntity)
    suspend fun saveTransactionList(monthYear: String, transactionEntityList: List<TransactionEntity>)
    suspend fun hasTransactions(): Boolean
    suspend fun fetchTransactions(monthYear: String): AppResult<List<TransactionEntity>>
    suspend fun deleteTransaction(transactionId: String)
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

    override suspend fun updateMonthlySummary(
        monthYear: String,
        transactionType: TransactionType,
        amountChanged: Long,
        editCategoryTransactionType: EditCategoryTransactionType
    ) {
        val oldMonthlySummaryRoomItem = monthlySummaryDao.fetchMonthlySummary(monthYear)

        var newTotalBalance = oldMonthlySummaryRoomItem?.totalBalance?:0
        var newTotalIncome = oldMonthlySummaryRoomItem?.totalIncome?:0
        var newTotalExpense = oldMonthlySummaryRoomItem?.totalExpense?:0


        if (transactionType == TransactionType.Income) {
                newTotalIncome += amountChanged
                newTotalBalance += amountChanged

        } else if (transactionType == TransactionType.Expense) {
            newTotalExpense += amountChanged
            newTotalBalance -= amountChanged
        }

       monthlySummaryDao.saveMonthlySummary(MonthlySummaryRoomItem(monthYear, editCategoryTransactionType.increment, editCategoryTransactionType.increment,
            editCategoryTransactionType.increment, newTotalBalance, newTotalIncome, newTotalExpense))

    }

    override suspend fun saveTransaction(monthYear: String, transactionEntity: TransactionEntity) {
        val transactionsRoomItem = mapToTransactionRoomItemList(monthYear, transactionEntity)
        transactionDao.saveTransaction(transactionsRoomItem)
    }

    override suspend fun saveTransactionList(monthYear: String, transactionEntityList: List<TransactionEntity>) {
        val transactionRoomItemList = transactionEntityList.map {
            mapToTransactionRoomItemList(monthYear, it)
        }
        transactionDao.saveTransactionList(transactionRoomItemList)
    }

    override suspend fun hasTransactions(): Boolean {
        val transactionsCount = transactionDao.hasTransactions()
        return !(transactionsCount == null || transactionsCount == 0)
    }

    override suspend fun fetchTransactions(monthYear: String): AppResult<List<TransactionEntity>> {
        val transactionsList = transactionDao.fetchAllTransactions(monthYear)?.map {
            mapToTransactionsEntity(it)
        }
        return if (transactionsList != null) AppResult.Success(transactionsList)
        else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
    }

    override suspend fun deleteTransaction(transactionId: String) {
        transactionDao.deleteTransaction(transactionId)
    }
}

private fun mapToTransactionsEntity(transactionsRoomItem: TransactionRoomItem): TransactionEntity {
    transactionsRoomItem.apply {
        return TransactionEntity(name, amount, category, description, transactionType, transactionTime)
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

private fun mapToTransactionRoomItemList(monthYear: String, transactionEntity: TransactionEntity): TransactionRoomItem {
    transactionEntity.apply {
        return TransactionRoomItem(id, monthYear, name, amount, category, description, transactionType, transactionTime)
    }
}
