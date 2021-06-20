package com.example.mobiledger.data.repository

import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.sources.api.BudgetTemplateApi
import com.example.mobiledger.data.sources.cache.CacheSource
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.BudgetTemplateCategoryEntity
import com.example.mobiledger.domain.entities.NewBudgetTemplateEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface BudgetTemplateRepository {
    suspend fun addNewBudgetTemplate(
        newBudgetTemplateEntity: NewBudgetTemplateEntity
    ): AppResult<Unit>

    suspend fun getBudgetTemplateList(): AppResult<List<NewBudgetTemplateEntity>>
    suspend fun getBudgetTemplateCategoryList(id: String): AppResult<List<BudgetTemplateCategoryEntity>>
    suspend fun addBudgetTemplateCategory(id: String, budgetTemplateCategoryEntity: BudgetTemplateCategoryEntity): AppResult<Unit>
    suspend fun getBudgetTemplateSummary(id: String): AppResult<NewBudgetTemplateEntity>
    suspend fun updateBudgetCategoryAmount(id: String, category: String, value: Long): AppResult<Unit>
    suspend fun deleteCategoryFromBudgetTemplate(id: String, category: String): AppResult<Unit>
    suspend fun deleteBudgetTemplate(id: String): AppResult<Unit>
    suspend fun updateBudgetTemplateMaxLimit(id: String, value: Long): AppResult<Unit>
}

class BudgetTemplateRepositoryImpl(
    private val budgetTemplateApi: BudgetTemplateApi, private val cacheSource: CacheSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BudgetTemplateRepository {

    override suspend fun addNewBudgetTemplate(newBudgetTemplateEntity: NewBudgetTemplateEntity): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) budgetTemplateApi.addNewBudgetTemplate(uId, newBudgetTemplateEntity)
            else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun getBudgetTemplateList(): AppResult<List<NewBudgetTemplateEntity>> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                budgetTemplateApi.getBudgetTemplateList(uId)
            } else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun getBudgetTemplateCategoryList(id: String): AppResult<List<BudgetTemplateCategoryEntity>> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                budgetTemplateApi.getBudgetTemplateCategoryList(uId, id)
            } else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun addBudgetTemplateCategory(
        id: String,
        budgetTemplateCategoryEntity: BudgetTemplateCategoryEntity
    ): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                budgetTemplateApi.addBudgetTemplateCategory(uId, id, budgetTemplateCategoryEntity)
            } else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun getBudgetTemplateSummary(id: String): AppResult<NewBudgetTemplateEntity> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                budgetTemplateApi.getBudgetTemplateSummary(uId, id)
            } else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun updateBudgetCategoryAmount(id: String, category: String, value: Long): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                budgetTemplateApi.updateBudgetCategoryAmount(uId, id, category, value)
            } else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun deleteCategoryFromBudgetTemplate(id: String, category: String): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                budgetTemplateApi.deleteCategoryFromBudgetTemplate(uId, id, category)
            } else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun deleteBudgetTemplate(id: String): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                budgetTemplateApi.deleteBudgetTemplate(uId, id)
            } else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun updateBudgetTemplateMaxLimit(id: String, value: Long): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                budgetTemplateApi.updateBudgetTemplateMaxLimit(uId, id, value)
            } else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }
}