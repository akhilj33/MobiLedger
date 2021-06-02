package com.example.mobiledger.domain.usecases

import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.repository.CategoryRepository
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.DocumentReferenceEntity
import com.example.mobiledger.domain.entities.ExpenseCategoryListEntity
import com.example.mobiledger.domain.entities.IncomeCategoryListEntity
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.presentation.budget.MonthlyCategorySummary
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

interface CategoryUseCase {
    suspend fun addUserIncomeCategories(defaultCategoryList: List<String>): AppResult<Unit>
    suspend fun addUserExpenseCategories(defaultCategoryList: List<String>): AppResult<Unit>
    suspend fun getUserIncomeCategories(): AppResult<IncomeCategoryListEntity>
    suspend fun getUserExpenseCategories(): AppResult<ExpenseCategoryListEntity>
    suspend fun updateUserIncomeCategory(newIncomeCategory: IncomeCategoryListEntity): AppResult<Unit>
    suspend fun updateUserExpenseCategory(newExpenseCategory: ExpenseCategoryListEntity): AppResult<Unit>
    suspend fun addMonthlyCategoryTransaction(monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit>
    suspend fun getMonthlyCategorySummary(monthYear: String, category: String): AppResult<MonthlyCategorySummary>
    suspend fun getAllMonthlyCategories(monthYear: String): AppResult<List<MonthlyCategorySummary>>
    suspend fun getMonthlyCategoryTransactionReferences(monthYear: String, category: String): AppResult<List<DocumentReferenceEntity>>
    suspend fun getMonthlyCategoryTransaction(monthYear: String, category: String): AppResult<List<TransactionEntity>>

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

    override suspend fun addMonthlyCategoryTransaction(monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit> {
        return categoryRepository.addMonthlyCategoryTransaction(monthYear, transactionEntity)
    }

    override suspend fun getMonthlyCategorySummary(monthYear: String, category: String): AppResult<MonthlyCategorySummary> {
        return categoryRepository.getMonthlyCategorySummary(monthYear, category)
    }

    override suspend fun getAllMonthlyCategories(monthYear: String): AppResult<List<MonthlyCategorySummary>> {
        return categoryRepository.getAllMonthlyCategories(monthYear)
    }

    override suspend fun getMonthlyCategoryTransactionReferences(
        monthYear: String,
        category: String
    ): AppResult<List<DocumentReferenceEntity>> {
        return categoryRepository.getMonthlyCategoryTransactionReferences(monthYear, category)

    }

    private suspend fun getTransactionFromReference(transRef: DocumentReference): AppResult<TransactionEntity> {
        return categoryRepository.getTransactionFromReference(transRef)
    }

    override suspend fun getMonthlyCategoryTransaction(monthYear: String, category: String): AppResult<List<TransactionEntity>> {
        return withContext(Dispatchers.IO) {
            when (val result = getMonthlyCategoryTransactionReferences(monthYear, category)) {
                is AppResult.Success -> {
                    val list = mutableListOf<TransactionEntity>()
                    val runningTask = result.data.map {
                        async {
                            it.transRef?.let {
                                getTransactionFromReference(it)
                            }
                        }
                    }

                    val responses = runningTask.awaitAll()

                    responses.forEach {
                        if (it is AppResult.Success) list.add(it.data)
                        else return@withContext AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                    }
                    AppResult.Success(list)
                }

                is AppResult.Failure -> {
                    AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                }
            }
        }

    }
}