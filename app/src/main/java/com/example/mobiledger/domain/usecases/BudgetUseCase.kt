package com.example.mobiledger.domain.usecases

import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.repository.BudgetRepository
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.presentation.budget.MonthlyBudgetData
import com.example.mobiledger.presentation.budget.MonthlyCategoryBudget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

interface BudgetUseCase {
    suspend fun getMonthlyBudgetOverView(monthYear: String): AppResult<MonthlyBudgetData?>
    suspend fun getCategoryBudgetListByMonth(monthYear: String): AppResult<List<MonthlyCategoryBudget>>
    suspend fun setMonthlyBudget(monthYear: String, monthlyBudgetData: MonthlyBudgetData): AppResult<Unit>
    suspend fun addCategoryBudget(monthYear: String, monthlyCategoryBudget: MonthlyCategoryBudget): AppResult<Unit>
    suspend fun updateBudgetTotal(monthYear: String, totalBudgetData: Long): AppResult<Unit>
    suspend fun getMonthlyCategoryBudget(monthYear: String, category: String): AppResult<MonthlyCategoryBudget>
    suspend fun updateMonthlyCategoryBudgetAmounts(monthYear: String, category: String, budgetChange: Long = 0L, expenseChange: Long = 0L): AppResult<Unit>
    suspend fun updateMonthlyCategoryBudgetOnCategoryChanged(monthYear: String, oldCategory: String, newCategory: String,
                                                             oldAmount: Long, newAmount: Long): AppResult<Unit>

}

class BudgetUseCaseImpl(private val budgetRepository: BudgetRepository) : BudgetUseCase {
    override suspend fun getMonthlyBudgetOverView(monthYear: String): AppResult<MonthlyBudgetData?> =
        budgetRepository.getMonthlyBudgetOverView(monthYear)

    override suspend fun getCategoryBudgetListByMonth(monthYear: String): AppResult<List<MonthlyCategoryBudget>> =
        budgetRepository.getCategoryBudgetListByMonth(monthYear)

    override suspend fun setMonthlyBudget(monthYear: String, monthlyBudgetData: MonthlyBudgetData): AppResult<Unit> =
        budgetRepository.setMonthlyBudget(monthYear, monthlyBudgetData)

    override suspend fun addCategoryBudget(monthYear: String, monthlyCategoryBudget: MonthlyCategoryBudget): AppResult<Unit> =
        budgetRepository.addCategoryBudget(monthYear, monthlyCategoryBudget)

    override suspend fun updateBudgetTotal(monthYear: String, totalBudgetData: Long): AppResult<Unit> =
        budgetRepository.updateBudgetTotal(monthYear, totalBudgetData)

    override suspend fun getMonthlyCategoryBudget(monthYear: String, category: String): AppResult<MonthlyCategoryBudget> {
        return budgetRepository.getMonthlyCategoryBudget(monthYear, category)
    }

    override suspend fun updateMonthlyCategoryBudgetAmounts(
        monthYear: String,
        category: String,
        budgetChange: Long,
        expenseChange: Long
    ): AppResult<Unit> {
        return budgetRepository.updateMonthlyCategoryBudgetAmounts(monthYear, category, budgetChange,expenseChange)
    }

    override suspend fun updateMonthlyCategoryBudgetOnCategoryChanged(
        monthYear: String,
        oldCategory: String,
        newCategory: String,
        oldAmount: Long,
        newAmount: Long
    ): AppResult<Unit> {
       return withContext(Dispatchers.IO){
            val updateOldBudgetJob = async { updateMonthlyCategoryBudgetAmounts(monthYear, oldCategory, expenseChange = -oldAmount) }
            val updateNewBudgetJob = async { updateMonthlyCategoryBudgetAmounts(monthYear, newCategory, expenseChange = newAmount) }

            if (updateOldBudgetJob.await() is AppResult.Success && updateNewBudgetJob.await() is AppResult.Success)
                AppResult.Success(Unit)
            else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))


        }
    }

}