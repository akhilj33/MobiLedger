package com.example.mobiledger.data.sources.api

import com.example.mobiledger.common.utils.ConstantUtils.CATEGORY_AMOUNT
import com.example.mobiledger.common.utils.ConstantUtils.CATEGORY_TRANSACTION
import com.example.mobiledger.common.utils.ConstantUtils.EXPENSE_CATEGORY_LIST
import com.example.mobiledger.common.utils.ConstantUtils.INCOME_CATEGORY_LIST
import com.example.mobiledger.common.utils.ConstantUtils.MONTH
import com.example.mobiledger.common.utils.ConstantUtils.UNAUTHORIZED_ERROR_MSG
import com.example.mobiledger.common.utils.ConstantUtils.USERS
import com.example.mobiledger.common.utils.ConstantUtils.USER_CATEGORIES
import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.ErrorMapper
import com.example.mobiledger.data.sources.auth.AuthSource
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.FireBaseResult
import com.example.mobiledger.domain.entities.*
import com.example.mobiledger.presentation.budget.MonthlyCategorySummary
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

interface CategoryApi {
    suspend fun addDefaultIncomeCategories(uid: String, categoryList: List<String>): AppResult<Unit>
    suspend fun addDefaultExpenseCategories(uid: String, categoryList: List<String>): AppResult<Unit>

    suspend fun getUserIncomeCategories(uid: String): AppResult<IncomeCategoryListEntity>
    suspend fun getUserExpenseCategories(uid: String): AppResult<ExpenseCategoryListEntity>

    suspend fun updateUserIncomeCategory(uid: String, newIncomeCategory: IncomeCategoryListEntity): AppResult<Unit>
    suspend fun updateUserExpenseCategory(uid: String, newExpenseCategory: ExpenseCategoryListEntity): AppResult<Unit>

    suspend fun getMonthlyCategorySummary(uid: String, monthYear: String, category: String): AppResult<MonthlyCategorySummary?>
    suspend fun addMonthlyCategorySummary(
        uid: String,
        monthYear: String,
        category: String,
        monthlyCategorySummary: MonthlyCategorySummary
    ): AppResult<Unit>

    suspend fun deleteMonthlyCategorySummary(uid: String, monthYear: String, category: String): AppResult<Unit>
    suspend fun updateMonthlyCategoryAmount(uid: String, monthYear: String, category: String, categoryAmountChange: Long): AppResult<Unit>

    suspend fun getAllMonthlyCategorySummaries(uid: String, monthYear: String): AppResult<List<MonthlyCategorySummary>>

    suspend fun addMonthlyCategoryTransaction(uid: String, monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit>
    suspend fun deleteMonthlyCategoryTransaction(uid: String, monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit>

    suspend fun getMonthlyCategoryTransactionReferences(
        uid: String,
        monthYear: String,
        category: String
    ): AppResult<List<DocumentReferenceEntity>>

    suspend fun getTransactionFromReference(transRef: DocumentReference): AppResult<TransactionEntity>
}

class CategoryApiImpl(private val firebaseDb: FirebaseFirestore, private val authSource: AuthSource) : CategoryApi {

    override suspend fun addDefaultIncomeCategories(uid: String, categoryList: List<String>): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null

        try {
            val docRef = firebaseDb.collection(USERS).document(uid)
                .collection(USER_CATEGORIES).document(INCOME_CATEGORY_LIST)

            response = docRef.set(IncomeCategoryListEntity(categoryList))
            response.await(60000)
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

    suspend fun <T> Task<T>.await(milliSec: Long): T {
        return withTimeout(milliSec) {
            this@await.await()
        }
    }

    override suspend fun addDefaultExpenseCategories(uid: String, categoryList: List<String>): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null

        try {
            val docRef = firebaseDb.collection(USERS).document(uid)
                .collection(USER_CATEGORIES).document(EXPENSE_CATEGORY_LIST)

            response = docRef.set(ExpenseCategoryListEntity(categoryList))
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

    override suspend fun getUserIncomeCategories(uid: String): AppResult<IncomeCategoryListEntity> {
        var response: Task<DocumentSnapshot>? = null
        var exception: Exception? = null

        try {
            response = firebaseDb.collection(USERS).document(uid)
                .collection(USER_CATEGORIES).document(INCOME_CATEGORY_LIST).get()
            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                val userInfo = categoryIncomeResultEntityMapper(result.data?.result)
                if (userInfo != null) {
                    AppResult.Success(userInfo)
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun getUserExpenseCategories(uid: String): AppResult<ExpenseCategoryListEntity> {
        var response: Task<DocumentSnapshot>? = null
        var exception: Exception? = null

        try {
            response = firebaseDb.collection(USERS).document(uid)
                .collection(USER_CATEGORIES).document(EXPENSE_CATEGORY_LIST).get()
            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                val userInfo = categoryExpenseResultEntityMapper(result.data?.result)
                if (userInfo != null) {
                    AppResult.Success(userInfo)
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun updateUserIncomeCategory(uid: String, newIncomeCategory: IncomeCategoryListEntity): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null

        try {
            val docRef = firebaseDb.collection(USERS).document(uid)
                .collection(USER_CATEGORIES).document(INCOME_CATEGORY_LIST)

            response = docRef.update(newIncomeCategory.toMutableMap())
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

    override suspend fun addMonthlyCategoryTransaction(
        uid: String,
        monthYear: String,
        transactionEntity: TransactionEntity
    ): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null

        val transRef: DocumentReference =
            firebaseDb.document("/${USERS}/$uid/Months/$monthYear/Transaction/${transactionEntity.id}")

        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                UNAUTHORIZED_ERROR_MSG
            )
            response = firebaseDb.collection(USERS)
                .document(uid)
                .collection(MONTH)
                .document(monthYear)
                .collection(CATEGORY_TRANSACTION)
                .document(transactionEntity.category)
                .collection(CATEGORY_TRANSACTION)
                .document(transactionEntity.id)
                .set(TransactionReference(transRef))
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

    override suspend fun deleteMonthlyCategoryTransaction(
        uid: String,
        monthYear: String,
        transactionEntity: TransactionEntity
    ): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null

        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                UNAUTHORIZED_ERROR_MSG
            )
            response = firebaseDb.collection(USERS)
                .document(uid)
                .collection(MONTH)
                .document(monthYear)
                .collection(CATEGORY_TRANSACTION)
                .document(transactionEntity.category)
                .collection(CATEGORY_TRANSACTION)
                .document(transactionEntity.id)
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

    override suspend fun getMonthlyCategoryTransactionReferences(
        uid: String,
        monthYear: String,
        category: String
    ): AppResult<List<DocumentReferenceEntity>> {
        var response: Task<QuerySnapshot>? = null
        var exception: Exception? = null

        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                UNAUTHORIZED_ERROR_MSG
            )
            response = firebaseDb.collection(USERS)
                .document(uid)
                .collection(MONTH)
                .document(monthYear)
                .collection(CATEGORY_TRANSACTION)
                .document(category)
                .collection(CATEGORY_TRANSACTION)
                .get()
            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null && result.data.result != null) {
                    AppResult.Success(transactionEntityListMapper(result.data.result!!))
                } else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun getTransactionFromReference(transRef: DocumentReference): AppResult<TransactionEntity> {
        var response: Task<DocumentSnapshot>? = null
        var exception: Exception? = null

        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                UNAUTHORIZED_ERROR_MSG
            )
            response = transRef.get()
            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null && result.data.result != null) {
                    val entity = mapToTransactionEntity(result.data.result!!)
                    if (entity != null)
                        AppResult.Success(entity)
                    else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                } else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }

    }

    override suspend fun updateMonthlyCategoryAmount(
        uid: String,
        monthYear: String,
        category: String,
        categoryAmountChange: Long
    ): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null

        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                UNAUTHORIZED_ERROR_MSG
            )


            response = firebaseDb.collection(USERS)
                .document(uid)
                .collection(MONTH)
                .document(monthYear)
                .collection(CATEGORY_TRANSACTION)
                .document(category)
                .update(CATEGORY_AMOUNT, FieldValue.increment(categoryAmountChange))

            response.await()

        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null) {
                    AppResult.Success(Unit)
                } else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun getAllMonthlyCategorySummaries(uid: String, monthYear: String): AppResult<List<MonthlyCategorySummary>> {
        var response: Task<QuerySnapshot>? = null
        var exception: Exception? = null

        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                UNAUTHORIZED_ERROR_MSG
            )
            response = firebaseDb.collection(USERS)
                .document(uid)
                .collection(MONTH)
                .document(monthYear)
                .collection(CATEGORY_TRANSACTION)
                .get()
            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null && result.data.result != null) AppResult.Success(mapToCategoryList(result.data.result!!))
                else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun getMonthlyCategorySummary(uid: String, monthYear: String, category: String): AppResult<MonthlyCategorySummary?> {
        var response: Task<DocumentSnapshot>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                UNAUTHORIZED_ERROR_MSG
            )
            response = firebaseDb.collection(USERS)
                .document(uid)
                .collection(MONTH)
                .document(monthYear)
                .collection(CATEGORY_TRANSACTION)
                .document(category)
                .get()

            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null && result.data.result != null) {
                    // If doc doesn't exist, sending null as success value indicating that no doc exists
                    if ((result.data.result as DocumentSnapshot).exists()) {
                        val monthlyCategoryResult = monthlyCategoryEntityMapper(result.data.result as DocumentSnapshot)
                        if (monthlyCategoryResult != null) AppResult.Success(monthlyCategoryResult)
                        else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                    } else AppResult.Success(null)
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun addMonthlyCategorySummary(
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
                UNAUTHORIZED_ERROR_MSG
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

    override suspend fun deleteMonthlyCategorySummary(uid: String, monthYear: String, category: String): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                UNAUTHORIZED_ERROR_MSG
            )
            response = firebaseDb.collection(USERS)
                .document(uid)
                .collection(MONTH)
                .document(monthYear)
                .collection(CATEGORY_TRANSACTION)
                .document(category)
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

    override suspend fun updateUserExpenseCategory(uid: String, newExpenseCategory: ExpenseCategoryListEntity): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null
        try {
            val docRef = firebaseDb.collection(USERS).document(uid)
                .collection(USER_CATEGORIES).document(EXPENSE_CATEGORY_LIST)

            response = docRef.update(newExpenseCategory.toMutableMap())
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

private fun categoryIncomeResultEntityMapper(category: DocumentSnapshot?): IncomeCategoryListEntity? {
    return category?.toObject(IncomeCategoryListEntity::class.java)
}

private fun categoryExpenseResultEntityMapper(category: DocumentSnapshot?): ExpenseCategoryListEntity? {
    return category?.toObject(ExpenseCategoryListEntity::class.java)
}

private fun monthlyCategoryEntityMapper(user: DocumentSnapshot): MonthlyCategorySummary? {
    return user.toObject(MonthlyCategorySummary::class.java)
}

private fun mapToCategoryList(data: QuerySnapshot): List<MonthlyCategorySummary> {
    return data.map { it.toObject(MonthlyCategorySummary::class.java) }
}

private fun transactionEntityListMapper(result: QuerySnapshot): List<DocumentReferenceEntity> {
    return result.map { it.toObject(DocumentReferenceEntity::class.java) }
}

private fun mapToTransactionEntity(result: DocumentSnapshot): TransactionEntity? {
    return result.toObject(TransactionEntity::class.java)
}
