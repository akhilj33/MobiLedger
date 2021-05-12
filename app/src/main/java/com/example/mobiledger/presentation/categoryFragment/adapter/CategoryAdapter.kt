package com.example.mobiledger.presentation.categoryFragment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledger.databinding.ItemCategoryBinding

class CategoryAdapter(
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
        fun bind(item: String) {
            if (item == "Others")
                viewBinding.deleteCategory.visibility = View.GONE
            else
                viewBinding.deleteCategory.visibility = View.VISIBLE
            viewBinding.tvCategoryName.text = item
            viewBinding.deleteCategory.setOnClickListener {
                onCategoryDeleteClicked(item, categoryList)
            }
        }
    }

    fun addItemList(list: List<String>) {
        if (list.isNotEmpty()) {
            val newIndex = categoryList.size
            val newItemsCount = list.size
            if (categoryList.addAll(list)) notifyItemRangeInserted(newIndex, newItemsCount)
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