package com.example.mobiledger.presentation.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.databinding.FragmentLoginBinding


class LoginFragment : BaseFragment<FragmentLoginBinding, BaseNavigator>(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels { viewModelFactory }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loginUserViaEmail()
    }

    companion object {
        fun newInstance() = LoginFragment()
    }


}