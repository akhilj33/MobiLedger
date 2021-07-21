package com.example.mobiledger.data.sources.api

import com.example.mobiledger.common.utils.ConstantUtils
import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.ErrorMapper
import com.example.mobiledger.data.sources.auth.AuthSource
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.FireBaseResult
import com.example.mobiledger.domain.entities.BudgetTemplateCategoryEntity
import com.example.mobiledger.domain.entities.NewBudgetTemplateEntity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

interface BudgetTemplateApi {
    suspend fun addNewBudgetTemplate(
        uid: String, newBudgetTemplateEntity: NewBudgetTemplateEntity
    ): AppResult<Unit>

    suspend fun getBudgetTemplateList(uid: String): AppResult<List<NewBudgetTemplateEntity>>
    suspend fun getBudgetTemplateCategoryList(uid: String, id: String): AppResult<List<BudgetTemplateCategoryEntity>>
    suspend fun addBudgetTemplateCategory(
        uid: String,
        id: String,
        budgetTemplateCategoryEntity: BudgetTemplateCategoryEntity
    ): AppResult<Unit>

    suspend fun getBudgetTemplateSummary(uid: String, id: String): AppResult<NewBudgetTemplateEntity>
    suspend fun updateBudgetCategoryAmount(uid: String, id: String, category: String, value: Long): AppResult<Unit>
    suspend fun deleteCategoryFromBudgetTemplate(uid: String, id: String, category: String): AppResult<Unit>
    suspend fun deleteBudgetTemplateSummary(uid: String, id: String): AppResult<Unit>
    suspend fun updateBudgetTemplateMaxLimit(uid: String, id: String, value: Long): AppResult<Unit>
}

class BudgetTemplateApiImpl(private val firebaseDb: FirebaseFirestore, private val authSource: AuthSource) : BudgetTemplateApi {

    override suspend fun addNewBudgetTemplate(uid: String, newBudgetTemplateEntity: NewBudgetTemplateEntity): AppResult<Unit> {

        var response: Task<Void>? = null
        var exception: Exception? = null

        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                ConstantUtils.UNAUTHORIZED_ERROR_MSG
            )

            response = firebaseDb.collection(ConstantUtils.USERS)
                .document(uid)
                .collection(ConstantUtils.BUDGET_TEMPLATES)
                .document(newBudgetTemplateEntity.id)
                .set(newBudgetTemplateEntity)
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

    override suspend fun getBudgetTemplateList(uid: String): AppResult<List<NewBudgetTemplateEntity>> {
        var response: Task<QuerySnapshot>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                ConstantUtils.UNAUTHORIZED_ERROR_MSG
            )
            response = firebaseDb.collection(ConstantUtils.USERS)
                .document(uid)
                .collection(ConstantUtils.BUDGET_TEMPLATES)
                .get()

            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null && result.data.result != null) {
                    AppResult.Success(budgetTemplateEntityMapper(result.data.result!!))
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun getBudgetTemplateCategoryList(uid: String, id: String): AppResult<List<BudgetTemplateCategoryEntity>> {
        var response: Task<QuerySnapshot>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                ConstantUtils.UNAUTHORIZED_ERROR_MSG
            )
            response = firebaseDb.collection(ConstantUtils.USERS)
                .document(uid)
                .collection(ConstantUtils.BUDGET_TEMPLATES)
                .document(id)
                .collection(ConstantUtils.BUDGET_TEMPLATE_CATEGORY_LIST)
                .get()

            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null && result.data.result != null) {
                    AppResult.Success(budgetTemplateCategoryEntityMapper(result.data.result!!))
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun addBudgetTemplateCategory(
        uid: String,
        id: String,
        budgetTemplateCategoryEntity: BudgetTemplateCategoryEntity
    ): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null

        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                ConstantUtils.UNAUTHORIZED_ERROR_MSG
            )

            response = firebaseDb.collection(ConstantUtils.USERS)
                .document(uid)
                .collection(ConstantUtils.BUDGET_TEMPLATES)
                .document(id)
                .collection(ConstantUtils.BUDGET_TEMPLATE_CATEGORY_LIST)
                .document(budgetTemplateCategoryEntity.category)
                .set(budgetTemplateCategoryEntity)
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

    override suspend fun getBudgetTemplateSummary(uid: String, id: String): AppResult<NewBudgetTemplateEntity> {
        var response: Task<DocumentSnapshot>? = null
        var exception: Exception? = null
        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                ConstantUtils.UNAUTHORIZED_ERROR_MSG
            )
            response = firebaseDb.collection(ConstantUtils.USERS)
                .document(uid)
                .collection(ConstantUtils.BUDGET_TEMPLATES)
                .document(id)
                .get()

            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null && result.data.result != null) {
                    val monthlyResult = budgetTemplateSummaryEntityMapper(result.data.result as DocumentSnapshot)
                    if (monthlyResult != null) AppResult.Success(monthlyResult)
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

    override suspend fun updateBudgetCategoryAmount(uid: String, id: String, category: String, value: Long): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null

        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                ConstantUtils.UNAUTHORIZED_ERROR_MSG
            )

            response = firebaseDb.collection(ConstantUtils.USERS)
                .document(uid)
                .collection(ConstantUtils.BUDGET_TEMPLATES)
                .document(id)
                .collection(ConstantUtils.BUDGET_TEMPLATE_CATEGORY_LIST)
                .document(category)
                .update(
                    mapOf(
                        ConstantUtils.CATEGORY_BUDGET to FieldValue.increment(value)
                    )
                )

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

    override suspend fun deleteBudgetTemplateSummary(uid: String, id: String): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null

        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                ConstantUtils.UNAUTHORIZED_ERROR_MSG
            )

            response = firebaseDb.collection(ConstantUtils.USERS)
                .document(uid)
                .collection(ConstantUtils.BUDGET_TEMPLATES)
                .document(id)
                .delete()

            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null) AppResult.Success(Unit)
                else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun updateBudgetTemplateMaxLimit(uid: String, id: String, value: Long): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null

        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                ConstantUtils.UNAUTHORIZED_ERROR_MSG
            )

            response = firebaseDb.collection(ConstantUtils.USERS)
                .document(uid)
                .collection(ConstantUtils.BUDGET_TEMPLATES)
                .document(id)
                .update(ConstantUtils.MAX_BUDGET_LIMIT, value)

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

    override suspend fun deleteCategoryFromBudgetTemplate(uid: String, id: String, category: String): AppResult<Unit> {
        var response: Task<Void>? = null
        var exception: Exception? = null

        try {
            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
                ErrorCodes.FIREBASE_UNAUTHORIZED,
                ConstantUtils.UNAUTHORIZED_ERROR_MSG
            )

            response = firebaseDb.collection(ConstantUtils.USERS)
                .document(uid)
                .collection(ConstantUtils.BUDGET_TEMPLATES)
                .document(id)
                .collection(ConstantUtils.BUDGET_TEMPLATE_CATEGORY_LIST)
                .document(category)
                .delete()

            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null) AppResult.Success(Unit)
                else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }
}


private fun budgetTemplateSummaryEntityMapper(user: DocumentSnapshot): NewBudgetTemplateEntity? {
    return user.toObject(NewBudgetTemplateEntity::class.java)
}

private fun budgetTemplateEntityMapper(result: QuerySnapshot): List<NewBudgetTemplateEntity> {
    return result.map { it.toObject(NewBudgetTemplateEntity::class.java) }
}

private fun budgetTemplateCategoryEntityMapper(result: QuerySnapshot): List<BudgetTemplateCategoryEntity> {
    return result.map { it.toObject(BudgetTemplateCategoryEntity::class.java) }
}