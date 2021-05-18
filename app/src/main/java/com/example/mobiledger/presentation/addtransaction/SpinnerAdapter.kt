package com.example.mobiledger.presentation.addtransaction

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.mobiledger.R
import com.example.mobiledger.databinding.SpinnerRowItemBinding

class SpinnerAdapter(context: Context): ArrayAdapter<String>(context, R.layout.spinner_row_item) {

    private val items: MutableList<String> = mutableListOf()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): String? {
        return items[position]
    }

    fun addItems(list: List<String>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    private fun initView(
        position: Int, view: View?,
        parent: ViewGroup
    ): View {
        // It is used to set our custom view.
        var convertView: View? = view
        if (convertView == null) {
            convertView =
                SpinnerRowItemBinding.inflate(LayoutInflater.from(context), parent, false).root
        }
        val tvName = convertView.findViewById<TextView>(R.id.tvName)
        val currentItem: String? = getItem(position)

        currentItem?.let {
            tvName.text = it
        }
        return convertView
    }
}