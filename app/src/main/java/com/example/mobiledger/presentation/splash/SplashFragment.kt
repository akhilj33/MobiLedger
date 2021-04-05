package com.example.mobiledger.presentation.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.databinding.FragmentSplashBinding
import com.example.mobiledger.presentation.OneTimeObserver

class SplashFragment : BaseFragment<FragmentSplashBinding, SplashNavigator>(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels {viewModelFactory}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isUserSignedIn()
        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.isUserSignedInLiveData.observe(viewLifecycleOwner, OneTimeObserver{ isSignedIn ->
            if(isSignedIn) navigator?.navigateAuthToHomeScreen()
            else navigator?.navigateSplashToLoginScreen()
        })
    }

    companion object {
        fun newInstance() = SplashFragment()
    }

}