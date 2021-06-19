package com.example.mobiledger.presentation.budgetTemplate.budgetTemplateAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledger.common.utils.DateUtils
import com.example.mobiledger.databinding.ItemBudgetTemplateListBinding
import com.example.mobiledger.domain.entities.NewBudgetTemplateEntity

class BudgetTemplateFragmentRecyclerAdapter(
    val onTemplateItemClick: (String) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context

    private val budgetTemplateList: MutableList<NewBudgetTemplateEntity> = mutableListOf()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemBudgetTemplateListBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BudgetTemplateFragmentRecyclerAdapter.ViewHolder).bind(budgetTemplateList[position])
    }

    override fun getItemCount(): Int = budgetTemplateList.size

    inner class ViewHolder(private val viewBinding: ItemBudgetTemplateListBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(newBudgetTemplateEntity: NewBudgetTemplateEntity) {

            viewBinding.apply {
                tvTemplateName.text = newBudgetTemplateEntity.name
                tvTemplateAmount.text = newBudgetTemplateEntity.maxBudgetLimit.toString()
                tvTemplateDate.text = DateUtils.getDateInDDMMMMyyyyFormat(newBudgetTemplateEntity.transactionTime)
                root.setOnClickListener {
                    onTemplateItemClick(newBudgetTemplateEntity.id)
                }
            }
        }
    }

    fun addList(list: List<NewBudgetTemplateEntity>) {
        if (list.isNotEmpty()) {
            val newIndex = budgetTemplateList.size
            val newItemsCount = list.size
            if (budgetTemplateList.addAll(list)) notifyItemRangeInserted(newIndex, newItemsCount)
        }
        budgetTemplateList.clear()
        budgetTemplateList.addAll(list)
        notifyDataSetChanged()
    }
}