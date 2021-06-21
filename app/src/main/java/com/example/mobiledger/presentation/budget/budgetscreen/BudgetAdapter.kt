package com.example.mobiledger.presentation.budget.budgetscreen

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledger.common.extention.roundToOneDecimal
import com.example.mobiledger.common.extention.toAmount
import com.example.mobiledger.common.extention.toPercent
import com.example.mobiledger.databinding.*
import com.example.mobiledger.presentation.budget.BudgetCategoryData
import com.example.mobiledger.presentation.budget.BudgetViewItem
import com.example.mobiledger.presentation.budget.BudgetViewType
import com.example.mobiledger.presentation.budget.MonthlyBudgetOverviewData


class BudgetAdapter(
    val onMakeBudgetClick: () -> Unit,
    val onApplyTemplateClick: () -> Unit,
    val onBudgetOverViewClick: () -> Unit,
    val onAddBudgetCategoryClick: () -> Unit,
    val onBudgetCategoryClick: (category: String, budget: Long) -> Unit
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

            BudgetViewType.BtnAddCategory -> AddBudgetCategoryViewHolder(
                AddBudgetCategoryBinding.inflate(layoutInflater, parent, false)
            )

            BudgetViewType.BudgetData -> BudgetDataViewHolder(
                BudgetCategoryItemBinding.inflate(layoutInflater, parent, false)
            )

            BudgetViewType.EmptyBudget -> BudgetEmptyViewHolder(
                BudgetEmptyItemBinding.inflate(layoutInflater, parent, false)
            )

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is BudgetViewItem.BudgetHeaderData -> (holder as BudgetHeaderViewHolder).bind(item.data)
            is BudgetViewItem.BudgetOverviewData -> (holder as MonthlyBudgetOverviewViewHolder).bind(item.data)
            is BudgetViewItem.BtnAddCategory -> (holder as AddBudgetCategoryViewHolder).bind()
            is BudgetViewItem.BudgetCategory -> (holder as BudgetDataViewHolder).bind(item.data)
            is BudgetViewItem.BudgetEmpty -> (holder as BudgetEmptyViewHolder).bind()
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return items[position].viewType.ordinal
    }


    /*---------------------------------View Holders---------------------------- */

    inner class BudgetHeaderViewHolder(private val viewBinding: BudgetHeaderLayoutBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(headerString: Int) {
            viewBinding.tvHeader.text = context.resources.getString(headerString)
        }
    }

    inner class MonthlyBudgetOverviewViewHolder(private val viewBinding: MonthlyBudgetOverviewItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: MonthlyBudgetOverviewData) {
            viewBinding.apply {
                imgEditBudget.setOnClickListener {
                    onBudgetOverViewClick()
                }
                tvMaxBudgetAmount.text = item.maxBudget.toAmount()
                tvTotalBudgetAmount.text = item.totalBudget.toAmount()
                val percent = (item.totalBudget.toFloat() / item.maxBudget.toFloat() * 100)
                budgetAmountSeekBarID.value = (kotlin.math.min(percent, 100f))
            }
        }
    }

    inner class BudgetDataViewHolder(private val viewBinding: BudgetCategoryItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: BudgetCategoryData) {
            viewBinding.apply {
                budgetCategoryRoot.setOnClickListener { onBudgetCategoryClick(item.categoryName, item.totalCategoryBudget) }
                tvBudgetAmount.text = item.totalCategoryBudget.toAmount()
                tvCategoryName.text = item.categoryName
                tvAmount.text = item.totalCategoryExpense.toAmount()
                ivCategoryIcon.background = ContextCompat.getDrawable(context, item.categoryIcon)
                val percent = (item.totalCategoryExpense.toFloat() / item.totalCategoryBudget.toFloat() * 100)
                budgetCatAmountSeekBarID.value = (kotlin.math.min(percent, 100f))
                tvSpentPercent.text = percent.toString().roundToOneDecimal(percent).toPercent()
            }
        }
    }

    inner class AddBudgetCategoryViewHolder(private val viewBinding: AddBudgetCategoryBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind() {
            viewBinding.apply {
                root.setOnClickListener {
                    onAddBudgetCategoryClick()
                }
            }
        }
    }

    inner class BudgetEmptyViewHolder(private val viewBinding: BudgetEmptyItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind() {
            viewBinding.apply {
                btnMakeBudget.setOnClickListener {
                    onMakeBudgetClick()
                }
                btnApplyTemplate.setOnClickListener {
                    onApplyTemplateClick()
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
