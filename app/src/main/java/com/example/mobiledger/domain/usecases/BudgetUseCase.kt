package com.example.mobiledger.domain.usecases

import com.example.mobiledger.data.repository.BudgetRepository
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.presentation.budget.MonthlyBudgetData
import com.example.mobiledger.presentation.budget.MonthlyCategoryBudget

interface BudgetUseCase {
    suspend fun getMonthlyBudgetOverView(monthYear: String): AppResult<MonthlyBudgetData?>
    suspend fun getCategoryBudgetListByMonth(monthYear: String): AppResult<List<MonthlyCategoryBudget>>
    suspend fun setMonthlyBudget(monthYear: String, monthlyBudgetData: MonthlyBudgetData): AppResult<Unit>
}

class BudgetUseCaseImpl(private val budgetRepository: BudgetRepository) : BudgetUseCase {
    override suspend fun getMonthlyBudgetOverView(monthYear: String): AppResult<MonthlyBudgetData?> =
        budgetRepository.getMonthlyBudgetOverView(monthYear)

    override suspend fun getCategoryBudgetListByMonth(monthYear: String): AppResult<List<MonthlyCategoryBudget>> =
        budgetRepository.getCategoryBudgetListByMonth(monthYear)

    override suspend fun setMonthlyBudget(monthYear: String, monthlyBudgetData: MonthlyBudgetData): AppResult<Unit> =
        budgetRepository.setMonthlyBudget(monthYear, monthlyBudgetData)
}