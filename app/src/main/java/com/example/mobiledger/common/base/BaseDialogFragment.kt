package com.example.mobiledger.common.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.activityViewModels
import com.example.mobiledger.R
import com.example.mobiledger.common.di.DependencyProvider
import com.example.mobiledger.common.extention.gone
import com.example.mobiledger.common.extention.visible
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.presentation.NormalObserver
import com.example.mobiledger.presentation.main.MainActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber

abstract class BaseDialogFragment<B : ViewDataBinding, NV : BaseNavigator>(
    @LayoutRes private val layoutId: Int, private val statusBarColor: BaseFragment.StatusBarColor? = null
) : BottomSheetDialogFragment() {
    //This is nullable as Fragments outlive their views.
    private var _viewBinding: B? = null

    protected val viewModelFactory = DependencyProvider.provideViewModelFactory()
    val activityViewModel: MainActivityViewModel by activityViewModels()

    protected var firebaseAnalytics: FirebaseAnalytics? = null

    /*This property is only valid between onCreateView and onDestroyView.
      This object returns _binding but thanks to !!(non-null asserted)
      we don't have to use ?(safe operator) everywhere in the code.*/
    val viewBinding get() = _viewBinding!!

    private val errorTimeOut: Long = 5000

    protected var navigator: NV? = null

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = activity
        if (activity is BaseActivity<*, *>) {
            try {
                navigator = activity.getFragmentNavigator() as NV
            } catch (e: Exception) {
                Timber.e("Navigator Initialisation Issue")
            }
        }
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
        setStatusBarColor()
        observeInternetState()
    }

    override fun onResume() {
        super.onResume()
        setBottomSheetBehaviour()
    }

    private fun setBottomSheetBehaviour() {
        // Fixes Bottomsheet's state to Expanded from Collapsed
        (dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED

        // Makes BottomSheet Non-Draggable
        (dialog as? BottomSheetDialog)?.behavior?.isDraggable = false

        // Makes BottomSheet Non-Cancelable
//        val touchOutsideView = dialog?.window?.decorView?.findViewById<View>(com.google.android.material.R.id.touch_outside)
//        touchOutsideView?.setOnSafeClickListener(null)
    }

    private fun setStatusBarColor() {
        if (statusBarColor != null) {
            dialog?.window?.statusBarColor =
                ContextCompat.getColor(requireContext(), statusBarColor.color)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //garbage collection
        _viewBinding = null
    }

    /*---------------------------------------Analytics--------------------------------------*/

    protected fun logEvent(msg: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, msg)
        firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }
    /*------------------------------------Snack Bar error ----------------------------------------*/

    protected open fun getSnackBarErrorView(): SnackViewErrorBinding? = null

    protected fun showSnackBarErrorView(message: String, isVanishing: Boolean) {
        getSnackBarErrorView()?.apply {
            root.visible()
            tvWarning.text = message
        }

        if (isVanishing) {
            Handler(Looper.getMainLooper()).postDelayed({
                hideSnackBarErrorView()
            }, errorTimeOut)

        }
    }

    protected fun hideSnackBarErrorView(forceHide: Boolean = false) {
        val isInternetAvailable: Boolean =
            activityViewModel.isInternetAvailableLiveData.value?.peekContent()?.current ?: true
        if (!forceHide && isInternetAvailable) {
            getSnackBarErrorView()?.root?.gone()
        } else if (forceHide) {
            getSnackBarErrorView()?.root?.gone()
        }
    }

    private fun observeInternetState() {
        activityViewModel.isInternetAvailableLiveData.observe(this, NormalObserver { state ->
            if (state.current && !state.previous) {
                hideSnackBarErrorView()
            } else if (!state.current) {
                showSnackBarErrorView(getString(R.string.device_offline_error_message), false)
            }
        })
    }
}
