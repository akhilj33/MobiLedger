package com.example.mobiledger.data.sources.api

import com.example.mobiledger.common.utils.ConstantUtils
import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.ErrorMapper
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.FireBaseResult
import com.example.mobiledger.domain.entities.ExpenseCategoryListEntity
import com.example.mobiledger.domain.entities.IncomeCategoryListEntity
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface CategoryApi {
    suspend fun addDefaultIncomeCategories(uid: String, defaultCategoryList: List<String>): AppResult<Unit>
    suspend fun addDefaultExpenseCategories(uid: String, defaultCategoryList: List<String>): AppResult<Unit>
    suspend fun getDefaultIncomeCategories(): AppResult<IncomeCategoryListEntity>
    suspend fun getDefaultExpenseCategories(): AppResult<ExpenseCategoryListEntity>

    suspend fun getUserIncomeCategories(uid: String): AppResult<IncomeCategoryListEntity>
    suspend fun getUserExpenseCategories(uid: String): AppResult<ExpenseCategoryListEntity>
}

class CategoryApiImpl(private val firebaseDb: FirebaseFirestore) : CategoryApi {
    override suspend fun addDefaultIncomeCategories(uid: String, defaultCategoryList: List<String>): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null

        try {
            val docRef = firebaseDb.collection(ConstantUtils.USERS).document(uid)
                .collection(ConstantUtils.USER_CATEGORIES).document(ConstantUtils.INCOME_CATEGORY_LIST)

            response = docRef.set(IncomeCategoryListEntity(defaultCategoryList))
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

    override suspend fun addDefaultExpenseCategories(uid: String, defaultCategoryList: List<String>): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null

        try {
            val docRef = firebaseDb.collection(ConstantUtils.USERS).document(uid)
                .collection(ConstantUtils.USER_CATEGORIES).document(ConstantUtils.EXPENSE_CATEGORY_LIST)

            response = docRef.set(ExpenseCategoryListEntity(defaultCategoryList))
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

    override suspend fun getDefaultIncomeCategories(): AppResult<IncomeCategoryListEntity> {
        var response: Task<DocumentSnapshot>? = null
        var exception: Exception? = null

        try {
            firebaseDb.collection(ConstantUtils.CATEGORIES).document(ConstantUtils.INCOME)
            response = firebaseDb.collection(ConstantUtils.CATEGORIES).document(ConstantUtils.INCOME).get()
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

    override suspend fun getDefaultExpenseCategories(): AppResult<ExpenseCategoryListEntity> {
        var response: Task<DocumentSnapshot>? = null
        var exception: Exception? = null

        try {
            response = firebaseDb.collection(ConstantUtils.CATEGORIES).document(ConstantUtils.EXPENSE).get()
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


}

private fun categoryIncomeResultEntityMapper(category: DocumentSnapshot?): IncomeCategoryListEntity? {
    return category?.toObject(IncomeCategoryListEntity::class.java)
}

private fun categoryExpenseResultEntityMapper(category: DocumentSnapshot?): ExpenseCategoryListEntity? {
    return category?.toObject(ExpenseCategoryListEntity::class.java)
}