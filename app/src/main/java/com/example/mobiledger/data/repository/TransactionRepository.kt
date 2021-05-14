package com.example.mobiledger.data.repository

import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.sources.api.TransactionApi
import com.example.mobiledger.data.sources.cache.CacheSource
import com.example.mobiledger.data.sources.room.transaction.TransactionDb
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.MonthlyTransactionSummaryEntity
import com.example.mobiledger.domain.entities.TransactionEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface TransactionRepository {
    suspend fun getMonthlySummaryEntity(monthYear: String): AppResult<MonthlyTransactionSummaryEntity>
    suspend fun addMonthlySummaryToFirebase(monthYear: String, monthlySummaryEntity: MonthlyTransactionSummaryEntity): AppResult<Unit>
    suspend fun updateMonthlySummary(monthYear: String, monthlySummaryEntity: MonthlyTransactionSummaryEntity): AppResult<Unit>
    suspend fun getTransactionListByMonth(monthYear: String): AppResult<List<TransactionEntity>>
    suspend fun addUserTransactionToFirebase(monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit>
    suspend fun deleteTransaction(transactionId: String, monthYear: String): AppResult<Unit>
}

class TransactionRepositoryImpl(
    private val transactionApi: TransactionApi, private val cacheSource: CacheSource, private val transactionDb: TransactionDb,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : TransactionRepository {

    override suspend fun getMonthlySummaryEntity(monthYear: String): AppResult<MonthlyTransactionSummaryEntity> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                val monthlySummaryExists = transactionDb.hasMonthlySummary(monthYear)
                if (!monthlySummaryExists) {
                    when (val firebaseResult = transactionApi.getMonthlySummaryEntity(uId, monthYear)) {
                        is AppResult.Success -> {
                            transactionDb.saveMonthlySummary(monthYear, firebaseResult.data ?: MonthlyTransactionSummaryEntity())
                        }
                        is AppResult.Failure -> {
                            return@withContext AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                        }
                    }
                }
                transactionDb.fetchMonthlySummary(monthYear)
            } else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun addMonthlySummaryToFirebase(
        monthYear: String,
        monthlySummaryEntity: MonthlyTransactionSummaryEntity
    ): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) transactionApi.addMonthlySummaryToFirebase(uId, monthYear, monthlySummaryEntity).also {
                if (it is AppResult.Success) transactionDb.saveMonthlySummary(monthYear, monthlySummaryEntity)
            }
            else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun updateMonthlySummary(monthYear: String, monthlySummaryEntity: MonthlyTransactionSummaryEntity): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) transactionApi.updateMonthlySummary(uId, monthYear, monthlySummaryEntity).also {
                if (it is AppResult.Success) transactionDb.saveMonthlySummary(monthYear, monthlySummaryEntity)
            }
            else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun getTransactionListByMonth(monthYear: String): AppResult<List<TransactionEntity>> {
        return withContext(dispatcher){
            val uId = cacheSource.getUID()
            if(uId!=null) {
                val transactionsExists = transactionDb.hasTransactions()
                if (!transactionsExists) {
                    when (val firebaseResult = transactionApi.getTransactionListByMonth(uId, monthYear)) {
                        is AppResult.Success -> {
                            transactionDb.saveTransactionList(monthYear, firebaseResult.data)
                        }
                        is AppResult.Failure -> {
                            return@withContext AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                        }
                    }
                }
                transactionDb.fetchTransactions(monthYear)
            }
            else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun addUserTransactionToFirebase(monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null)
                transactionApi.addUserTransactionToFirebase(uId, monthYear, transactionEntity).also {
                    if (it is AppResult.Success) transactionDb.saveTransaction(monthYear, transactionEntity)
                }
            else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun deleteTransaction(transactionId: String, monthYear: String): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null)
                transactionApi.deleteTransaction(uId, transactionId, monthYear).also {
                    if (it is AppResult.Success) transactionDb.deleteTransaction(transactionId)
                }
            else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }
}