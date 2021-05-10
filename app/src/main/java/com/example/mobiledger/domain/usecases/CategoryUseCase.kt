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
}

class CategoryUseCaseImpl(private val categoryRepository: CategoryRepository) : CategoryUseCase {

    override suspend fun addUserIncomeCategories(defaultCategoryList: List<String>): AppResult<Unit> =
        categoryRepository.addUserIncomeCategoryDb(defaultCategoryList)

    override suspend fun addUserExpenseCategories(defaultCategoryList: List<String>): AppResult<Unit> =
        categoryRepository.addUserExpenseCategoryDb(defaultCategoryList)

    override suspend fun getUserIncomeCategories(): AppResult<IncomeCategoryListEntity> = categoryRepository.getUserIncomeCategories()

    override suspend fun getUserExpenseCategories(): AppResult<ExpenseCategoryListEntity> = categoryRepository.getUserExpenseCategories()
}