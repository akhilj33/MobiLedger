package com.example.mobiledger.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.mobiledger.common.di.DependencyProvider
import com.example.mobiledger.presentation.main.MainActivityViewModel
import timber.log.Timber

abstract class BaseDialogFragment<B : ViewDataBinding, NV : BaseNavigator>(
    @LayoutRes private val layoutId: Int, private val statusBarColor: BaseFragment.StatusBarColor? = null
) : DialogFragment() {
    //This is nullable as Fragments outlive their views.
    private var _viewBinding: B? = null

    protected val viewModelFactory = DependencyProvider.provideViewModelFactory()
    val activityViewModel: MainActivityViewModel by activityViewModels()

    /*This property is only valid between onCreateView and onDestroyView.
      This object returns _binding but thanks to !!(non-null asserted)
      we don't have to use ?(safe operator) everywhere in the code.*/
    val viewBinding get() = _viewBinding!!

    protected var navigator: NV? = null

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
    }


    private fun setStatusBarColor() {
        if (statusBarColor != null) {
            dialog?.window?.statusBarColor =
                ContextCompat.getColor(requireContext(), statusBarColor.color)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity = activity
        if (activity is BaseActivity<*, *>) {
            try {
                navigator = activity.getFragmentNavigator() as NV
            } catch (e: Exception) {
                Timber.e("Navigator Initialisation Issue")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //garbage collection
        _viewBinding = null
    }
}