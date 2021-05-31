package com.example.mobiledger.data.sources.api

import com.example.mobiledger.common.utils.ConstantUtils
import com.example.mobiledger.common.utils.ConstantUtils.BUDGET
import com.example.mobiledger.common.utils.ConstantUtils.BUDGET_DETAILS
import com.example.mobiledger.common.utils.ConstantUtils.CATEGORY_BUDGET
import com.example.mobiledger.common.utils.ConstantUtils.CATEGORY_TRANSACTION
import com.example.mobiledger.common.utils.ConstantUtils.MONTH
import com.example.mobiledger.common.utils.ConstantUtils.USERS
import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.ErrorMapper
import com.example.mobiledger.data.sources.auth.AuthSource
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.FireBaseResult
import com.example.mobiledger.domain.entities.MonthlyTransactionSummaryEntity
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.entities.TransactionReference
import com.example.mobiledger.domain.entities.toMutableMap
import com.example.mobiledger.presentation.budget.MonthlyCategorySummary
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

interface TransactionApi {
    suspend fun getMonthlySummaryEntity(uid: String, monthYear: String): AppResult<MonthlyTransactionSummaryEntity>
    suspend fun addMonthlySummaryToFirebase(
        uid: String, monthYear: String, monthlySummaryEntity: MonthlyTransactionSummaryEntity
    ): AppResult<Unit>

    suspend fun updateMonthlySummary(uid: String, monthYear: String, monthlySummaryEntity: MonthlyTransactionSummaryEntity): AppResult<Unit>
    suspend fun getTransactionListByMonth(uid: String, monthYear: String): AppResult<List<TransactionEntity>>
    suspend fun addUserTransactionToFirebase(uid: String, monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit>
    suspend fun deleteTransaction(uid: String, transactionId: String, monthYear: String): AppResult<Unit>

    suspend fun updateMonthlyCategorySummary(
        uid: String,
        monthYear: String,
        category: String,
        monthlyCategorySummary: MonthlyCategorySummary
    ): AppResult<Unit>

    suspend fun updateExpenseInBudget(uid: String, monthYear: String, monthlyCategorySummary: MonthlyCategorySummary): AppResult<Unit>

}

class TransactionApiImpl(private val firebaseDb: FirebaseFirestore, private val authSource: AuthSource) : TransactionApi {

    override suspend fun getMonthlySummaryEntity(uid: String, monthYear: String): AppResult<MonthlyTransactionSummaryEntity> {
        var response: Task<DocumentSnapshot>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                ConstantUtils.UNAUTHORIZED_ERROR_MSG
            )
            response = firebaseDb.collection(USERS)
                .document(uid)
                .collection(MONTH)
                .document(monthYear)
                .get()

            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null && result.data.result!=null) {
                   val monthlyResult =  monthlySummaryEntityMapper(result.data.result!!)
                    if (monthlyResult!=null) AppResult.Success(monthlyResult)
                    else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun addMonthlySummaryToFirebase(
        uid: String,
        monthYear: String,
        monthlySummaryEntity: MonthlyTransactionSummaryEntity
    ): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null

        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                ConstantUtils.UNAUTHORIZED_ERROR_MSG
            )

            response = firebaseDb.collection(USERS)
                .document(uid)
                .collection(MONTH)
                .document(monthYear)
                .set(monthlySummaryEntity)
            response.await()

        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                AppResult.Success(Unit)
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun updateMonthlySummary(
        uid: String,
        monthYear: String,
        monthlySummaryEntity: MonthlyTransactionSummaryEntity
    ): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null

        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                ConstantUtils.UNAUTHORIZED_ERROR_MSG
            )
            response = firebaseDb.collection(USERS)
                .document(uid)
                .collection(MONTH)
                .document(monthYear)
                .update(monthlySummaryEntity.toMutableMap())
            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                AppResult.Success(Unit)
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun getTransactionListByMonth(uid: String, monthYear: String): AppResult<List<TransactionEntity>> {
        var response: Task<QuerySnapshot>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                ConstantUtils.UNAUTHORIZED_ERROR_MSG
            )
            response = firebaseDb.collection(USERS)
                .document(uid)
                .collection(MONTH)
                .document(monthYear)
                .collection(ConstantUtils.TRANSACTION)
                .get()

            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null && result.data.result != null) {
                    AppResult.Success(transactionEntityMapper(result.data.result!!))
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
        transactionEntity: TransactionEntity
    ): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null

        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                ConstantUtils.UNAUTHORIZED_ERROR_MSG
            )

            response = firebaseDb.collection(USERS)
                .document(uid)
                .collection(MONTH)
                .document(monthYear)
                .collection(ConstantUtils.TRANSACTION)
                .document(transactionEntity.id)
                .set(transactionEntity)

            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                AppResult.Success(Unit)
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun deleteTransaction(uid: String, transactionId: String, monthYear: String): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null

        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                ConstantUtils.UNAUTHORIZED_ERROR_MSG
            )

            response = firebaseDb.collection(USERS)
                .document(uid)
                .collection(MONTH)
                .document(monthYear)
                .collection(ConstantUtils.TRANSACTION)
                .document(transactionId)
                .delete()

            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                AppResult.Success(Unit)
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun updateMonthlyCategorySummary(
        uid: String,
        monthYear: String,
        category: String,
        monthlyCategorySummary: MonthlyCategorySummary
    ): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                ConstantUtils.UNAUTHORIZED_ERROR_MSG
            )
            response = firebaseDb.collection(USERS)
                .document(uid)
                .collection(MONTH)
                .document(monthYear)
                .collection(CATEGORY_TRANSACTION)
                .document(category)
                .set(monthlyCategorySummary)

            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                AppResult.Success(Unit)
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun updateExpenseInBudget(
        uid: String,
        monthYear: String,
        monthlyCategorySummary: MonthlyCategorySummary
    ): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null

        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                ConstantUtils.UNAUTHORIZED_ERROR_MSG
            )
            val expenseMap = mapOf(Pair("categoryExpense", monthlyCategorySummary.categoryAmount))
            response = firebaseDb.collection(USERS)
                .document(uid)
                .collection(MONTH)
                .document(monthYear)
                .collection(BUDGET)
                .document(BUDGET_DETAILS)
                .collection(CATEGORY_BUDGET)
                .document(monthlyCategorySummary.categoryName)
                .update(expenseMap)

            response.await()

        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                AppResult.Success(Unit)
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }
}

private fun transactionEntityMapper(result: QuerySnapshot): List<TransactionEntity> {
    return result.map { it.toObject(TransactionEntity::class.java) }
}

private fun monthlySummaryEntityMapper(user: DocumentSnapshot): MonthlyTransactionSummaryEntity? {
    if (user.data==null) return MonthlyTransactionSummaryEntity()
    return user.toObject(MonthlyTransactionSummaryEntity::class.java)
}
