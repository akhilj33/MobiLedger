package com.example.mobiledger.presentation.stats

import com.example.mobiledger.common.base.BaseNavigator
import java.util.*

interface StatsNavigator : BaseNavigator {
    fun navigateToProfileScreen()
    fun navigateToStatsDetailScreen(categoryNameList: List<String>, amount: Long, monthYear: Calendar)
}