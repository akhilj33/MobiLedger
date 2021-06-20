package com.example.mobiledger.data.sources.room.categories

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monthly_category_summary")
data class MonthlyCategorySummaryRoomItem(
    @ColumnInfo(name = "monthYear") val monthYear: String,
    @PrimaryKey @ColumnInfo(name = "categoryName") val categoryName: String,
    @ColumnInfo(name = "categoryAmount") val categoryAmount: Long,
    @ColumnInfo(name = "categoryType") val categoryType: String
)
