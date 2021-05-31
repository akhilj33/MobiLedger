package com.example.mobiledger.data.sources.api

import com.example.mobiledger.common.utils.ConstantUtils
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
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

interface CategoryApi {
    suspend fun addDefaultIncomeCategories(uid: String, categoryList: List<String>): AppResult<Unit>
    suspend fun addDefaultExpenseCategories(uid: String, categoryList: List<String>): AppResult<Unit>

    suspend fun getUserIncomeCategories(uid: String): AppResult<IncomeCategoryListEntity>
    suspend fun getUserExpenseCategories(uid: String): AppResult<ExpenseCategoryListEntity>

    suspend fun updateUserIncomeCategory(uid: String, newIncomeCategory: IncomeCategoryListEntity): AppResult<Unit>
    suspend fun updateUserExpenseCategory(uid: String, newExpenseCategory: ExpenseCategoryListEntity): AppResult<Unit>

    suspend fun getMonthlyCategorySummary(uid: String, monthYear: String, category: String): AppResult<MonthlyCategorySummary>
    suspend fun addCategoryTransaction(uid: String, monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit>
    suspend fun getAllMonthlyCategories(uid: String, monthYear: String): AppResult<List<MonthlyCategorySummary>>
}

class CategoryApiImpl(private val firebaseDb: FirebaseFirestore, private val authSource: AuthSource) : CategoryApi {
    override suspend fun addDefaultIncomeCategories(uid: String, categoryList: List<String>): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null

        try {
            val docRef = firebaseDb.collection(ConstantUtils.USERS).document(uid)
                .collection(ConstantUtils.USER_CATEGORIES).document(ConstantUtils.INCOME_CATEGORY_LIST)

            response = docRef.set(IncomeCategoryListEntity(categoryList))
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

    override suspend fun addDefaultExpenseCategories(uid: String, categoryList: List<String>): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null

        try {
            val docRef = firebaseDb.collection(ConstantUtils.USERS).document(uid)
                .collection(ConstantUtils.USER_CATEGORIES).document(ConstantUtils.EXPENSE_CATEGORY_LIST)

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
            response = firebaseDb.collection(ConstantUtils.USERS).document(uid)
                .collection(ConstantUtils.USER_CATEGORIES).document(ConstantUtils.INCOME_CATEGORY_LIST).get()
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
            response = firebaseDb.collection(ConstantUtils.USERS).document(uid)
                .collection(ConstantUtils.USER_CATEGORIES).document(ConstantUtils.EXPENSE_CATEGORY_LIST).get()
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
            val docRef = firebaseDb.collection(ConstantUtils.USERS).document(uid)
                .collection(ConstantUtils.USER_CATEGORIES).document(ConstantUtils.INCOME_CATEGORY_LIST)

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


    override suspend fun addCategoryTransaction(uid: String, monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null

        val transRef: DocumentReference = firebaseDb.document("/${ConstantUtils.USERS}/$uid/Months/$monthYear/Transaction/${transactionEntity.id}")

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
                .document(transactionEntity.category)
                .collection(ConstantUtils.CATEGORY_TRANSACTION)
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

    override suspend fun getAllMonthlyCategories(uid: String, monthYear: String): AppResult<List<MonthlyCategorySummary>> {
        var response: Task<QuerySnapshot>? = null
        var exception: Exception? = null

        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(ErrorCodes.FIREBASE_UNAUTHORIZED,
                ConstantUtils.UNAUTHORIZED_ERROR_MSG)
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
                if (result.data!=null && result.data.result!=null) AppResult.Success(mapToCategoryList(result.data.result!!))
                else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun getMonthlyCategorySummary(uid: String, monthYear: String, category: String): AppResult<MonthlyCategorySummary> {
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
                .collection(ConstantUtils.CATEGORY_TRANSACTION)
                .document(category)
                .get()

            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null && result.data.result!=null) {
                    val monthlyCategoryResult = monthlyCategoryEntityMapper(result.data.result!!)
                    if(monthlyCategoryResult!=null)
                            AppResult.Success(monthlyCategoryResult)
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

    override suspend fun updateUserExpenseCategory(uid: String, newExpenseCategory: ExpenseCategoryListEntity): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null
        try {
            val docRef = firebaseDb.collection(ConstantUtils.USERS).document(uid)
                .collection(ConstantUtils.USER_CATEGORIES).document(ConstantUtils.EXPENSE_CATEGORY_LIST)

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
    if (user.data==null) return MonthlyCategorySummary()
    return user.toObject(MonthlyCategorySummary::class.java)
}

private fun mapToCategoryList(data: QuerySnapshot): List<MonthlyCategorySummary> {
    return data.map { it.toObject(MonthlyCategorySummary::class.java) }
}
