package com.example.mobiledger.presentation.categoryFragment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledger.common.extention.gone
import com.example.mobiledger.common.extention.setOnSafeClickListener
import com.example.mobiledger.common.extention.visible
import com.example.mobiledger.common.utils.DefaultCategoryUtils
import com.example.mobiledger.databinding.ItemCategoryBinding
import com.example.mobiledger.domain.enums.TransactionType

class CategoryAdapter(
    val transactionType: TransactionType,
    val onCategoryDeleteClicked: (String, List<String>) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context

    private val categoryList: MutableList<String> = mutableListOf()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemCategoryBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as CategoryAdapter.ViewHolder).bind(categoryList[position])
    }

    override fun getItemCount(): Int = categoryList.size

    inner class ViewHolder(private val viewBinding: ItemCategoryBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(category: String) {

            if (category == DefaultCategoryUtils.INC_OTHERS)
                viewBinding.deleteCategory.gone()
            else
                viewBinding.deleteCategory.visible()
            viewBinding.tvCategoryName.text = category
            if (transactionType == TransactionType.Income)
                viewBinding.ivCategoryIcon.background =
                    ContextCompat.getDrawable(context, DefaultCategoryUtils.getCategoryIcon(category, TransactionType.Income))
            else if (transactionType == TransactionType.Expense)
                viewBinding.ivCategoryIcon.background =
                    ContextCompat.getDrawable(context, DefaultCategoryUtils.getCategoryIcon(category, TransactionType.Expense))
            viewBinding.deleteCategory.setOnSafeClickListener {
                onCategoryDeleteClicked(category, categoryList)
            }
        }
    }

    fun addList(list: List<String>) {
        if (list.isNotEmpty()) {
            val newIndex = categoryList.size
            val newItemsCount = list.size
            if (categoryList.addAll(list)) notifyItemRangeInserted(newIndex, newItemsCount)
        }
        categoryList.clear()
        categoryList.addAll(list)
        notifyDataSetChanged()
    }
}