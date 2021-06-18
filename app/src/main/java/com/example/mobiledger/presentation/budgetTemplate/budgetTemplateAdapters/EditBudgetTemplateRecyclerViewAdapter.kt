package com.example.mobiledger.presentation.budgetTemplate.budgetTemplateAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledger.databinding.BudgetTemplateCategoryItemBinding
import com.example.mobiledger.domain.entities.BudgetTemplateCategoryEntity

class EditBudgetTemplateRecyclerViewAdapter(
    val onTemplateItemClick: (String) -> Unit,
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

                root.setOnClickListener {
                    onTemplateItemClick(budgetTemplateCategoryEntity.category)
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