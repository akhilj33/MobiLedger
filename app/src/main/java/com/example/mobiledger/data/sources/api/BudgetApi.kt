package com.example.mobiledger.data.sources.api

import com.example.mobiledger.common.utils.ConstantUtils
import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.ErrorMapper
import com.example.mobiledger.data.sources.auth.AuthSource
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.FireBaseResult
import com.example.mobiledger.domain.entities.TransactionEntity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

interface BudgetApi {
    suspend fun getCategoryBudgetListByMonth(uid: String, monthYear: String): AppResult<List<TransactionEntity>>
}

class BudgetApiImpl(private val firebaseDb: FirebaseFirestore, private val authSource: AuthSource) : BudgetApi {

    override suspend fun getCategoryBudgetListByMonth(uid: String, monthYear: String): AppResult<List<TransactionEntity>> {

        var response: Task<QuerySnapshot>? = null
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
                .collection(ConstantUtils.CATEGORY_TRANSACTION)
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

}


private fun transactionEntityMapper(result: QuerySnapshot): List<TransactionEntity> {
    return result.map { it.toObject(TransactionEntity::class.java) }
}