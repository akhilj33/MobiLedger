package com.example.mobiledger.common.base

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.mobiledger.common.di.DependencyProvider

abstract class BaseActivity<B : ViewDataBinding, out NV>(
    @LayoutRes
    private val layoutId: Int
) : AppCompatActivity() {

    lateinit var viewBinding: B

    protected val viewModelFactory = DependencyProvider.provideViewModelFactory()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DataBindingUtil.setContentView(
            this, layoutId
        )
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        val newOverride = Configuration(newBase?.resources?.configuration)
        if (newOverride.fontScale > 1.0f) {
            newOverride.fontScale = 1.0f
            applyOverrideConfiguration(newOverride)
        }
    }

    abstract fun getFragmentNavigator(): NV?
}
