package com.example.mobiledger.data.repository

import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.sources.api.BudgetApi
import com.example.mobiledger.data.sources.cache.CacheSource
import com.example.mobiledger.data.sources.room.categories.CategoriesDb
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.presentation.budget.MonthlyBudgetData
import com.example.mobiledger.presentation.budget.MonthlyCategoryBudget
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface BudgetRepository {

    suspend fun getMonthlyBudgetOverView(monthYear: String): AppResult<MonthlyBudgetData?>
    suspend fun getCategoryBudgetListByMonth(monthYear: String): AppResult<List<MonthlyCategoryBudget>>
    suspend fun setMonthlyBudget(monthYear: String, monthlyBudgetData: MonthlyBudgetData): AppResult<Unit>
    suspend fun addCategoryBudget(monthYear: String, monthlyCategoryBudget: MonthlyCategoryBudget): AppResult<Unit>
    suspend fun updateBudgetTotal(monthYear: String, totalBudgetData: Long): AppResult<Unit>
}

class BudgetRepositoryImpl(
    private val budgetApi: BudgetApi,
    private val cacheSource: CacheSource,
    private val categoryDb: CategoriesDb,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BudgetRepository {

    override suspend fun getMonthlyBudgetOverView(monthYear: String): AppResult<MonthlyBudgetData?> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                budgetApi.getMonthlyBudgetOverView(uId, monthYear)
            } else
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun getCategoryBudgetListByMonth(monthYear: String): AppResult<List<MonthlyCategoryBudget>> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                budgetApi.getCategoryBudgetListByMonth(uId, monthYear)
            } else
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun setMonthlyBudget(monthYear: String, monthlyBudgetData: MonthlyBudgetData): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                budgetApi.setMonthlyBudget(uId, monthYear, monthlyBudgetData)
            } else
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun addCategoryBudget(monthYear: String, monthlyCategoryBudget: MonthlyCategoryBudget): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                budgetApi.addCategoryBudget(uId, monthYear, monthlyCategoryBudget)
            } else
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun updateBudgetTotal(monthYear: String, totalBudgetData: Long): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                budgetApi.updateBudgetTotal(uId, monthYear, totalBudgetData)
            } else
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }
}
