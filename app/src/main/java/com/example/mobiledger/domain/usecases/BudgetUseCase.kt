package com.example.mobiledger.domain.usecases

import com.example.mobiledger.data.repository.BudgetRepository
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.presentation.budget.MonthlyBudgetData
import com.example.mobiledger.presentation.budget.MonthlyCategoryBudget

interface BudgetUseCase {
    suspend fun getMonthlyBudgetOverView(monthYear: String): AppResult<MonthlyBudgetData?>
    suspend fun getCategoryBudgetListByMonth(monthYear: String): AppResult<List<MonthlyCategoryBudget>>
    suspend fun setMonthlyBudget(monthYear: String, monthlyBudgetData: MonthlyBudgetData): AppResult<Unit>
    suspend fun addCategoryBudget(monthYear: String, monthlyCategoryBudget: MonthlyCategoryBudget): AppResult<Unit>
    suspend fun updateBudgetTotal(monthYear: String, totalBudgetData: Long): AppResult<Unit>
    suspend fun getMonthlyCategoryBudget(monthYear: String, category: String): AppResult<MonthlyCategoryBudget>

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

}