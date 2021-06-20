package com.example.mobiledger.data.sources.room.categories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mobiledger.domain.entities.ExpenseCategoryListEntity
import com.example.mobiledger.domain.entities.IncomeCategoryListEntity

@Dao
interface CategoryDao {

    @Query("SELECT count(*) FROM category")
    suspend fun hasCategory(): Int?

    @Query("SELECT expenseCategoryList FROM category")
    suspend fun fetchUserExpenseCategoryList(): ExpenseCategoryListEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCategoryList(categoryRoomItem: CategoryRoomItem)

    @Query("SELECT incomeCategoryList FROM category ")
    suspend fun fetchUserIncomeCategoryList(): IncomeCategoryListEntity?

    @Query("UPDATE category SET incomeCategoryList=:list")
    suspend fun updateIncomeCategoryList(list: IncomeCategoryListEntity)

    @Query("UPDATE category SET expenseCategoryList=:list")
    suspend fun updateExpenseCategoryList(list: ExpenseCategoryListEntity)
}

@Dao
interface MonthlyCategorySummaryDao {
    @Query("SELECT count(*) FROM monthly_category_summary WHERE monthYear = :monthYear")
    suspend fun hasAnyMonthlyCategorySummary(monthYear: String): Int?

    @Query("SELECT count(*) FROM monthly_category_summary WHERE monthYear = :monthYear and categoryName = :category")
    suspend fun hasMonthlyCategorySummary(monthYear: String, category: String): Int?

    @Query("SELECT * FROM monthly_category_summary WHERE monthYear = :monthYear and categoryName = :category")
    suspend fun fetchMonthlyCategorySummary(monthYear: String, category: String): MonthlyCategorySummaryRoomItem?

    @Query("SELECT * FROM monthly_category_summary WHERE monthYear = :monthYear")
    suspend fun fetchAllMonthlyCategorySummaries(monthYear: String): List<MonthlyCategorySummaryRoomItem>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMonthlyCategorySummary(monthlyCategorySummary: MonthlyCategorySummaryRoomItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAllMonthlyCategorySummaries(monthlyCategorySummaryList: List<MonthlyCategorySummaryRoomItem>)

    @Query("DELETE FROM monthly_category_summary WHERE monthYear = :monthYear and categoryName = :category")
    suspend fun deleteMonthlyCategorySummary(monthYear: String, category: String)

    @Query("DELETE FROM monthly_category_summary WHERE monthYear = :monthYear")
    suspend fun deleteAllMonthlyCategorySummaries(monthYear: String)
}

@Dao
interface CategoryTransactionsRefDao {
    @Query("SELECT count(*) FROM monthly_category_transactions WHERE monthYear = :monthYear and categoryName = :category")
    suspend fun hasMonthlyCategoryTransactions(monthYear: String, category: String): Int?

    @Query("SELECT * FROM monthly_category_transactions WHERE monthYear = :monthYear and categoryName = :category")
    suspend fun fetchMonthlyCategoryTransactionsList(monthYear: String, category: String): CategoriesTransactionsRoomItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMonthlyCategoryTransaction(monthlyCategoryTransaction: CategoriesTransactionsRoomItem)

    @Query("DELETE FROM monthly_category_transactions WHERE monthYear = :monthYear and categoryName = :category")
    suspend fun deleteMonthlyTransactions(monthYear: String, category: String)
}