package com.example.mobiledger.presentation.onBoarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.extention.disable
import com.example.mobiledger.common.extention.enable
import com.example.mobiledger.databinding.FragmentTermsAndConditionBinding


class TermsAndConditionFragment :
    BaseFragment<FragmentTermsAndConditionBinding, OnBoardingNavigator>(R.layout.fragment_terms_and_condition) {

    private val viewModel: TermsAndConditionViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
        viewBinding.btnAccept.disable()
    }

    override fun isBottomNavVisible(): Boolean = false

    private fun setOnClickListener() {
        viewBinding.checkBoxTAndC.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                viewBinding.btnAccept.enable()
            else
                viewBinding.btnAccept.disable()
        }

        viewBinding.btnAccept.setOnClickListener {
            viewModel.acceptTermsAndCondition()
            navigator?.navigateToAuthScreen()
        }
    }

    companion object {

        fun newInstance() = TermsAndConditionFragment()
    }
}