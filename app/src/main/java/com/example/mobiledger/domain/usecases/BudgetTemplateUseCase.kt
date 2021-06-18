package com.example.mobiledger.domain.usecases

import com.example.mobiledger.data.repository.BudgetTemplateRepository
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.BudgetTemplateCategoryEntity
import com.example.mobiledger.domain.entities.NewBudgetTemplateEntity

interface BudgetTemplateUseCase {
    suspend fun addNewBudgetTemplate(
        newBudgetTemplateEntity: NewBudgetTemplateEntity
    ): AppResult<Unit>

    suspend fun getBudgetTemplateList(): AppResult<List<NewBudgetTemplateEntity>>
    suspend fun getBudgetTemplateCategoryList(id: String): AppResult<List<BudgetTemplateCategoryEntity>>
    suspend fun addBudgetTemplateCategory(id: String, budgetTemplateCategoryEntity: BudgetTemplateCategoryEntity): AppResult<Unit>
    suspend fun getBudgetTemplateSummary(id: String): AppResult<NewBudgetTemplateEntity>
}


class BudgetTemplateUseCaseImpl(private val budgetTemplateRepository: BudgetTemplateRepository) : BudgetTemplateUseCase {

    override suspend fun addNewBudgetTemplate(newBudgetTemplateEntity: NewBudgetTemplateEntity): AppResult<Unit> {
        return budgetTemplateRepository.addNewBudgetTemplate(newBudgetTemplateEntity)
    }

    override suspend fun getBudgetTemplateList(): AppResult<List<NewBudgetTemplateEntity>> =
        budgetTemplateRepository.getBudgetTemplateList()

    override suspend fun getBudgetTemplateCategoryList(id: String): AppResult<List<BudgetTemplateCategoryEntity>> =
        budgetTemplateRepository.getBudgetTemplateCategoryList(id)

    override suspend fun addBudgetTemplateCategory(
        id: String,
        budgetTemplateCategoryEntity: BudgetTemplateCategoryEntity
    ): AppResult<Unit> = budgetTemplateRepository.addBudgetTemplateCategory(id, budgetTemplateCategoryEntity)

    override suspend fun getBudgetTemplateSummary(id: String): AppResult<NewBudgetTemplateEntity> =
        budgetTemplateRepository.getBudgetTemplateSummary(id)
}