package com.example.mobiledger.data.sources.room.categories

import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.CategoryListEntity
import com.example.mobiledger.domain.entities.ExpenseCategoryListEntity
import com.example.mobiledger.domain.entities.IncomeCategoryListEntity

interface CategoriesDb {
    suspend fun fetchUserIncomeCategories(): AppResult<IncomeCategoryListEntity>
    suspend fun fetchUserExpenseCategories(): AppResult<ExpenseCategoryListEntity>
    suspend fun updateIncomeCategoryList(list: IncomeCategoryListEntity)
    suspend fun updateExpenseCategoryList(list: ExpenseCategoryListEntity)
    suspend fun hasCategory(): Boolean
    suspend fun saveUser(categoryList: CategoryListEntity)
}

class CategoryDbImpl(private val dao: CategoryDao) : CategoriesDb {

    override suspend fun fetchUserIncomeCategories(): AppResult<IncomeCategoryListEntity> {
        val userIncomeCategoryList = dao.fetchUserIncomeCategoryList()
        return if (userIncomeCategoryList != null) AppResult.Success(userIncomeCategoryList)
        else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
    }

    override suspend fun fetchUserExpenseCategories(): AppResult<ExpenseCategoryListEntity> {
        val userExpenseCategoryList = dao.fetchUserExpenseCategoryList()
        return if (userExpenseCategoryList != null) AppResult.Success(userExpenseCategoryList)
        else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
    }

    override suspend fun updateIncomeCategoryList(list: IncomeCategoryListEntity) {
        dao.updateIncomeCategoryList(list)
    }

    override suspend fun updateExpenseCategoryList(list: ExpenseCategoryListEntity) {
        dao.updateExpenseCategoryList(list)
    }

    override suspend fun hasCategory(): Boolean {
        val userCount = dao.hasCategory()
        return !(userCount == null || userCount == 0)
    }

    override suspend fun saveUser(categoryList: CategoryListEntity) {
        val categoryRoomItem = mapToCategoryRoomEntity(categoryList)
        dao.saveCategory(categoryRoomItem)
    }
}


private fun mapToCategoryRoomEntity(categoryListEntity: CategoryListEntity): CategoryRoomItem {
    categoryListEntity.apply {
        return CategoryRoomItem(uid, expenseCategoryList, incomeCategoryList)
    }
}