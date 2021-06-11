package com.example.mobiledger.data.sources.api

import com.example.mobiledger.common.utils.ConstantUtils.BUDGET
import com.example.mobiledger.common.utils.ConstantUtils.BUDGET_DETAILS
import com.example.mobiledger.common.utils.ConstantUtils.CATEGORY_BUDGET
import com.example.mobiledger.common.utils.ConstantUtils.CATEGORY_EXPENSE
import com.example.mobiledger.common.utils.ConstantUtils.MAX_BUDGET
import com.example.mobiledger.common.utils.ConstantUtils.MONTH
import com.example.mobiledger.common.utils.ConstantUtils.TOTAL_BUDGET
import com.example.mobiledger.common.utils.ConstantUtils.UNAUTHORIZED_ERROR_MSG
import com.example.mobiledger.common.utils.ConstantUtils.USERS
import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.ErrorMapper
import com.example.mobiledger.data.sources.auth.AuthSource
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.FireBaseResult
import com.example.mobiledger.presentation.budget.MonthlyBudgetData
import com.example.mobiledger.presentation.budget.MonthlyCategoryBudget
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await

interface BudgetApi {
    suspend fun getCategoryBudgetListByMonth(uid: String, monthYear: String): AppResult<List<MonthlyCategoryBudget>>
    suspend fun getMonthlyBudgetOverView(uid: String, monthYear: String): AppResult<MonthlyBudgetData?>
    suspend fun setMonthlyBudget(uid: String, monthYear: String, monthlyBudgetData: MonthlyBudgetData): AppResult<Unit>
    suspend fun addCategoryBudget(uid: String, monthYear: String, monthlyCategoryBudget: MonthlyCategoryBudget): AppResult<Unit>
    suspend fun updateMonthlyBudgetData(uid: String, monthYear: String, monthlyLimitChange: Long, totalBudgetChange:Long): AppResult<Unit>
    suspend fun getMonthlyCategoryBudget(uid: String, monthYear: String, category: String): AppResult<MonthlyCategoryBudget>
    suspend fun updateMonthlyCategoryBudgetAmounts(
        uid: String,
        monthYear: String,
        category: String,
        budgetChange: Long,
        expenseChange: Long
    ): AppResult<Unit>

    suspend fun deleteBudgetCategory(uid: String, monthYear: String, category: String): AppResult<Unit>
}

class BudgetApiImpl(private val firebaseDb: FirebaseFirestore, private val authSource: AuthSource) : BudgetApi {

    override suspend fun getCategoryBudgetListByMonth(uid: String, monthYear: String): AppResult<List<MonthlyCategoryBudget>> {

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
                .collection(BUDGET)
                .document(BUDGET_DETAILS)
                .collection(CATEGORY_BUDGET)
                .get()

            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null && result.data.result != null) {
                    AppResult.Success(budgetCategoryListMapper(result.data.result!!))
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun getMonthlyBudgetOverView(
        uid: String,
        monthYear: String
    ): AppResult<MonthlyBudgetData?> {
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
                .collection(BUDGET)
                .document(BUDGET_DETAILS)
                .get()

            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null) {
                    AppResult.Success(budgetOverviewEntityMapper(result.data.result))
                } else {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun setMonthlyBudget(uid: String, monthYear: String, monthlyBudgetData: MonthlyBudgetData): AppResult<Unit> {
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
                .collection(BUDGET)
                .document(BUDGET_DETAILS)
                .set(monthlyBudgetData)

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

    override suspend fun addCategoryBudget(uid: String, monthYear: String, monthlyCategoryBudget: MonthlyCategoryBudget): AppResult<Unit> {
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
                .collection(BUDGET)
                .document(BUDGET_DETAILS)
                .collection(CATEGORY_BUDGET)
                .document(monthlyCategoryBudget.categoryName)
                .set(monthlyCategoryBudget)

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

    override suspend fun updateMonthlyBudgetData(uid: String, monthYear: String, monthlyLimitChange: Long, totalBudgetChange:Long): AppResult<Unit> {
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
                .collection(BUDGET)
                .document(BUDGET_DETAILS)
                .update(
                    mapOf(
                        MAX_BUDGET to FieldValue.increment(monthlyLimitChange),
                        TOTAL_BUDGET to FieldValue.increment(totalBudgetChange)
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

    override suspend fun getMonthlyCategoryBudget(uid: String, monthYear: String, category: String): AppResult<MonthlyCategoryBudget> {
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
                .collection(BUDGET)
                .document(BUDGET_DETAILS)
                .collection(CATEGORY_BUDGET)
                .document(category)
                .get()

            response.await()
        } catch (e: Exception) {
            exception = e
        }

        return when (val result = ErrorMapper.checkAndMapFirebaseApiError(response, exception)) {
            is FireBaseResult.Success -> {
                if (result.data != null && result.data.result != null) {
                    val monthlyResult = monthlyCategoryBudgetMapper(result = result.data.result!!)
                    if (monthlyResult != null) AppResult.Success(monthlyResult)
                    else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                } else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
            is FireBaseResult.Failure -> {
                AppResult.Failure(result.error)
            }
        }
    }

    override suspend fun updateMonthlyCategoryBudgetAmounts(
        uid: String,
        monthYear: String,
        category: String,
        budgetChange: Long,
        expenseChange: Long
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
                .collection(BUDGET)
                .document(BUDGET_DETAILS)
                .collection(CATEGORY_BUDGET)
                .document(category)
                .update(
                    mapOf(
                        CATEGORY_BUDGET to FieldValue.increment(budgetChange),
                        CATEGORY_EXPENSE to FieldValue.increment(expenseChange)
                    )
                )

            response.await()
        } catch (e: Exception) {
            exception = e
            if ((exception as FirebaseFirestoreException).code == FirebaseFirestoreException.Code.NOT_FOUND)
                return AppResult.Success(Unit)
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

    override suspend fun deleteBudgetCategory(uid: String, monthYear: String, category: String): AppResult<Unit> {
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
                .collection(BUDGET)
                .document(BUDGET_DETAILS)
                .collection(CATEGORY_BUDGET)
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


private fun budgetCategoryListMapper(result: QuerySnapshot): List<MonthlyCategoryBudget> {
    return result.map { it.toObject(MonthlyCategoryBudget::class.java) }
}

private fun budgetOverviewEntityMapper(result: DocumentSnapshot?): MonthlyBudgetData? {
    return result?.toObject(MonthlyBudgetData::class.java)
}

private fun monthlyCategoryBudgetMapper(result: DocumentSnapshot): MonthlyCategoryBudget? {
    return result.toObject(MonthlyCategoryBudget::class.java)
}