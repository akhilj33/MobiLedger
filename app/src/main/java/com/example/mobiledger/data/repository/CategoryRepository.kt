package com.example.mobiledger.data.repository

import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.sources.api.CategoryApi
import com.example.mobiledger.data.sources.cache.CacheSource
import com.example.mobiledger.data.sources.room.categories.CategoriesDb
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.CategoryListEntity
import com.example.mobiledger.domain.entities.ExpenseCategoryListEntity
import com.example.mobiledger.domain.entities.IncomeCategoryListEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

interface CategoryRepository {
    suspend fun addUserIncomeCategoryDb(defaultCategoryList: List<String>): AppResult<Unit>
    suspend fun addUserExpenseCategoryDb(defaultCategoryList: List<String>): AppResult<Unit>
    suspend fun getDefaultIncomeCategories(): AppResult<IncomeCategoryListEntity>
    suspend fun getDefaultExpenseCategories(): AppResult<ExpenseCategoryListEntity>
    suspend fun getUserIncomeCategories(): AppResult<IncomeCategoryListEntity>
    suspend fun getUserExpenseCategories(): AppResult<ExpenseCategoryListEntity>
}

class CategoryRepositoryImpl(
    private val categoryApi: CategoryApi,
    private val cacheSource: CacheSource,
    private val categoryDb: CategoriesDb,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    CategoryRepository {

    override suspend fun addUserIncomeCategoryDb(defaultCategoryList: List<String>): AppResult<Unit> {
        return withContext(dispatcher) {
            val uid = cacheSource.getUID()
            if (uid != null) {
                categoryApi.addDefaultIncomeCategories(uid, defaultCategoryList)
            } else
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun addUserExpenseCategoryDb(defaultCategoryList: List<String>): AppResult<Unit> {
        return withContext(dispatcher) {
            val uid = cacheSource.getUID()
            if (uid != null) {
                categoryApi.addDefaultExpenseCategories(uid, defaultCategoryList)
            } else
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun getDefaultIncomeCategories(): AppResult<IncomeCategoryListEntity> {
        return withContext(dispatcher) {
            categoryApi.getDefaultIncomeCategories()
        }
    }

    override suspend fun getDefaultExpenseCategories(): AppResult<ExpenseCategoryListEntity> {
        return withContext(dispatcher) {
            categoryApi.getDefaultExpenseCategories()
        }
    }

    private suspend fun addUserCategory(uid: String) {
        return withContext(dispatcher) {
            var incomeCategoryListEntity = IncomeCategoryListEntity(emptyList())
            var expenseCategoryListEntity = ExpenseCategoryListEntity(emptyList())
            when (val firebaseResult = categoryApi.getUserIncomeCategories(uid)) {
                is AppResult.Success -> {
                    incomeCategoryListEntity = firebaseResult.data
                }
                is AppResult.Failure -> {
                    Timber.i(firebaseResult.error.message.toString())
                }
            }
            when (val firebaseResult = categoryApi.getUserExpenseCategories(uid)) {
                is AppResult.Success -> {
                    expenseCategoryListEntity = firebaseResult.data
                }
                is AppResult.Failure -> {
                    Timber.i(firebaseResult.error.message.toString())
                }
            }
            if (incomeCategoryListEntity.incomeCategoryList.isNotEmpty() && expenseCategoryListEntity.expenseCategoryList.isNotEmpty()) {
                val categoryList = CategoryListEntity(uid, incomeCategoryListEntity, expenseCategoryListEntity)
                categoryDb.saveUser(categoryList)
            }
        }
    }

    override suspend fun getUserIncomeCategories(): AppResult<IncomeCategoryListEntity> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                val categoryExist = categoryDb.hasCategory()
                if (!categoryExist) {
                    addUserCategory(uId)
                }
                categoryDb.fetchUserIncomeCategories()
            } else {
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
        }
    }

    override suspend fun getUserExpenseCategories(): AppResult<ExpenseCategoryListEntity> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                val categoryExist = categoryDb.hasCategory()
                if (!categoryExist) {
                    addUserCategory(uId)
                }
                categoryDb.fetchUserExpenseCategories()
            } else {
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
        }
    }
}