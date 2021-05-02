package com.example.mobiledger.data.repository

import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.sources.api.TransactionApi
import com.example.mobiledger.data.sources.cache.CacheSource
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.MonthlyTransactionSummaryEntity
import com.example.mobiledger.domain.entities.TransactionEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface TransactionRepository {
    suspend fun getMonthlyTransactionFromFirebaseDb(monthYear: String): AppResult<MonthlyTransactionSummaryEntity?>
    suspend fun addUserTransactionToFirebase(
        monthYear: String,
        transactionEntity: TransactionEntity,
        monthlyTransactionSummaryEntity: MonthlyTransactionSummaryEntity?
    ): AppResult<Unit>
}

class TransactionRepositoryImpl(
    private val transactionApi: TransactionApi, private val cacheSource: CacheSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : TransactionRepository {

    override suspend fun getMonthlyTransactionFromFirebaseDb(monthYear: String): AppResult<MonthlyTransactionSummaryEntity?> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) transactionApi.getMonthlyTransactionDetail(uId, monthYear)
            else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun addUserTransactionToFirebase(
        monthYear: String,
        transactionEntity: TransactionEntity,
        monthlyTransactionSummaryEntity: MonthlyTransactionSummaryEntity?
    ): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null)
                transactionApi.addUserTransactionToFirebase(uId, monthYear, transactionEntity, monthlyTransactionSummaryEntity)
            else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }
}