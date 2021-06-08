package com.example.mobiledger.presentation.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.extention.gone
import com.example.mobiledger.common.extention.visible
import com.example.mobiledger.common.showBiometricSystemPrompt
import com.example.mobiledger.databinding.FragmentSplashBinding
import com.example.mobiledger.presentation.OneTimeObserver

class SplashFragment : BaseFragment<FragmentSplashBinding, SplashNavigator>(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels { viewModelFactory }
    private var isBiometricActive = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservers()
        setOnClickListener()
    }

    override fun isBottomNavVisible(): Boolean = false

    private fun setUpObservers() {

        viewModel.isBiometricIdEnabled.observe(viewLifecycleOwner, OneTimeObserver { isBiometricEnabled ->
            isBiometricActive = isBiometricEnabled
        })

        viewModel.isUserSignedInLiveData.observe(viewLifecycleOwner, OneTimeObserver { isSignedIn ->
            if (isSignedIn) {
                if (isBiometricActive)
                    authenticateWithFingerprint()
                else
                    navigator?.launchDashboard()
            } else
                navigator?.navigateToAuthScreen()
        })

        activityViewModel.userLogoutLiveData.observe(viewLifecycleOwner, OneTimeObserver { isLoggedOut ->
            if (isLoggedOut) {
                navigator?.navigateToAuthScreen()
            }
        })
    }

    private fun setOnClickListener() {
        viewBinding.btnLoginUsingCred.setOnClickListener {
            //todo: Logout
            navigator?.navigateToAuthScreen()
        }

        viewBinding.btnRetry.setOnClickListener {
            authenticateWithFingerprint()
            viewBinding.btnRetry.gone()
            viewBinding.btnLoginUsingCred.gone()
        }

        viewBinding.btnLogout.setOnClickListener {
            activityViewModel.logout()
        }
    }

    private fun authenticateWithFingerprint() {
        val title = requireContext().getString(R.string.to_login_biometric)
        val cancel = requireContext().getString(R.string.cancel)
        activity?.showBiometricSystemPrompt(
            title,
            cancel,
            onAuthenticated,
            onAuthenticationCancelled
        )
    }

    private val onAuthenticated = {
        moveToHome()
    }

    private val onAuthenticationCancelled = {
        viewBinding.btnRetry.visible()
        viewBinding.btnLoginUsingCred.visible()
        viewBinding.btnLogout.visible()
    }

    private fun moveToHome() {
        navigator?.launchDashboard()
    }

    companion object {
        fun newInstance() = SplashFragment()
    }

}