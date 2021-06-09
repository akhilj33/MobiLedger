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

    suspend fun getMonthlyCategorySummary(monthYear: String, category: String): AppResult<MonthlyCategorySummary?>
    suspend fun addMonthlyCategorySummaryData(
        monthYear: String,
        category: String,
        monthlyCategorySummary: MonthlyCategorySummary
    ): AppResult<Unit>

    suspend fun deleteMonthlyCategory(monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit>

    suspend fun deleteMonthlyCategorySummary(monthYear: String, category: String): AppResult<Unit>
    suspend fun updateMonthlyCategoryAmount(
        monthYear: String,
        oldTransactionEntity: TransactionEntity,
        categoryAmountChange: Long,
        editCategoryTransactionType: EditCategoryTransactionType
    ): AppResult<Unit>

    suspend fun updateOrAddMonthlyCategorySummary(monthYear: String, newTransactionEntity: TransactionEntity): AppResult<Unit>

    suspend fun updateMonthlyCategoryData(
        monthYear: String,
        monthlyCategorySummary: MonthlyCategorySummary,
        transactionEntity: TransactionEntity,
        editCategoryTransactionType: EditCategoryTransactionType
    ): AppResult<Unit>

    suspend fun addMonthlyCategoryTransaction(monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit>
    suspend fun deleteMonthlyCategoryTransaction(monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit>
    suspend fun updateCategoryDataOnCategoryChanged(
        monthYear: String,
        oldTransactionEntity: TransactionEntity,
        newTransactionEntity: TransactionEntity,
    ): AppResult<Unit>

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

    override suspend fun deleteMonthlyCategoryTransaction(monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit> {
        return categoryRepository.deleteMonthlyCategoryTransaction(monthYear, transactionEntity)
    }

    override suspend fun getMonthlyCategorySummary(monthYear: String, category: String): AppResult<MonthlyCategorySummary?> {
        return categoryRepository.getMonthlyCategorySummary(monthYear, category)
    }

    override suspend fun addMonthlyCategorySummaryData(
        monthYear: String,
        category: String,
        monthlyCategorySummary: MonthlyCategorySummary
    ): AppResult<Unit> {
        return categoryRepository.addMonthlyCategorySummaryData(monthYear, category, monthlyCategorySummary)
    }

    /**
     * Responsibilities
     * 1- Deletes monthly category data i.e. monthly category summary and transactions linked
     */
    override suspend fun deleteMonthlyCategory(monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit> {
      return withContext(Dispatchers.IO){
            val deleteMonthlySummaryJob = async { deleteMonthlyCategorySummary(monthYear, transactionEntity.category) }
            val deleteMonthlyCategoryTransactionJob = async { deleteMonthlyCategoryTransaction(monthYear, transactionEntity) }

            if (deleteMonthlyCategoryTransactionJob.await() is AppResult.Success && deleteMonthlySummaryJob.await() is AppResult.Success)
                AppResult.Success(Unit)
            else
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun deleteMonthlyCategorySummary(monthYear: String, category: String): AppResult<Unit> {
        return categoryRepository.deleteMonthlyCategorySummary(monthYear, category)
    }

    /**
     * Responsibilities
     * 1- It updates monthly category amount and also edit transaction linked to category on the basis of EditCategoryTransactionType
     * 2 - If the new amount is 0 i.e. category has no transactions linked then it'll delete the category
     */
    override suspend fun updateMonthlyCategoryAmount(
        monthYear: String,
        oldTransactionEntity: TransactionEntity,
        categoryAmountChange: Long,
        editCategoryTransactionType: EditCategoryTransactionType
    ): AppResult<Unit> {
        return withContext(Dispatchers.IO) {
            val category = oldTransactionEntity.category
            when (val result = getMonthlyCategorySummary(monthYear, category)) {
                is AppResult.Success -> {
                    if (result.data == null) return@withContext AppResult.Success(Unit)
                    val newCategoryAmount = result.data.categoryAmount + categoryAmountChange
                    if (newCategoryAmount > 0L)
                        updateMonthlyCategoryData(
                            monthYear, MonthlyCategorySummary(category, newCategoryAmount, result.data.categoryType),
                            oldTransactionEntity, editCategoryTransactionType
                        )
                    else deleteMonthlyCategory(monthYear, oldTransactionEntity)
                }
                is AppResult.Failure -> AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
        }
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

    /**
     * Responsibilities
     * 1- Fetches all transactions linked to a specific category
     */
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

    /**
     * Responsibilities
     * 1- If category is already present, It updates monthly category data
     * 2- If category is not present, It adds monthly category data
     */
    override suspend fun updateOrAddMonthlyCategorySummary(monthYear: String, newTransactionEntity: TransactionEntity): AppResult<Unit> {
        return withContext(Dispatchers.IO) {
            when (val result = getMonthlyCategorySummary(monthYear, newTransactionEntity.category)) {
                is AppResult.Success -> {
                    val newAmount = newTransactionEntity.amount
                    val category = newTransactionEntity.category
                    val amt = if (result.data != null) newAmount + result.data.categoryAmount else newAmount
                    updateMonthlyCategoryData(
                        monthYear, MonthlyCategorySummary(category, amt, newTransactionEntity.transactionType.type),
                        newTransactionEntity, EditCategoryTransactionType.ADD
                    )
                }
                is AppResult.Failure -> AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
            }
        }
    }

    /**
     * Responsibilities
     * 1- Its basic work is to update category data i.e. monthly category summary and category transactions
     * 2 - Updates monthly category summary
     * 3 - If required add or delete category transactions
     */
    override suspend fun updateMonthlyCategoryData(
        monthYear: String,
        monthlyCategorySummary: MonthlyCategorySummary,
        transactionEntity: TransactionEntity,
        editCategoryTransactionType: EditCategoryTransactionType
    ): AppResult<Unit> {
        return withContext(Dispatchers.IO) {
            val updateSummaryJob = async { addMonthlyCategorySummaryData(monthYear, transactionEntity.category, monthlyCategorySummary) }
            val updateTransJob = when (editCategoryTransactionType) {
                EditCategoryTransactionType.DELETE -> async { deleteMonthlyCategoryTransaction(monthYear, transactionEntity) }
                EditCategoryTransactionType.ADD -> async { addMonthlyCategoryTransaction(monthYear, transactionEntity) }
                else -> async{ AppResult.Success(Unit) }
            }

            if (updateSummaryJob.await() is AppResult.Success && updateTransJob.await() is AppResult.Success)
                AppResult.Success(Unit)
            else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    /**
     * Responsibilities
     * 1- Update Category Transaction in Db whenever category is updated
     * 2 - It reduces old category amount and deletes transaction in it
     * 3 - It updates new category summary and also add transaction
     */
    override suspend fun updateCategoryDataOnCategoryChanged(
        monthYear: String,
        oldTransactionEntity: TransactionEntity,
        newTransactionEntity: TransactionEntity,
    ): AppResult<Unit> {
        return withContext(Dispatchers.IO) {
            val updateOldCategoryAmountJob =
                async { updateMonthlyCategoryAmount(monthYear, oldTransactionEntity, -oldTransactionEntity.amount, EditCategoryTransactionType.DELETE) }
            val updateNewCategoryJob = async { updateOrAddMonthlyCategorySummary(monthYear, newTransactionEntity) }

            if (updateNewCategoryJob.await() is AppResult.Success && updateOldCategoryAmountJob.await() is AppResult.Success)
                AppResult.Success(Unit)
            else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }
}

enum class EditCategoryTransactionType{
    ADD, DELETE, NOTHING
}