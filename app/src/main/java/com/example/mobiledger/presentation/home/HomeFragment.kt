package com.example.mobiledger.presentation.home

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.extention.setOnSafeClickListener
import com.example.mobiledger.common.utils.showAddTransactionDialogFragment
import com.example.mobiledger.common.utils.showTransactionDetailDialogFragment
import com.example.mobiledger.databinding.FragmentHomeBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.enums.TransactionDetailScreenSource
import com.example.mobiledger.presentation.OneTimeObserver

class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeNavigator>(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels { viewModelFactory }

    private val homeAdapter: HomeAdapter by lazy { HomeAdapter(onDeleteItemClick, onTransactionItemClick, onAllTransactionClicked) }

    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

    override fun swipeRefreshLayout(): SwipeRefreshLayout {
        return viewBinding.swipeRefreshLayout
    }

    override fun refreshView() {
        hideSnackBarErrorView()
        viewModel.reloadData(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        setOnClickListener()
        setUpObservers()
        viewModel.getHomeData(false)
    }

    private fun setUpObservers() {
        activityViewModel.updateTransactionResultLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            refreshView()
        })

        viewModel.homeViewItemListLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            homeAdapter.addItemList(it)
        })

        viewModel.userNameLiveData.observe(viewLifecycleOwner, {
            viewBinding.tvHello.text = getString(R.string.hello_name, it)
        })

        viewModel.monthNameLiveData.observe(viewLifecycleOwner, {
            viewBinding.monthNavigationBar.tvMonth.text = it
        })

        viewModel.deleteTransactionLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            refreshView()
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

        activityViewModel.addTransactionResultLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            logEvent(getString(R.string.transaction_added))
            refreshView()
        })
    }

    private fun setOnClickListener() {
        viewBinding.apply {
            btnAddTransaction.setOnSafeClickListener {
                showAddTransactionDialogFragment(requireActivity().supportFragmentManager)
            }
            monthNavigationBar.leftArrow.setOnSafeClickListener { handleLeftClick() }
            monthNavigationBar.rightArrow.setOnSafeClickListener { handleRightClick() }
        }
        viewBinding.horizontalGuideline2
    }

    private fun handleRightClick() {
        if (!viewModel.isCurrentMonth()) {
            viewModel.getNextMonthData()
        }
        handleRightArrowState()
    }

    private fun handleLeftClick() {
        viewModel.getPreviousMonthData()
        handleRightArrowState()
    }

    private fun handleRightArrowState() {
        val color = if (!viewModel.isCurrentMonth())
            R.color.prussianBlue
        else
            R.color.colorGrey

        ImageViewCompat.setImageTintList(
            viewBinding.monthNavigationBar.rightArrow,
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))
        )
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity)
        viewBinding.rvHome.apply {
            layoutManager = linearLayoutManager
            adapter = homeAdapter
        }
    }

    private val onDeleteItemClick = fun(transactionEntity: TransactionEntity, position: Int) {
        viewModel.deleteTransaction(transactionEntity, position)
    }

    private val onAllTransactionClicked = fun() {
        navigator?.navigateToTransactionFragmentScreen(viewModel.transList, viewModel.currentMonth)
    }

    private val onTransactionItemClick = fun(transactionEntity: TransactionEntity) {
        showTransactionDetailDialogFragment(
            transactionEntity,
            requireActivity().supportFragmentManager,
            TransactionDetailScreenSource.HomeScreen
        )
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}