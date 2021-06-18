package com.example.mobiledger.presentation.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.enums.TransactionType
import com.github.mikephil.charting.data.PieEntry

sealed class HomeViewItem(val viewType: HomeViewType) {
    data class HeaderDataRow(val headerData: HeaderData, val type: HomeViewType = HomeViewType.Header) : HomeViewItem(type)
    data class MonthlyDataRow(val data: MonthlyData, val type: HomeViewType = HomeViewType.MonthlyData) : HomeViewItem(type)
    data class MonthlyTotalPie(val pieEntryList: ArrayList<PieEntry>, val type: HomeViewType = HomeViewType.MonthlyTotalPieChart) :
        HomeViewItem(type)
    data class TransactionDataRow(val data: TransactionData, val type: HomeViewType = HomeViewType.TransactionData) : HomeViewItem(type)
    object EmptyDataRow : HomeViewItem(HomeViewType.EmptyData)
}

data class HeaderData(@StringRes val headerString: Int, val isSecondaryHeaderVisible: Boolean = false)

data class MonthlyData(val incomeAmount: Long, val expenseAmount: Long)

data class TransactionData(
    val id: String,
    val name: String,
    val amount: String,
    val transactionType: TransactionType,
    val category: String,
    val transactionEntity: TransactionEntity,
    @DrawableRes val categoryIcon: Int
)

enum class HomeViewType { Header, MonthlyData, MonthlyTotalPieChart, TransactionData, EmptyData }
