package com.example.mobiledger.data.repository

import com.example.mobiledger.common.utils.DateUtils.isCurrentMonthYear
import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.data.sources.api.CategoryApi
import com.example.mobiledger.data.sources.cache.CacheSource
import com.example.mobiledger.data.sources.room.categories.CategoriesDb
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.*
import com.example.mobiledger.presentation.budget.MonthlyCategorySummary
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

interface CategoryRepository {
    suspend fun addUserIncomeCategoryDb(categoryList: List<String>): AppResult<Unit>
    suspend fun addUserExpenseCategoryDb(categoryList: List<String>): AppResult<Unit>

    suspend fun getUserIncomeCategories(): AppResult<IncomeCategoryListEntity>
    suspend fun getUserExpenseCategories(): AppResult<ExpenseCategoryListEntity>

    suspend fun updateUserIncomeCategory(newIncomeCategory: IncomeCategoryListEntity): AppResult<Unit>
    suspend fun updateUserExpenseCategory(newExpenseCategory: ExpenseCategoryListEntity): AppResult<Unit>

    suspend fun addMonthlyCategoryTransaction(monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit>
    suspend fun deleteMonthlyCategoryTransaction(monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit>

    suspend fun getMonthlyCategorySummary(monthYear: String, category: String, isPTR: Boolean): AppResult<MonthlyCategorySummary?>
    suspend fun addMonthlyCategorySummary(
        monthYear: String,
        category: String,
        monthlyCategorySummary: MonthlyCategorySummary
    ): AppResult<Unit>

    suspend fun deleteMonthlyCategorySummary(monthYear: String, category: String): AppResult<Unit>

    suspend fun getAllMonthlyCategories(monthYear: String, isPTR: Boolean): AppResult<List<MonthlyCategorySummary>>
    suspend fun getMonthlyCategoryTransactionReferences(
        monthYear: String,
        category: String,
        isPTR: Boolean
    ): AppResult<List<DocumentReferenceEntity>>

    suspend fun getTransactionFromReference(transRef: DocumentReference): AppResult<TransactionEntity>
}

class CategoryRepositoryImpl(
    private val categoryApi: CategoryApi,
    private val cacheSource: CacheSource,
    private val categoryDb: CategoriesDb,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    CategoryRepository {

    override suspend fun addUserIncomeCategoryDb(categoryList: List<String>): AppResult<Unit> {
        return withContext(dispatcher) {
            val uid = cacheSource.getUID()
            if (uid != null) {
                categoryApi.addDefaultIncomeCategories(uid, categoryList)
            } else
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun addUserExpenseCategoryDb(categoryList: List<String>): AppResult<Unit> {
        return withContext(dispatcher) {
            val uid = cacheSource.getUID()
            if (uid != null) {
                categoryApi.addDefaultExpenseCategories(uid, categoryList)
            } else
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
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
                categoryDb.saveUserCategoryList(categoryList)
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

    override suspend fun updateUserIncomeCategory(newIncomeCategory: IncomeCategoryListEntity): AppResult<Unit> {
        return withContext(dispatcher) {
            val uid = cacheSource.getUID()
            if (uid != null) {
                categoryApi.updateUserIncomeCategory(uid, newIncomeCategory).also {
                    if (it is AppResult.Success) categoryDb.updateIncomeCategoryList(newIncomeCategory)
                }
            } else
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun updateUserExpenseCategory(newExpenseCategory: ExpenseCategoryListEntity): AppResult<Unit> {
        return withContext(dispatcher) {
            val uid = cacheSource.getUID()
            if (uid != null) {
                categoryApi.updateUserExpenseCategory(uid, newExpenseCategory).also {
                    if (it is AppResult.Success) categoryDb.updateExpenseCategoryList(newExpenseCategory)
                }
            } else
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun addMonthlyCategoryTransaction(monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                categoryApi.addMonthlyCategoryTransaction(uId, monthYear, transactionEntity).also {
                    if (it is AppResult.Success && isCurrentMonthYear(monthYear)) categoryDb.saveMonthlyCategoryTransaction(
                        uId,
                        monthYear,
                        transactionEntity
                    )
                }
            } else
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun deleteMonthlyCategoryTransaction(monthYear: String, transactionEntity: TransactionEntity): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                categoryApi.deleteMonthlyCategoryTransaction(uId, monthYear, transactionEntity).also {
                    if (it is AppResult.Success && isCurrentMonthYear(monthYear)) categoryDb.deleteMonthlyCategoryTransaction(
                        uId,
                        monthYear,
                        transactionEntity
                    )
                }
            } else
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun getMonthlyCategorySummary(
        monthYear: String,
        category: String,
        isPTR: Boolean
    ): AppResult<MonthlyCategorySummary?> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                if (isCurrentMonthYear(monthYear)) {
                    val monthlyCategorySummaryExists = categoryDb.hasMonthlyCategorySummary(monthYear, category)
                    if (!monthlyCategorySummaryExists || isPTR) {
                        when (val firebaseResult = categoryApi.getMonthlyCategorySummary(uId, monthYear, category)) {
                            is AppResult.Success -> {
                                categoryDb.saveMonthlyCategorySummary(monthYear, firebaseResult.data ?: MonthlyCategorySummary())
                            }
                            is AppResult.Failure -> {
                                return@withContext AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                            }
                        }
                    }
                    categoryDb.fetchMonthlyCategorySummary(monthYear, category)
                } else categoryApi.getMonthlyCategorySummary(uId, monthYear, category)

            } else
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }


    override suspend fun addMonthlyCategorySummary(
        monthYear: String,
        category: String,
        monthlyCategorySummary: MonthlyCategorySummary,
    ): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                categoryApi.addMonthlyCategorySummary(uId, monthYear, category, monthlyCategorySummary).also {
                    if (it is AppResult.Success && isCurrentMonthYear(monthYear)) categoryDb.saveMonthlyCategorySummary(
                        monthYear,
                        monthlyCategorySummary
                    )
                }
            } else
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun deleteMonthlyCategorySummary(monthYear: String, category: String): AppResult<Unit> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                categoryApi.deleteMonthlyCategorySummary(uId, monthYear, category).also {
                    if (it is AppResult.Success && isCurrentMonthYear(monthYear)) categoryDb.deleteMonthlyCategorySummary(
                        monthYear,
                        category
                    )
                }
            } else
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun getAllMonthlyCategories(monthYear: String, isPTR: Boolean): AppResult<List<MonthlyCategorySummary>> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                if (isCurrentMonthYear(monthYear)) {
                    val monthlyCategoryTransactionsExists = categoryDb.hasMonthlyCategorySummary(monthYear)
                    if (!monthlyCategoryTransactionsExists || isPTR) {
                        when (val firebaseResult = categoryApi.getAllMonthlyCategorySummaries(uId, monthYear)) {
                            is AppResult.Success -> {
                                categoryDb.saveAllMonthlyCategorySummaries(monthYear, firebaseResult.data)
                            }
                            is AppResult.Failure -> {
                                return@withContext AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                            }
                        }
                    }
                    categoryDb.fetchAllMonthlyCategorySummaries(monthYear)
                } else categoryApi.getAllMonthlyCategorySummaries(uId, monthYear)
            } else
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun getMonthlyCategoryTransactionReferences(
        monthYear: String,
        category: String,
        isPTR: Boolean
    ): AppResult<List<DocumentReferenceEntity>> {
        return withContext(dispatcher) {
            val uId = cacheSource.getUID()
            if (uId != null) {
                if (isCurrentMonthYear(monthYear)) {
                    val monthlyCategoryTransactionsExists = categoryDb.hasMonthlyCategoryTransaction(monthYear, category)
                    if (!monthlyCategoryTransactionsExists || isPTR) {
                        when (val firebaseResult = categoryApi.getMonthlyCategoryTransactionReferences(uId, monthYear, category)) {
                            is AppResult.Success -> {
                                categoryDb.addAllMonthlyCategoryTransactions(monthYear, category, firebaseResult.data)
                            }
                            is AppResult.Failure -> {
                                return@withContext AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
                            }
                        }
                    }
                    categoryDb.fetchMonthlyCategoryTransactionsList(monthYear, category)
                } else categoryApi.getMonthlyCategoryTransactionReferences(uId, monthYear, category)
            } else
                AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    override suspend fun getTransactionFromReference(transRef: DocumentReference): AppResult<TransactionEntity> {
        return withContext(dispatcher) {
            categoryApi.getTransactionFromReference(transRef)
        }
    }
}


