package com.example.mobiledger.presentation.onBoarding

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledger.R
import com.example.mobiledger.databinding.ItemOnboardingCarouselBinding
import com.example.mobiledger.domain.entities.OnBoardingCarouselEntity

class OnBoardingCarouselAdapter(
    private val carouselEntityList: List<OnBoardingCarouselEntity>
) : RecyclerView.Adapter<OnBoardingCarouselAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemOnboardingCarouselBinding.inflate(layoutInflater, parent, false))
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun getItemCount(): Int {
        return carouselEntityList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = carouselEntityList[position]
        holder.binding.carouselHeadingTv.text = context.resources.getString(item.carouselTitle)
        holder.binding.carouselSubHeadingTv.text =
            context.resources.getString(item.carouselSubTitle)

        when (item.position) {
            0 -> holder.binding.carouselIv.setImageResource(R.drawable.app_logo)
            1 -> holder.binding.carouselIv.setImageResource(R.drawable.budget_icon_black)
            2 -> holder.binding.carouselIv.setImageResource(R.drawable.calendar_icon)
            3 -> holder.binding.carouselIv.setImageResource(R.drawable.cancel_icon)
            4 -> holder.binding.carouselIv.setImageResource(R.drawable.delete_white)
        }
    }

    inner class ViewHolder(var binding: ItemOnboardingCarouselBinding) :
        RecyclerView.ViewHolder(binding.root)
}