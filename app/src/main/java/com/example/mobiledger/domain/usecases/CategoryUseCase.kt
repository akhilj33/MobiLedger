package com.example.mobiledger.domain.usecases

import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.repository.CategoryRepository
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.ExpenseCategoryListEntity
import com.example.mobiledger.domain.entities.IncomeCategoryListEntity
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.presentation.budget.MonthlyCategorySummary
import kotlinx.coroutines.*

interface CategoryUseCase {
    suspend fun addUserIncomeCategories(defaultCategoryList: List<String>): AppResult<Unit>
    suspend fun addUserExpenseCategories(defaultCategoryList: List<String>): AppResult<Unit>
    suspend fun getUserIncomeCategories(): AppResult<IncomeCategoryListEntity>
    suspend fun getUserExpenseCategories(): AppResult<ExpenseCategoryListEntity>
    suspend fun updateUserIncomeCategory(newIncomeCategory: IncomeCategoryListEntity): AppResult<Unit>
    suspend fun updateUserExpenseCategory(newExpenseCategory: ExpenseCategoryListEntity): AppResult<Unit>
    suspend fun addCategoryTransaction(monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit>
    suspend fun getMonthlyCategorySummary(monthYear: String, category: String): AppResult<MonthlyCategorySummary>
    suspend fun getAllMonthlyCategories(monthYear: String): AppResult<List<MonthlyCategorySummary>>
}

class CategoryUseCaseImpl(private val categoryRepository: CategoryRepository) : CategoryUseCase {

    override suspend fun addUserIncomeCategories(defaultCategoryList: List<String>): AppResult<Unit> =
        categoryRepository.addUserIncomeCategoryDb(defaultCategoryList)

    override suspend fun addUserExpenseCategories(defaultCategoryList: List<String>): AppResult<Unit> =
        categoryRepository.addUserExpenseCategoryDb(defaultCategoryList)

    override suspend fun getUserIncomeCategories(): AppResult<IncomeCategoryListEntity> =
        categoryRepository.getUserIncomeCategories()

    override suspend fun getUserExpenseCategories(): AppResult<ExpenseCategoryListEntity> =
        categoryRepository.getUserExpenseCategories()

    override suspend fun updateUserIncomeCategory(newIncomeCategory: IncomeCategoryListEntity): AppResult<Unit> =
        categoryRepository.updateUserIncomeCategory(newIncomeCategory)

    override suspend fun updateUserExpenseCategory(newExpenseCategory: ExpenseCategoryListEntity): AppResult<Unit> =
        categoryRepository.updateUserExpenseCategory(newExpenseCategory)

    override suspend fun addCategoryTransaction(monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit> {
        return categoryRepository.addCategoryTransaction(monthYear, transactionEntity)
    }

    override suspend fun getMonthlyCategorySummary(monthYear: String, category: String): AppResult<MonthlyCategorySummary> {
        return categoryRepository.getMonthlyCategorySummary(monthYear, category)
    }

    override suspend fun getAllMonthlyCategories(monthYear: String): AppResult<List<MonthlyCategorySummary>> {
        return categoryRepository.getAllMonthlyCategories(monthYear)
    }
}