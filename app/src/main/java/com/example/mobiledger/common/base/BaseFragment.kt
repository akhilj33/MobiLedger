package com.example.mobiledger.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.mobiledger.common.di.DependencyProvider
import com.example.mobiledger.common.showToast
import com.example.mobiledger.presentation.main.MainActivityViewModel
import timber.log.Timber

abstract class BaseFragment<B : ViewDataBinding, NV : BaseNavigator>(
    @LayoutRes private val layoutId: Int,
    private val statusBarColor: StatusBarColor? = null
) : Fragment() {

    private var _viewBinding: B? = null
    protected var navigator: NV? = null

        protected val viewModelFactory = DependencyProvider.provideViewModelFactory()
    protected val activityViewModel: MainActivityViewModel by activityViewModels()

    val viewBinding get() = _viewBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, backPressCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        viewBinding.lifecycleOwner = viewLifecycleOwner
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        initSwipeRefreshLayout()
//        initHelpAndSupport()
//        observeInternetState()
//        setBottomNavVisibility()
    }

//    private fun setBottomNavVisibility() {
//        if (isBottomNavVisible()) {
//            showBottomNav()
//        } else {
//            hideBottomNav()
//        }
//    }

//    protected fun showBottomNav() {
//        activityViewModel.showBottomNavigationView()
//    }
//
//    protected fun hideBottomNav() {
//        activityViewModel.hideBottomNavigationView()
//    }

//    private fun observeInternetState() {
//        activityViewModel.isInternetAvailableLiveData.observe(viewLifecycleOwner, NormalObserver {
//            if (it) {
//                hideSnackBarErrorView()
//            } else {
//                showSnackBarErrorView(getString(R.string.device_offline_error_message))
//            }
//        })
//    }

//    override fun onResume() {
//        super.onResume()
//        if (statusBarColor != null) {
//            viewBinding.root.changeStatusBarColor(requireActivity(), statusBarColor)
//        }
//        registerForAuthResult()
//    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity = activity
        if (activity is BaseActivity<*, *>) {
            try {
                @Suppress("UNCHECKED_CAST")
                navigator = activity.getFragmentNavigator() as NV
            } catch (e: Exception) {
                Timber.e("Activity Navigator should implement Fragment navigator")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

//    protected open fun isBottomNavVisible(): Boolean = true

    /*----------------------------------------Back Press----------------------------------------*/

    protected open fun onBackPressHandled(): Boolean = false

    private val backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!onBackPressHandled()) {
                isEnabled = false
                activity?.onBackPressed()
            }
        }
    }

    /*----------------------------------------Swipe Refresh----------------------------------------*/

//    private fun initSwipeRefreshLayout() {
//        swipeRefreshLayout()?.setOnRefreshListener {
//            refreshView()
//        }
//        swipeRefreshLayout()?.setColorSchemeColors(
//            ContextCompat.getColor(requireContext(), R.color.orange),
//            ContextCompat.getColor(requireContext(), R.color.darkRed),
//            ContextCompat.getColor(requireContext(), R.color.prussianBlue),
//            ContextCompat.getColor(requireContext(), R.color.darkYellow),
//            ContextCompat.getColor(requireContext(), R.color.green)
//        )
//    }
//
//    protected fun hideSwipeRefresh() {
//        when (swipeRefreshLayout()?.isRefreshing) {
//            true -> swipeRefreshLayout()?.isRefreshing = false
//        }
//    }
//
//    protected fun showSwipeRefresh() {
//        when (swipeRefreshLayout()?.isRefreshing) {
//            false -> swipeRefreshLayout()?.isRefreshing = true
//        }
//    }
//
//    open fun swipeRefreshLayout(): SwipeRefreshLayout? = null
//    open fun refreshView() = Unit

    /*----------------------------------------Toast----------------------------------------*/

    protected fun handleNativeToast(toast: NativeToastData) {
        toast.msg?.let { requireActivity().showToast(it) }
            ?: toast.msgRes?.let { requireActivity().showToast(getString(it)) }
    }

    /*------------------------------------Snack Bar error ----------------------------------------*/

//    protected open fun getSnackBarErrorView(): SnackErrorViewBinding? = null
//
//    protected fun showSnackBarErrorView(message: String) {
//        getSnackBarErrorView()?.apply {
//            root.visible()
//            tvWarning.text = message
//        }
//    }
//
//    protected fun hideSnackBarErrorView(forceHide: Boolean = false) {
//        val isInternetAvailable: Boolean =
//            activityViewModel.isInternetAvailableLiveData.value?.peekContent() ?: true
//        if (!forceHide && isInternetAvailable) {
//            getSnackBarErrorView()?.root?.gone()
//        } else if (forceHide) {
//            getSnackBarErrorView()?.root?.gone()
//        }
//    }

///*-----------------------------------Authentication Error-----------------------------------------*/
//
//    private var authResultCallback: ((Boolean) -> Unit)? = null
//
//    protected fun addAuthCallback(authCallback: (Boolean) -> Unit) {
//        authResultCallback = authCallback
//    }
//
//    private val delayToStartAuthFlow: Int = 3000
//    private var lastTimeAuthFlowStarted: Long = 0
//
//    protected fun navigateToLoginScreen() {
//        if (SystemClock.elapsedRealtime() - lastTimeAuthFlowStarted < delayToStartAuthFlow) {
//            return
//        }
//        lastTimeAuthFlowStarted = SystemClock.elapsedRealtime()
//        Handler(Looper.getMainLooper()).postDelayed({
//            navigator?.navigateToLoginScreen(RequestCode.REQUEST_CODE_AUTH_RESULT)
//        }, delayToStartAuthFlow.toLong())
//    }


/*----------------------------------------Status Bar----------------------------------------*/

    enum class StatusBarColor(@ColorRes val color: Int, val isLightColor: Boolean) {
//        BLUE(R.color.blue, isLightColor = false),
//        LIGHT_GREY(R.color.lightGrey, isLightColor = true),
//        TRANSPARENT(R.color.transparent, isLightColor = true),
//        WHITE(R.color.white, isLightColor = true),
//        PEACH(R.color.peach, isLightColor = false),
//        PALE_BLUE(R.color.paleBlue, isLightColor = false),
//        MID_GREY(R.color.midGrey, isLightColor = true)
    }
}