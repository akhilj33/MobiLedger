package com.example.mobiledger.data.sources.room.categories

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mobiledger.domain.entities.ExpenseCategoryListEntity
import com.example.mobiledger.domain.entities.IncomeCategoryListEntity

@Entity(tableName = "category")
data class CategoryRoomItem(
    @PrimaryKey @ColumnInfo(name = "uid") val uid: String,
    @ColumnInfo(name = "expenseCategoryList") val expenseCategoryList: ExpenseCategoryListEntity? = null,
    @ColumnInfo(name = "incomeCategoryList") val incomeCategoryList: IncomeCategoryListEntity? = null
)

