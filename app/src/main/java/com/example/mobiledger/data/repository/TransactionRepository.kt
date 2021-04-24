package com.example.mobiledger.data.repository

import com.example.mobiledger.data.sources.api.model.UserApi
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.MonthlyTransactionSummaryEntity
import com.example.mobiledger.domain.entities.TransactionEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface TransactionRepository {
    suspend fun getMonthlyTransactionFromFirebaseDb(uid: String, monthYear: String): AppResult<MonthlyTransactionSummaryEntity?>
    suspend fun addUserTransactionToFirebase(
        uid: String,
        monthYear: String,
        transactionId: String,
        transaction: TransactionEntity,
        monthlyTransactionSummaryEntity: MonthlyTransactionSummaryEntity?
    ): Boolean
}

class TransactionRepositoryImpl(private val userApi: UserApi, private val dispatcher: CoroutineDispatcher = Dispatchers.IO) :
    TransactionRepository {

    override suspend fun getMonthlyTransactionFromFirebaseDb(uid: String, monthYear: String): AppResult<MonthlyTransactionSummaryEntity?> {
        return withContext(dispatcher) {
            userApi.getMonthlyTransactionDetail(uid, monthYear)
        }
    }

    override suspend fun addUserTransactionToFirebase(
        uid: String,
        monthYear: String,
        transactionId: String,
        transaction: TransactionEntity,
        monthlyTransactionSummaryEntity: MonthlyTransactionSummaryEntity?
    ): Boolean {
        return withContext(dispatcher) {
            userApi.addUserTransactionToFirebase(uid, monthYear, transactionId, transaction, monthlyTransactionSummaryEntity)
        }
    }
}