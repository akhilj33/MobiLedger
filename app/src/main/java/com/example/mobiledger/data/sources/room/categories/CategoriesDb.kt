package com.example.mobiledger.data.sources.room.categories

import com.example.mobiledger.common.utils.ConstantUtils
import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.*
import com.example.mobiledger.presentation.budget.MonthlyCategorySummary
import com.google.firebase.firestore.FirebaseFirestore

interface CategoriesDb {
    suspend fun fetchUserIncomeCategories(): AppResult<IncomeCategoryListEntity>
    suspend fun fetchUserExpenseCategories(): AppResult<ExpenseCategoryListEntity>
    suspend fun updateIncomeCategoryList(list: IncomeCategoryListEntity)
    suspend fun updateExpenseCategoryList(list: ExpenseCategoryListEntity)
    suspend fun hasCategory(): Boolean
    suspend fun saveUserCategoryList(categoryList: CategoryListEntity)

    suspend fun fetchMonthlyCategorySummary(monthYear: String, category: String): AppResult<MonthlyCategorySummary>
    suspend fun fetchAllMonthlyCategorySummaries(monthYear: String): AppResult<List<MonthlyCategorySummary>>
    suspend fun saveMonthlyCategorySummary(monthYear: String, monthlyCategorySummary: MonthlyCategorySummary)
    suspend fun saveAllMonthlyCategorySummaries(monthYear: String, monthlyCategorySummaryList: List<MonthlyCategorySummary>)
    suspend fun hasMonthlyCategorySummary(monthYear: String, category: String? = null): Boolean
    suspend fun deleteMonthlyCategorySummary(monthYear: String, category: String)

    suspend fun fetchMonthlyCategoryTransactionsList(monthYear: String, category: String): AppResult<List<DocumentReferenceEntity>>
    suspend fun saveMonthlyCategoryTransaction(uId: String, monthYear: String, transactionEntity: TransactionEntity)
    suspend fun addAllMonthlyCategoryTransactions(monthYear: String, category: String, docRefEntityList: List<DocumentReferenceEntity>)
    suspend fun hasMonthlyCategoryTransaction(monthYear: String, category: String): Boolean
    suspend fun deleteMonthlyCategoryTransaction(uId: String, monthYear: String, transactionEntity: TransactionEntity)
    suspend fun deleteAllMonthlyCategoryTransactions(monthYear: String, category: String)
}

class CategoryDbImpl(
    private val firebaseDb: FirebaseFirestore,
    private val dao: CategoryDao,
    private val categorySummaryDao: MonthlyCategorySummaryDao,
    private val categoryTransactionsRefDao: CategoryTransactionsRefDao
) : CategoriesDb {

    //    <------------------------ Category List ------------------------>

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

    override suspend fun saveUserCategoryList(categoryList: CategoryListEntity) {
        val categoryRoomItem = mapToCategoryRoomEntity(categoryList)
        dao.saveCategoryList(categoryRoomItem)
    }


//    <------------------------Monthly Category Summary ------------------------>

    override suspend fun fetchMonthlyCategorySummary(monthYear: String, category: String): AppResult<MonthlyCategorySummary> {
        val monthlyCategorySummary = categorySummaryDao.fetchMonthlyCategorySummary(monthYear, category)?.let {
            mapToMonthlyCategorySummaryEntity(it)
        }
        return if (monthlyCategorySummary != null) AppResult.Success(monthlyCategorySummary)
        else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
    }

    override suspend fun fetchAllMonthlyCategorySummaries(monthYear: String): AppResult<List<MonthlyCategorySummary>> {
        val result = categorySummaryDao.fetchAllMonthlyCategorySummaries(monthYear)?.map { mapToMonthlyCategorySummaryEntity(it) }
        return if (result != null) AppResult.Success(result)
        else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
    }

    override suspend fun saveMonthlyCategorySummary(monthYear: String, monthlyCategorySummary: MonthlyCategorySummary) {
        val monthlyCategorySummaryRoomItem = mapToMonthlyCategorySummaryRoomItem(monthYear, monthlyCategorySummary)
        categorySummaryDao.saveMonthlyCategorySummary(monthlyCategorySummaryRoomItem)
    }

    override suspend fun saveAllMonthlyCategorySummaries(monthYear: String, monthlyCategorySummaryList: List<MonthlyCategorySummary>) {
        categorySummaryDao.deleteAllMonthlyCategorySummaries(monthYear)
        val monthlyCategorySummaryRoomItemList = monthlyCategorySummaryList.map { mapToMonthlyCategorySummaryRoomItem(monthYear, it) }
        categorySummaryDao.saveAllMonthlyCategorySummaries(monthlyCategorySummaryRoomItemList)
    }

    override suspend fun hasMonthlyCategorySummary(monthYear: String, category: String?): Boolean {
        val monthlySummaryCount = if (category == null) categorySummaryDao.hasAnyMonthlyCategorySummary(monthYear)
        else categorySummaryDao.hasMonthlyCategorySummary(monthYear, category)
        return !(monthlySummaryCount == null || monthlySummaryCount == 0)
    }

    override suspend fun deleteMonthlyCategorySummary(monthYear: String, category: String) {
        categorySummaryDao.deleteMonthlyCategorySummary(monthYear, category)
        deleteAllMonthlyCategoryTransactions(monthYear, category)
    }

    //    <------------------------Monthly Category Transactions ------------------------>

    override suspend fun fetchMonthlyCategoryTransactionsList(
        monthYear: String,
        category: String
    ): AppResult<MutableList<DocumentReferenceEntity>> {
        val docRefEntityList = categoryTransactionsRefDao.fetchMonthlyCategoryTransactionsList(monthYear, category)
        return if (docRefEntityList != null) {
            val list = mutableListOf<DocumentReferenceEntity>()
            docRefEntityList.transRefList?.forEach {
                list.add(mapToDocRefEntity(it))
            }
            AppResult.Success(list)
        } else AppResult.Failure(AppError(ErrorCodes.GENERIC_ERROR))
    }

    private fun mapToDocRefEntity(documentReferenceRoomItem: DocumentReferenceRoomItem): DocumentReferenceEntity {
        documentReferenceRoomItem.apply {
            return DocumentReferenceEntity(firebaseDb.document(transRef))
        }
    }

    override suspend fun saveMonthlyCategoryTransaction(uId: String, monthYear: String, transactionEntity: TransactionEntity) {
        fetchMonthlyCategoryTransactionsList(monthYear, transactionEntity.category).let {
            if (it is AppResult.Success) {
                it.data.add(getDocumentReferenceEntity(uId, monthYear, transactionEntity))
                addAllMonthlyCategoryTransactions(monthYear, transactionEntity.category, it.data)
            }
        }
    }

    override suspend fun addAllMonthlyCategoryTransactions(
        monthYear: String,
        category: String,
        docRefEntityList: List<DocumentReferenceEntity>
    ) {
        categoryTransactionsRefDao.saveMonthlyCategoryTransaction(
            CategoriesTransactionsRoomItem(monthYear, category, docRefEntityList.map { mapToDocRefRoomItem(it) }.toMutableList())
        )
    }

    override suspend fun hasMonthlyCategoryTransaction(monthYear: String, category: String): Boolean {
        val monthlyCategoryTransCount = categoryTransactionsRefDao.hasMonthlyCategoryTransactions(monthYear, category)
        return !(monthlyCategoryTransCount == null || monthlyCategoryTransCount == 0)
    }

    override suspend fun deleteMonthlyCategoryTransaction(uId: String, monthYear: String, transactionEntity: TransactionEntity) {
        fetchMonthlyCategoryTransactionsList(monthYear, transactionEntity.category).let {
            if (it is AppResult.Success) {
                it.data.remove(getDocumentReferenceEntity(uId, monthYear, transactionEntity))
                addAllMonthlyCategoryTransactions(monthYear, transactionEntity.category, it.data)
            }
        }
    }

    override suspend fun deleteAllMonthlyCategoryTransactions(monthYear: String, category: String) {
        categoryTransactionsRefDao.deleteMonthlyTransactions(monthYear, category)
    }

    private fun getDocumentReferenceEntity(uid: String, monthYear: String, transactionEntity: TransactionEntity): DocumentReferenceEntity {
        return DocumentReferenceEntity(firebaseDb.document("/${ConstantUtils.USERS}/$uid/Months/$monthYear/Transaction/${transactionEntity.id}"))
    }
}

private fun mapToDocRefRoomItem(documentReferenceEntity: DocumentReferenceEntity): DocumentReferenceRoomItem {
    documentReferenceEntity.apply {
        return DocumentReferenceRoomItem(transRef?.path ?: "")
    }
}

private fun mapToCategoryRoomEntity(categoryListEntity: CategoryListEntity): CategoryRoomItem {
    categoryListEntity.apply {
        return CategoryRoomItem(uid, expenseCategoryList, incomeCategoryList)
    }
}

private fun mapToMonthlyCategorySummaryRoomItem(
    monthYear: String,
    monthlyCategorySummary: MonthlyCategorySummary
): MonthlyCategorySummaryRoomItem {
    monthlyCategorySummary.apply {
        return MonthlyCategorySummaryRoomItem(monthYear, categoryName, categoryAmount, categoryType)
    }
}

private fun mapToMonthlyCategorySummaryEntity(categorySummaryRoomItem: MonthlyCategorySummaryRoomItem): MonthlyCategorySummary {
    categorySummaryRoomItem.apply {
        return MonthlyCategorySummary(categoryName, categoryAmount, categoryType)
    }
}
