package com.example.mobiledger.presentation.statsdetail

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.common.extention.setOnSafeClickListener
import com.example.mobiledger.common.utils.DateUtils.getDateInMMMMyyyyFormat
import com.example.mobiledger.common.utils.JsonUtils.convertJsonStringToObject
import com.example.mobiledger.common.utils.JsonUtils.convertToJsonString
import com.example.mobiledger.databinding.FragmentStatsDetailBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.presentation.OneTimeObserver
import java.util.*

class StatsDetailFragment : BaseFragment<FragmentStatsDetailBinding, BaseNavigator>(R.layout.fragment_stats_detail, StatusBarColor.BLUE) {

    private val viewModel: StatsDetailViewModel by viewModels { viewModelFactory }
    private val statsDetailAdapter: StatsDetailAdapter by lazy { StatsDetailAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            getString(CATEGORY)?.let {
                viewModel.categoryList = convertJsonStringToObject(it)?: emptyList()
            }
            getLong(AMOUNT).let {
                viewModel.amount = it
            }
            getSerializable(MONTH_YEAR)?.let {
                viewModel.monthYear = it as Calendar
            }
        }
    }

    override fun isBottomNavVisible(): Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        setUpObservers()
        setOnClickListeners()
        initUI()
        viewModel.getCategoryListDetails(false)
    }

    private fun setOnClickListeners() {
        viewBinding.btnBack.setOnSafeClickListener {
            activity?.onBackPressed()
        }
    }

    private fun initUI() {
        val headerName = if (viewModel.categoryList.size > 1 ) requireContext().getString(R.string.others)
                         else viewModel.categoryList[0]
        viewBinding.monthHeader.text = requireContext().getString(
            R.string.stats_detail_header,
            headerName,
            getDateInMMMMyyyyFormat(viewModel.monthYear)
        )
        initHeaderCardView()
    }

    private fun initHeaderCardView() {
        viewBinding.headerText.apply {
            if (viewModel.amount >= 0L) {
                text = requireContext().getString(
                    R.string.stats_detail_header_income, viewModel.getAbsoluteStringAmount(),
                    viewModel.categoryList.joinToString(), viewModel.getDate()
                )
                setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.colorGreen))
            } else {
                text = requireContext().getString(
                    R.string.stats_detail_header_expense, viewModel.getAbsoluteStringAmount(),
                    viewModel.categoryList.joinToString(), viewModel.getDate()
                )
                setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.colorDarkRed))
            }
        }
    }

    private fun setUpObservers() {
        viewModel.statsDetailViewItemListLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            statsDetailAdapter.addItemList(it)
        })

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showSwipeRefresh()
            } else {
                hideSwipeRefresh()
            }
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            showSnackBarErrorView(it.message ?: getString(it.resID), false)
        })

        viewModel.authErrorLiveData.observe(viewLifecycleOwner, OneTimeObserver {
//            navigateToLoginScreen() todo
        })
    }

    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

    override fun swipeRefreshLayout(): SwipeRefreshLayout {
        return viewBinding.swipeRefreshLayout
    }

    override fun refreshView() {
        hideSnackBarErrorView()
        viewModel.reloadData(true)
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity)
        viewBinding.rvStatsdDetail.apply {
            layoutManager = linearLayoutManager
            adapter = statsDetailAdapter
        }
    }

    companion object {
        private const val CATEGORY = "CATEGORY_NAME"
        private const val AMOUNT = "CATEGORY_AMOUNT"
        private const val MONTH_YEAR = "MONTH_YEAR"
        fun newInstance(categoryList: List<String>, amount: Long, monthYear: Calendar) =
            StatsDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(CATEGORY, convertToJsonString(categoryList))
                    putLong(AMOUNT, amount)
                    putSerializable(MONTH_YEAR, monthYear)
                }
            }
    }
}