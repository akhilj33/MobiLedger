package com.example.mobiledger.presentation.profile


import android.os.Bundle
import android.view.View
import androidx.biometric.BiometricManager
import androidx.fragment.app.viewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.showAlertDialog
import com.example.mobiledger.common.showBiometricSystemPrompt
import com.example.mobiledger.common.showToast
import com.example.mobiledger.common.utils.AnimationDialogUtils
import com.example.mobiledger.common.utils.BiometricDeviceState
import com.example.mobiledger.common.utils.canAuthenticateUsingBiometrics
import com.example.mobiledger.databinding.FragmentProfileBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.domain.entities.UserEntity
import com.example.mobiledger.presentation.NormalObserver
import com.example.mobiledger.presentation.OneTimeObserver


class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileNavigator>(R.layout.fragment_profile) {

    private val viewModel: ProfileViewModel by viewModels { viewModelFactory }

    private var enableBiometric = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSwitchCompact()
        enableBiometric()
        enablePushNotification()
        enableDailyReminder()
        setOnClickListener()
        setObserver()
        viewModel.fetchUserData()
    }

    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

    private fun setObserver() {
        viewModel.userFromFirestoreResult.observe(viewLifecycleOwner, {
            updateProfileUI(it)
        })

        viewModel.loadingState.observe(viewLifecycleOwner, {
            if (it) {
                viewBinding.profileProgressBar.visibility = View.VISIBLE
            } else {
                viewBinding.profileProgressBar.visibility = View.GONE
            }
        })

        viewModel.errorLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            when (it.viewErrorType) {
                ProfileViewModel.ViewErrorType.NON_BLOCKING -> {
                    showSnackBarErrorView(it.message ?: getString(it.resID), false)
                }
            }
        })

        activityViewModel.userLogoutLiveData.observe(viewLifecycleOwner, OneTimeObserver { isLoggedOut ->
            if (isLoggedOut) {
                navigator?.navigateToAuthScreen()
            }
        })
    }

    private fun setOnClickListener() {
        viewBinding.apply {
            imgEdit.setOnClickListener {
                navigator?.navigateToEditProfileScreen()
            }

            textCategory.setOnClickListener {
                navigator?.navigateToCategoryFragmentScreen()
            }

            textLogout.setOnClickListener {
                logout()
            }

            textBudgetTemplate.setOnClickListener {
                navigator?.navigateToBudgetTemplateFragment()
            }

            textAboutUs.setOnClickListener {
                navigator?.navigateToAboutUsFragment()
            }
        }
    }

    private fun logout() {
        activity?.showAlertDialog(
            getString(R.string.logout),
            getString(R.string.logout_message),
            getString(R.string.yes),
            getString(R.string.no),
            onCancelButtonClick,
            onContinueClick
        )
    }

    private fun updateProfileUI(user: UserEntity) {
        viewBinding.displayName.text = user.userName ?: ""
        viewBinding.emailTv.text = user.emailId ?: ""
        viewBinding.contactNumTv.text = user.phoneNo ?: ""
    }

    private fun initSwitchCompact() {
        viewModel.isPushNotificationEnabled.observe(
            viewLifecycleOwner,
            NormalObserver { isPushNotificationEnabled ->
                viewBinding.toggleBtnNotification.isChecked = isPushNotificationEnabled
            })
        viewModel.isPushNotificationEnabled()

        viewModel.isReminderEnabled.observe(
            viewLifecycleOwner,
            NormalObserver { isReminderEnabled ->
                viewBinding.toggleBtnReminder.isChecked = isReminderEnabled
            })
        viewModel.isReminderEnabled()

        val biometricManager = BiometricManager.from(requireContext())
        if (canAuthenticateUsingBiometrics(biometricManager) != BiometricDeviceState.BIOMETRIC_AVAILABLE) {
            viewBinding.toggleBtnBiometric.isChecked = false
            return
        }
        viewModel.isBiometricIdEnabled.observe(
            viewLifecycleOwner,
            NormalObserver { isBiometricEnabled ->
                viewBinding.toggleBtnBiometric.isChecked = isBiometricEnabled
            })
        viewModel.isBiometricEnabled()

    }

    private fun enableBiometric() {
        val biometricManager = BiometricManager.from(requireContext())
        viewBinding.toggleBtnBiometric.setOnClickListener {
            when (canAuthenticateUsingBiometrics(biometricManager)) {
                BiometricDeviceState.BIOMETRIC_AVAILABLE -> {
                    if (viewBinding.toggleBtnBiometric.isChecked) {
                        enableBiometric = true
                        authenticateWithFingerprint()
                    } else {
                        enableBiometric = false
                        authenticateWithFingerprint()
                    }
                }
                BiometricDeviceState.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    activity?.showToast(getString(R.string.biometric_available))
                    viewBinding.toggleBtnBiometric.isChecked = false
                }
                BiometricDeviceState.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    activity?.showToast(getString(R.string.biometric_unavailable))
                    viewBinding.toggleBtnBiometric.isChecked = false
                }
                BiometricDeviceState.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    activity?.showToast(getString(R.string.biometric_not_associated))
                    viewBinding.toggleBtnBiometric.isChecked = false
                }
            }
        }
    }

    private val onCancelButtonClick = {

    }

    private val onContinueClick = {
        activityViewModel.logout()
    }

    private fun authenticateWithFingerprint() {
        val title = requireContext().getString(R.string.to_confirm_biometric)
        val cancel = requireContext().getString(R.string.cancel)
        activity?.showBiometricSystemPrompt(
            title,
            cancel,
            onAuthenticated,
            onAuthenticationCancelled
        )
    }

    private val onAuthenticated = {
        viewModel.saveBiometricEnabled(enableBiometric)
        if (enableBiometric) {
            AnimationDialogUtils.animatedDialog(requireActivity(), R.layout.animation_dialog_layout, 1500)
        }
    }

    private val onAuthenticationCancelled = {
        viewBinding.toggleBtnBiometric.isChecked = !enableBiometric
    }

    private fun enablePushNotification() {
        viewBinding.toggleBtnNotification.setOnClickListener {
            if (viewBinding.toggleBtnNotification.isChecked) {
                viewModel.savePushNotificationEnabled(true)
            } else {
                viewModel.savePushNotificationEnabled(false)
            }
        }
    }

    private fun enableDailyReminder() {
        viewBinding.toggleBtnReminder.setOnClickListener {
            if (viewBinding.toggleBtnReminder.isChecked) {
                activityViewModel.activateDailyReminder(true)
                viewModel.saveReminderEnabled(true)
            } else {
                activityViewModel.activateDailyReminder(false)
                viewModel.saveReminderEnabled(false)
            }
            viewModel.isReminderEnabled()
        }
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}