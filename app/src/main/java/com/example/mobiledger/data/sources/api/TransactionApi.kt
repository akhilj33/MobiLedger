package com.example.mobiledger.data.sources.api

import com.example.mobiledger.common.utils.ConstantUtils
import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.ErrorMapper
import com.example.mobiledger.data.sources.auth.AuthSource
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.FireBaseResult
import com.example.mobiledger.domain.entities.MonthlyTransactionSummaryEntity
import com.example.mobiledger.domain.entities.TransactionEntity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface TransactionApi {
    suspend fun getMonthlyTransactionDetail(uid: String, monthYear: String): AppResult<MonthlyTransactionSummaryEntity?>
    suspend fun addUserTransactionToFirebase(
        uid: String,
        monthYear: String,
        transactionEntity: TransactionEntity,
        monthlyTransactionSummaryEntity: MonthlyTransactionSummaryEntity?
    ): AppResult<Unit>
}

class TransactionApiImpl(private val firebaseDb: FirebaseFirestore, private val authSource: AuthSource) : TransactionApi {

    override suspend fun getMonthlyTransactionDetail(uid: String, monthYear: String): AppResult<MonthlyTransactionSummaryEntity?> {
        var response: Task<DocumentSnapshot>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                ConstantUtils.UNAUTHORIZED_ERROR_MSG
            )
            response = firebaseDb.collection(ConstantUtils.USERS)
                .document(uid)
                .collection(ConstantUtils.MONTH)
                .document(monthYear)
                .get()

            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null) {
                    AppResult.Success(monthlySummaryEntityMapper(result.data.result))
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun addUserTransactionToFirebase(
        uid: String,
        monthYear: String,
        transactionEntity: TransactionEntity,
        monthlyTransactionSummaryEntity: MonthlyTransactionSummaryEntity?
    ): AppResult<Unit> {
        lateinit var newMonthlySummary: MonthlyTransactionSummaryEntity
        var response: Task<Void>? = null
        var exception: Exception? = null

        val timeInMills = transactionEntity.transactionTime?.seconds.toString()

        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                ConstantUtils.UNAUTHORIZED_ERROR_MSG
            )
            if (monthlyTransactionSummaryEntity == null) {
                val docMonthRef = firebaseDb.collection(ConstantUtils.USERS)
                    .document(uid)
                    .collection(ConstantUtils.MONTH)
                    .document(monthYear)

                newMonthlySummary = MonthlyTransactionSummaryEntity(0, 0, 0, 0, 0, 0)
                docMonthRef.set(newMonthlySummary).await()
            }

            response = firebaseDb.collection(ConstantUtils.USERS)
                .document(uid)
                .collection(ConstantUtils.MONTH)
                .document(monthYear)
                .collection(ConstantUtils.TRANSACTION)
                .document(timeInMills)
                .set(transactionEntity)

            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (monthlyTransactionSummaryEntity != null)
                    updateMonthlySummary(uid, monthYear, monthlyTransactionSummaryEntity, transactionEntity)
                else
                    updateMonthlySummary(uid, monthYear, newMonthlySummary, transactionEntity)
                AppResult.Success(Unit)
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    private suspend fun updateMonthlySummary(
        uid: String, monthYear: String,
        monthlyTransactionSummaryEntity: MonthlyTransactionSummaryEntity,
        transaction: TransactionEntity
    ): Boolean {
        val noOfTransaction = monthlyTransactionSummaryEntity.noOfTransaction?.plus(1)
        var noOfIncome = monthlyTransactionSummaryEntity.noOfIncomeTransaction
        var noOfExpense = monthlyTransactionSummaryEntity.noOfExpenseTransaction
        var totalIncome = monthlyTransactionSummaryEntity.totalIncome
        var totalExpense = monthlyTransactionSummaryEntity.totalExpense
        if (transaction.transactionType == ConstantUtils.INCOME) {
            noOfIncome = noOfIncome?.plus(1)
            totalIncome = totalIncome?.plus(transaction.amount!!)
        } else {
            noOfExpense = noOfExpense?.plus(1)
            totalExpense = totalExpense?.plus(transaction.amount!!)
        }
        return try {
            val docRef = firebaseDb.collection(ConstantUtils.USERS)
                .document(uid)
                .collection(ConstantUtils.MONTH)
                .document(monthYear)

            docRef.update(ConstantUtils.NO_OF_TRANSACTION, noOfTransaction).await()
            docRef.update(ConstantUtils.NO_OF_INCOME_TRANSACTION, noOfIncome).await()
            docRef.update(ConstantUtils.NO_OF_EXPENSE_TRANSACTION, noOfExpense).await()
            docRef.update(ConstantUtils.TOTAL_INCOME, totalIncome).await()
            docRef.update(ConstantUtils.TOTAL_EXPENSE, totalExpense).await()
            docRef.update(ConstantUtils.TOTAL_BALANCE, totalIncome!! - totalExpense!!).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}

private fun monthlySummaryEntityMapper(user: DocumentSnapshot?): MonthlyTransactionSummaryEntity? {
    return user?.toObject(MonthlyTransactionSummaryEntity::class.java)
}