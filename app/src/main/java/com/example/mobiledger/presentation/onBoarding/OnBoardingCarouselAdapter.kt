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
            0 -> holder.binding.carouselIv.setImageResource(R.drawable.home_ss)
            1 -> holder.binding.carouselIv.setImageResource(R.drawable.budget_ss)
            2 -> holder.binding.carouselIv.setImageResource(R.drawable.stats_ss)
            3 -> holder.binding.carouselIv.setImageResource(R.drawable.profile_ss)
            4 -> holder.binding.carouselIv.setImageResource(R.drawable.budget_template_ss)
            5 -> holder.binding.carouselIv.setImageResource(R.drawable.category_ss)
        }
    }

    inner class ViewHolder(var binding: ItemOnboardingCarouselBinding) :
        RecyclerView.ViewHolder(binding.root)
}