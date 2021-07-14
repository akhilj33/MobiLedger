package com.example.mobiledger.presentation.budget.budgetscreen

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledger.R
import com.example.mobiledger.common.extention.*
import com.example.mobiledger.databinding.*
import com.example.mobiledger.presentation.budget.*
import com.google.android.material.slider.Slider


class BudgetAdapter(
    val onSetMonthlyLimitBtnClick: () -> Unit,
    val onApplyTemplateClick: () -> Unit,
    val onUpdateMonthlyLimitClick: () -> Unit,
    val onAddBudgetCategoryClick: () -> Unit,
    val onUpdateBudgetCategoryClick: (category: String, budget: Long) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    private val items: MutableList<BudgetViewItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (BudgetViewType.values()[viewType]) {

            BudgetViewType.Header -> BudgetHeaderViewHolder(BudgetHeaderLayoutBinding.inflate(layoutInflater, parent, false))

            BudgetViewType.MonthlyBudgetOverview -> MonthlyBudgetOverviewViewHolder(
                MonthlyBudgetOverviewItemBinding.inflate(layoutInflater, parent, false)
            )

            BudgetViewType.BudgetData -> BudgetDataViewHolder(
                BudgetCategoryItemBinding.inflate(layoutInflater, parent, false)
            )

            BudgetViewType.EmptyBudget -> BudgetEmptyViewHolder(
                BudgetEmptyItemBinding.inflate(layoutInflater, parent, false)
            )

            BudgetViewType.CategoryBudgetEmpty -> BudgetCategoryEmptyViewHolder(
                BudgetCategoryEmptyItemBinding.inflate(layoutInflater, parent, false)
            )

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is BudgetViewItem.BudgetHeaderData -> (holder as BudgetHeaderViewHolder).bind(item.headerData)
            is BudgetViewItem.BudgetOverviewData -> (holder as MonthlyBudgetOverviewViewHolder).bind(item.data)
            is BudgetViewItem.BudgetCategory -> (holder as BudgetDataViewHolder).bind(item.data)
            is BudgetViewItem.BudgetEmpty -> (holder as BudgetEmptyViewHolder).bind()
            is BudgetViewItem.BudgetCategoryEmpty -> (holder as BudgetCategoryEmptyViewHolder).bind()
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return items[position].viewType.ordinal
    }


    /*---------------------------------View Holders---------------------------- */

    inner class BudgetHeaderViewHolder(private val viewBinding: BudgetHeaderLayoutBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(headerData: HeaderData) {
            viewBinding.apply {
                tvAddCategoryBudget.setOnClickListener { onAddBudgetCategoryClick() }
                tvHeader.text = context.resources.getString(headerData.headerString)
                if (headerData.isSecondaryHeaderVisible) tvAddCategoryBudget.visible()
                else tvAddCategoryBudget.gone()
            }
        }
    }

    inner class MonthlyBudgetOverviewViewHolder(private val viewBinding: MonthlyBudgetOverviewItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: MonthlyBudgetOverviewData) {
            viewBinding.apply {
                addColorChangeListenerToSlider(budgetAmountSeekBarID)
                tvMaxBudgetAmount.setOnClickListener {
                    onUpdateMonthlyLimitClick()
                }
                val percentSpent = (item.totalMonthlyExpense.toFloat() / item.maxBudget.toFloat() * 100)
                if (percentSpent > 0.0){
                    percentSpentGroup.visible()
                    tvPercentSpent.text = context.getString(R.string.percent_spent, percentSpent.roundToOneDecimal().toPercent())
                }
                else percentSpentGroup.gone()
                tvMaxBudgetAmount.text = context.getString(R.string.monthly_limit_amount, item.maxBudget.toAmount())
                tvTotalBudgetAmount.text = item.totalBudget.toAmount()
                val percent = (item.totalBudget.toFloat() / item.maxBudget.toFloat() * 100)
                budgetAmountSeekBarID.value = (kotlin.math.min(percent, 100f))
            }
        }
    }

    private fun addColorChangeListenerToSlider(sliderView: Slider) {
        sliderView.addOnChangeListener { slider, value, _ ->
            if (value <= 33) ContextCompat.getColorStateList(context, R.color.colorGreen)?.let { slider.trackActiveTintList = it }
            else if (value > 33 && value <= 66) ContextCompat.getColorStateList(context, R.color.colorYellow)
                ?.let { slider.trackActiveTintList = it }
            else ContextCompat.getColorStateList(context, R.color.colorAppBlue)?.let { slider.trackActiveTintList = it }
        }
    }

    inner class BudgetDataViewHolder(private val viewBinding: BudgetCategoryItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: BudgetCategoryData) {
            viewBinding.apply {
                addColorChangeListenerToSlider(budgetCatAmountSeekBarID)
                budgetCategoryRoot.setOnClickListener { onUpdateBudgetCategoryClick(item.categoryName, item.totalCategoryBudget) }
                tvBudgetAmount.text = item.totalCategoryBudget.toAmount()
                tvCategoryName.text = item.categoryName
                tvAmount.text = item.totalCategoryExpense.toAmount()
                ivCategoryIcon.background = ContextCompat.getDrawable(context, item.categoryIcon)
                val percent = (item.totalCategoryExpense.toFloat() / item.totalCategoryBudget.toFloat() * 100)
                budgetCatAmountSeekBarID.value = (kotlin.math.min(percent, 100f))
                tvSpentPercent.text = percent.roundToOneDecimal().toPercent()
            }
        }
    }

    inner class BudgetEmptyViewHolder(private val viewBinding: BudgetEmptyItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind() {
            viewBinding.apply {
                btnMakeBudget.setOnClickListener {
                    onSetMonthlyLimitBtnClick()
                }
                btnApplyTemplate.setOnClickListener {
                    onApplyTemplateClick()
                }
            }
        }
    }

    inner class BudgetCategoryEmptyViewHolder(private val viewBinding: BudgetCategoryEmptyItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind() {
            viewBinding.apply {
                btnAddCategoryBudget.setOnClickListener {
                    onAddBudgetCategoryClick()
                }
            }
        }
    }

    /*---------------------------------Utility Functions---------------------------- */

    fun addItemList(budgetItemList: List<BudgetViewItem>) {
        items.clear()
        items.addAll(budgetItemList)
        notifyDataSetChanged()
    }
}
