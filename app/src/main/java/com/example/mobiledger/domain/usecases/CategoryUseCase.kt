package com.example.mobiledger.domain.usecases

import com.example.mobiledger.data.repository.CategoryRepository
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.ExpenseCategoryListEntity
import com.example.mobiledger.domain.entities.IncomeCategoryListEntity

interface CategoryUseCase {
    suspend fun addUserIncomeCategories(defaultCategoryList: List<String>): AppResult<Unit>
    suspend fun addUserExpenseCategories(defaultCategoryList: List<String>): AppResult<Unit>
    suspend fun getUserIncomeCategories(): AppResult<IncomeCategoryListEntity>
    suspend fun getUserExpenseCategories(): AppResult<ExpenseCategoryListEntity>
    suspend fun updateUserIncomeCategory(newIncomeCategory: IncomeCategoryListEntity): AppResult<Unit>
    suspend fun updateUserIncomeCategoryDB(newIncomeCategory: IncomeCategoryListEntity)
    suspend fun updateUserExpenseCategory(newExpenseCategory: ExpenseCategoryListEntity): AppResult<Unit>
    suspend fun updateUserExpenseCategoryDB(newExpenseCategory: ExpenseCategoryListEntity)
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

    override suspend fun updateUserIncomeCategoryDB(newIncomeCategory: IncomeCategoryListEntity) =
        categoryRepository.updateUserIncomeCategoryDB(newIncomeCategory)

    override suspend fun updateUserExpenseCategory(newExpenseCategory: ExpenseCategoryListEntity): AppResult<Unit> =
        categoryRepository.updateUserExpenseCategory(newExpenseCategory)

    override suspend fun updateUserExpenseCategoryDB(newExpenseCategory: ExpenseCategoryListEntity) =
        categoryRepository.updateUserExpenseCategoryDB(newExpenseCategory)
}