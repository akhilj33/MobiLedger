package com.example.mobiledger.domain.usecases

import com.example.mobiledger.data.repository.CategoryRepository
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.ExpenseCategoryListEntity
import com.example.mobiledger.domain.entities.IncomeCategoryListEntity

interface CategoryUseCase {
    suspend fun addDefaultIncomeCategories(defaultCategoryList: List<String>): AppResult<Unit>
    suspend fun addDefaultExpenseCategories(defaultCategoryList: List<String>): AppResult<Unit>
    suspend fun getDefaultIncomeCategories(): AppResult<IncomeCategoryListEntity>
    suspend fun getDefaultExpenseCategories(): AppResult<ExpenseCategoryListEntity>
    suspend fun getUserIncomeCategories(): AppResult<IncomeCategoryListEntity>
    suspend fun getUserExpenseCategories(): AppResult<ExpenseCategoryListEntity>
}

class CategoryUseCaseImpl(private val categoryRepository: CategoryRepository) : CategoryUseCase {

    override suspend fun addDefaultIncomeCategories(defaultCategoryList: List<String>): AppResult<Unit> =
        categoryRepository.addUserIncomeCategoryDb(defaultCategoryList)

    override suspend fun addDefaultExpenseCategories(defaultCategoryList: List<String>): AppResult<Unit> =
        categoryRepository.addUserExpenseCategoryDb(defaultCategoryList)

    override suspend fun getDefaultIncomeCategories(): AppResult<IncomeCategoryListEntity> = categoryRepository.getDefaultIncomeCategories()

    override suspend fun getDefaultExpenseCategories(): AppResult<ExpenseCategoryListEntity> =
        categoryRepository.getDefaultExpenseCategories()

    override suspend fun getUserIncomeCategories(): AppResult<IncomeCategoryListEntity> = categoryRepository.getUserIncomeCategories()

    override suspend fun getUserExpenseCategories(): AppResult<ExpenseCategoryListEntity> = categoryRepository.getUserExpenseCategories()
}