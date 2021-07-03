package com.example.mobiledger.presentation.onBoarding

import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseDialogFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.databinding.FragmentTermsAndConditionBinding


class TermsAndConditionFragment :
    BaseDialogFragment<FragmentTermsAndConditionBinding, BaseNavigator>(R.layout.fragment_terms_and_condition) {

    companion object {

        fun newInstance() = TermsAndConditionFragment()
    }
}