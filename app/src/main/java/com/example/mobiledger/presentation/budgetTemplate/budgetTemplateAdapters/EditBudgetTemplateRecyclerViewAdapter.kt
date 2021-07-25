package com.example.mobiledger.presentation.budgetTemplate.budgetTemplateAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledger.common.extention.setOnSafeClickListener
import com.example.mobiledger.common.utils.DefaultCategoryUtils
import com.example.mobiledger.databinding.BudgetTemplateCategoryItemBinding
import com.example.mobiledger.domain.entities.BudgetTemplateCategoryEntity
import com.example.mobiledger.domain.enums.TransactionType

class EditBudgetTemplateRecyclerViewAdapter(
    val onTemplateItemClick: (BudgetTemplateCategoryEntity) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context

    private val budgetTemplateCategoryList: MutableList<BudgetTemplateCategoryEntity> = mutableListOf()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(BudgetTemplateCategoryItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as EditBudgetTemplateRecyclerViewAdapter.ViewHolder).bind(budgetTemplateCategoryList[position])
    }

    override fun getItemCount(): Int = budgetTemplateCategoryList.size

    inner class ViewHolder(private val viewBinding: BudgetTemplateCategoryItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(budgetTemplateCategoryEntity: BudgetTemplateCategoryEntity) {

            viewBinding.apply {
                tvCategoryName.text = budgetTemplateCategoryEntity.category
                tvBudgetAmount.text = budgetTemplateCategoryEntity.categoryBudget.toString()
                viewBinding.ivCategoryIcon.background =
                    ContextCompat.getDrawable(
                        context,
                        DefaultCategoryUtils.getCategoryIcon(budgetTemplateCategoryEntity.category, TransactionType.Expense)
                    )
                root.setOnSafeClickListener {
                    onTemplateItemClick(budgetTemplateCategoryEntity)
                }
            }
        }
    }

    fun addList(list: List<BudgetTemplateCategoryEntity>) {
        if (list.isNotEmpty()) {
            val newIndex = budgetTemplateCategoryList.size
            val newItemsCount = list.size
            if (budgetTemplateCategoryList.addAll(list)) notifyItemRangeInserted(newIndex, newItemsCount)
        }
        budgetTemplateCategoryList.clear()
        budgetTemplateCategoryList.addAll(list)
        notifyDataSetChanged()
    }
}