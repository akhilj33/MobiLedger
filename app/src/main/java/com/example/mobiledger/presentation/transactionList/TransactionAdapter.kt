package com.example.mobiledger.presentation.transactionList

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.mobiledger.R
import com.example.mobiledger.common.utils.DateUtils
import com.example.mobiledger.databinding.HomeTransactionItemBinding
import com.example.mobiledger.domain.enums.TransactionType
import com.example.mobiledger.presentation.home.TransactionData

class TransactionAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context

    private val transactionList: MutableList<TransactionData> = mutableListOf()
    private val viewBinderHelper = ViewBinderHelper()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(HomeTransactionItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TransactionAdapter.ViewHolder).bind(transactionList[position])

    }

    override fun getItemCount(): Int = transactionList.size

    inner class ViewHolder(private val viewBinding: HomeTransactionItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        fun bind(item: TransactionData) {
            viewBinding.apply {
                deleteSwipeAction.setOnClickListener { }
                viewBinderHelper.setOpenOnlyOne(true)
                viewBinderHelper.bind(swipelayout, item.id)
                viewBinderHelper.closeLayout(item.id)
                tvTransactionName.text = item.name
                tvAmount.text = item.amount
                tvCategory.text = item.category
                tvTime.text = DateUtils.getDateInDDMMMMyyyyFormat(item.transactionEntity.transactionTime)
                ivCategoryIcon.background = ContextCompat.getDrawable(context, item.categoryIcon)
                if (item.transactionType == TransactionType.Income) tvAmount.setTextColor(
                    ContextCompat.getColorStateList(context, R.color.colorGreen)
                )
                else if (item.transactionType == TransactionType.Expense) tvAmount.setTextColor(
                    ContextCompat.getColorStateList(context, R.color.colorDarkRed)
                )
            }
        }
    }

    fun addList(list: List<TransactionData>) {
        if (list.isNotEmpty()) {
            val newIndex = transactionList.size
            val newItemsCount = list.size
            if (transactionList.addAll(list)) notifyItemRangeInserted(newIndex, newItemsCount)
        }
        transactionList.clear()
        transactionList.addAll(list)
        notifyDataSetChanged()
    }


}
