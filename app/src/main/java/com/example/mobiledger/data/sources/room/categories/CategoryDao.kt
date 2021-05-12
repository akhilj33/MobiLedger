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
    suspend fun saveCategory(categoryRoomItem: CategoryRoomItem)

    @Query("SELECT incomeCategoryList FROM category ")
    suspend fun fetchUserIncomeCategoryList(): IncomeCategoryListEntity?

    @Query("UPDATE category SET incomeCategoryList=:list")
    suspend fun updateIncomeCategoryList(list: IncomeCategoryListEntity)

    @Query("UPDATE category SET expenseCategoryList=:list")
    suspend fun updateExpenseCategoryList(list: ExpenseCategoryListEntity)

}