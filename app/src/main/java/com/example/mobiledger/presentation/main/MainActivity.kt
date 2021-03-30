package com.example.mobiledger.presentation.main

import android.os.Bundle
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseActivity
import com.example.mobiledger.databinding.ActivityMainBinding


class MainActivity :
    BaseActivity<ActivityMainBinding, MainActivityNavigator>(R.layout.activity_main) {

//    private val viewModel: MainActivityViewModel by viewModels { viewModelFactory }
    private var mainActivityNavigator: MainActivityNavigator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        mainActivityNavigator =
            MainActivityNavigator(
                getFragmentContainerID(),
                supportFragmentManager
            )

        mainActivityNavigator?.navigateToLoginScreen()

    }

    override fun getFragmentNavigator(): MainActivityNavigator? = mainActivityNavigator

    private fun getFragmentContainerID(): Int {
        return viewBinding.fragmentContainer.id
    }


    override fun onDestroy() {
        super.onDestroy()
        mainActivityNavigator = null
    }


}