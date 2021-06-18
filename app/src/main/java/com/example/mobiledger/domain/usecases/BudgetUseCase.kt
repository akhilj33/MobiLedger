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
    suspend fun updateMonthlyBudgetSummary(monthYear: String, monthlyLimitChange: Long = 0L, totalBudgetChange:Long=0L): AppResult<Unit>
    suspend fun getMonthlyCategoryBudget(monthYear: String, category: String): AppResult<MonthlyCategoryBudget>
    suspend fun updateMonthlyCategoryBudgetData(monthYear: String, category: String, budgetChange: Long = 0L, expenseChange: Long = 0L): AppResult<Unit>
    suspend fun updateMonthlyCategoryBudgetAmounts(monthYear: String, category: String, budgetChange: Long = 0L, expenseChange: Long = 0L): AppResult<Unit>
    suspend fun updateMonthlyCategoryBudgetOnCategoryChanged(monthYear: String, oldCategory: String, newCategory: String,
                                                             oldAmount: Long, newAmount: Long): AppResult<Unit>
    suspend fun deleteBudgetCategoryAndUpdateSummary(monthYear: String, category: String, budgetChange: Long): AppResult<Unit>
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

    override suspend fun updateMonthlyBudgetSummary(monthYear: String, monthlyLimitChange: Long, totalBudgetChange:Long): AppResult<Unit> =
        budgetRepository.updateMonthlyBudgetData(monthYear, monthlyLimitChange, totalBudgetChange)

    override suspend fun getMonthlyCategoryBudget(monthYear: String, category: String): AppResult<MonthlyCategoryBudget> {
        return budgetRepository.getMonthlyCategoryBudget(monthYear, category)
    }

    override suspend fun updateMonthlyCategoryBudgetData(
        monthYear: String,
        category: String,
        budgetChange: Long,
        expenseChange: Long
    ): AppResult<Unit> {
       return withContext(Dispatchers.IO){
            val updateMonthlyBudgetSummaryJob = async { updateMonthlyBudgetSummary(monthYear, totalBudgetChange = budgetChange) }
            val updateCategoryAmountJob = async { budgetRepository.updateMonthlyCategoryBudgetAmounts(monthYear, category, budgetChange, expenseChange) }

            if (updateCategoryAmountJob.await() is AppResult.Success && updateMonthlyBudgetSummaryJob.await() is AppResult.Success)
                AppResult.Success(Unit)
            else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun updateMonthlyCategoryBudgetAmounts(
        monthYear: String,
        category: String,
        budgetChange: Long,
        expenseChange: Long
    ): AppResult<Unit> {
        return withContext(Dispatchers.IO){
            if (budgetChange == 0L)
                budgetRepository.updateMonthlyCategoryBudgetAmounts(monthYear, category, budgetChange,expenseChange)
            else{
                when(val result = getMonthlyCategoryBudget(monthYear, category)){
                    is AppResult.Success -> {
                        val newBudget = result.data.categoryBudget + budgetChange
                        if (newBudget > 0L){
                            updateMonthlyCategoryBudgetData(monthYear, category, budgetChange,expenseChange)
                        }
                        else deleteBudgetCategoryAndUpdateSummary(monthYear, category, budgetChange)
                    }
                    is AppResult.Failure -> AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
        }
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

    override suspend fun deleteBudgetCategoryAndUpdateSummary(monthYear: String, category: String, budgetChange: Long): AppResult<Unit> {
        return withContext(Dispatchers.IO){
            val updateMonthlyBudgetSummaryJob = async { updateMonthlyBudgetSummary(monthYear, totalBudgetChange = budgetChange) }
            val deleteCategoryJob = async { budgetRepository.deleteBudgetCategory(monthYear, category) }

            if (deleteCategoryJob.await() is AppResult.Success && updateMonthlyBudgetSummaryJob.await() is AppResult.Success)
                AppResult.Success(Unit)
            else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

}