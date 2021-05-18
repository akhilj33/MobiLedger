package com.example.mobiledger.data.sources.api

import com.example.mobiledger.common.utils.ConstantUtils
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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

interface BudgetApi {
    suspend fun getCategoryBudgetListByMonth(uid: String, monthYear: String): AppResult<List<MonthlyCategoryBudget>>
    suspend fun getMonthlyBudgetOverView(uid: String, monthYear: String): AppResult<MonthlyBudgetData?>
    suspend fun setMonthlyBudget(uid: String, monthYear: String, monthlyBudgetData: MonthlyBudgetData): AppResult<Unit>
}

class BudgetApiImpl(private val firebaseDb: FirebaseFirestore, private val authSource: AuthSource) : BudgetApi {

    override suspend fun getCategoryBudgetListByMonth(uid: String, monthYear: String): AppResult<List<MonthlyCategoryBudget>> {

        var response: Task<QuerySnapshot>? = null
        var exception: Exception? = null
        try {
//            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
//                ErrorCodes.FIREBASE_UNAUTHORIZED,
//                ConstantUtils.UNAUTHORIZED_ERROR_MSG
//            )
            response = firebaseDb.collection(ConstantUtils.USERS)
                .document(uid)
                .collection(ConstantUtils.MONTH)
                .document(monthYear)
                .collection(ConstantUtils.BUDGET)
                .document(ConstantUtils.BUDGET_DETAILS)
                .collection(ConstantUtils.CATEGORY_BUDGET)
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
                ConstantUtils.UNAUTHORIZED_ERROR_MSG
            )
            response = firebaseDb.collection(ConstantUtils.USERS)
                .document(uid)
                .collection(ConstantUtils.MONTH)
                .document(monthYear)
                .collection(ConstantUtils.BUDGET)
                .document(ConstantUtils.BUDGET_DETAILS)
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
//            if (!authSource.isUserAuthorized()) throw FirebaseAuthException(
//                ErrorCodes.FIREBASE_UNAUTHORIZED,
//                ConstantUtils.UNAUTHORIZED_ERROR_MSG
//            )

            response = firebaseDb.collection(ConstantUtils.USERS)
                .document(uid)
                .collection(ConstantUtils.MONTH)
                .document(monthYear)
                .collection(ConstantUtils.BUDGET)
                .document(ConstantUtils.BUDGET_DETAILS)
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
}


private fun budgetCategoryListMapper(result: QuerySnapshot): List<MonthlyCategoryBudget> {
    return result.map { it.toObject(MonthlyCategoryBudget::class.java) }
}

private fun budgetOverviewEntityMapper(result: DocumentSnapshot?): MonthlyBudgetData? {
    return result?.toObject(MonthlyBudgetData::class.java)
}