package com.example.mobiledger.data.sources.room.categories

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monthly_category_transactions")
data class CategoriesTransactionsRoomItem(
    @ColumnInfo(name = "monthYear") val monthYear: String,
    @PrimaryKey @ColumnInfo(name = "categoryName") val categoryName: String,
    @ColumnInfo(name = "transRefList") val transRefList: MutableList<DocumentReferenceRoomItem>? = null,
)


data class DocumentReferenceRoomItem(val transRef: String)
