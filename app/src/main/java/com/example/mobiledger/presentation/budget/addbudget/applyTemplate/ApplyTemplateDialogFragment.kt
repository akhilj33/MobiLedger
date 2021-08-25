package com.example.mobiledger.presentation.budget.addbudget.applyTemplate

import android.os.Bundle
import android.view.View
import android.view.View.OnLayoutChangeListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseDialogFragment
import com.example.mobiledger.common.extention.gone
import com.example.mobiledger.common.extention.showAlertDialog
import com.example.mobiledger.common.extention.visible
import com.example.mobiledger.databinding.ApplyTemplateDialogLayoutBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.presentation.OneTimeObserver
import com.example.mobiledger.presentation.budgetTemplate.BudgetTemplateNavigator
import com.google.android.material.bottomsheet.BottomSheetDialog


class ApplyTemplateDialogFragment :
    BaseDialogFragment<ApplyTemplateDialogLayoutBinding, BudgetTemplateNavigator>(R.layout.apply_template_dialog_layout) {

    private val viewModel: ApplyTemplateViewModel by viewModels { viewModelFactory }

    private val applyTemplateRecyclerViewAdapter: ApplyTemplateRecyclerViewAdapter by lazy {
        ApplyTemplateRecyclerViewAdapter(
            onTemplateItemClick
        )
    }

    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            getString(KEY_MONTH)?.let {
                viewModel.month = it
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObserver()
        initRecyclerView()
        viewModel.getBudgetTemplateList()
    }

    private fun setUpObserver() {
        viewModel.budgetTemplateList.observe(viewLifecycleOwner, {
            it.let {
                applyTemplateRecyclerViewAdapter.addList(it.peekContent())
                if (it.peekContent().isNotEmpty()) {
                    viewBinding.tvNoTemplate.gone()
                } else {
                    viewBinding.tvNoTemplate.visible()
                }
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, {
            it.let {
                if (it)
                    viewBinding.rvTemplateListProgressBar.visible()
                else
                    viewBinding.rvTemplateListProgressBar.gone()
            }
        })

        viewModel.templateApplied.observe(viewLifecycleOwner, OneTimeObserver {
            activityViewModel.templateApplied()
            dismiss()
        })

    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity)
        viewBinding.rvBudgetTemplateList
            .apply {
                layoutManager = linearLayoutManager
                adapter = applyTemplateRecyclerViewAdapter
            }
    }

    private val onTemplateItemClick = fun(id: String) {
        viewModel.selectedId = id
        activity?.showAlertDialog(
            getString(R.string.apply_template),
            getString(R.string.apply_template_msg),
            getString(R.string.yes),
            getString(R.string.no),
            onCancelButtonClick,
            onContinueClick
        )
    }

    private val onCancelButtonClick = {}

    private val onContinueClick = {
        viewModel.applyTemplate(viewModel.selectedId)
    }

//    override fun onResume() {
//        super.onResume()
//        addGlobaLayoutListener(view)
//    }

    private fun addGlobaLayoutListener(view: View?) {
        view?.addOnLayoutChangeListener(object : OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                setPeekHeight(v.measuredHeight)
                v.removeOnLayoutChangeListener(this)
            }
        })
    }

    fun setPeekHeight(peekHeight: Int) {
        (dialog as BottomSheetDialog).behavior.peekHeight = peekHeight
//        val behavior = getBottomSheetBehaviour() ?: return
//        behavior.peekHeight = peekHeight
    }

//    private fun getBottomSheetBehaviour(): BottomSheetBehavior<*>? {
//        val layoutParams = (view?.parent as View).layoutParams as ConstraintLayout.LayoutParams
//        val behavior = layoutParams.behavior
//        if (behavior != null && behavior is BottomSheetBehavior<*>) {
//            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
//            return behavior
//        }
//        return null
//    }

    companion object {
        private const val KEY_MONTH = "MONTH"
        fun newInstance(month: String) = ApplyTemplateDialogFragment()
            .apply {
                arguments = Bundle().apply {
                    putString(KEY_MONTH, month)
                }
            }
    }
}