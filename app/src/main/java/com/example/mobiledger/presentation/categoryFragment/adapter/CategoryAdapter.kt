package com.example.mobiledger.presentation.categoryFragment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledger.databinding.ItemCategoryBinding

class CategoryAdapter(private val categoryList: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context

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
            viewBinding.tvCategoryName.text = item
            viewBinding.deleteCategory.setOnClickListener {

            }
        }
    }
}