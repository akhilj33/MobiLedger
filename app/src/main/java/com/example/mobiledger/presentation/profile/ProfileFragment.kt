package com.example.mobiledger.presentation.profile

import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.utils.ConstantUtils
import com.example.mobiledger.databinding.FragmentProfileBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.domain.entities.UserEntity
import com.example.mobiledger.presentation.OneTimeObserver


class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileNavigator>(R.layout.fragment_profile) {

    private val viewModel: ProfileViewModel by viewModels { viewModelFactory }

    private var notificationManager: NotificationManager? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        notificationManager =
//            activity?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        createNotificationChannel(
//            "com.ebookfrenzy.notifydemo.news",
//            "NotifyDemo News",
//            "Example News Channel")

        notificationManager = activity?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(
            notificationManager,
            getString(R.string.channel_transaction_description),
            ConstantUtils.CHANNEL_ID_TRANSACTION
        )

        setOnClickListener()
        setObserver()
        viewModel.fetchUserData()
    }

    override fun isBottomNavVisible(): Boolean = false
    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeErrorView

    private fun setObserver() {
        viewModel.userFromFirestoreResult.observe(viewLifecycleOwner, {
            updateProfileUI(it)
        })

        viewModel.loadingState.observe(viewLifecycleOwner, Observer {
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
    }

    private fun setOnClickListener() {
        viewBinding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

        viewBinding.imgEdit.setOnClickListener {
            navigator?.navigateToEditProfileScreen()
        }

        viewBinding.textCategory.setOnClickListener {
            navigator?.navigateToCategoryFragmentScreen()
        }

        viewBinding.textContactUs.setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                demoNotification()
            }
        }
    }

    private fun updateProfileUI(user: UserEntity) {
        viewBinding.displayName.text = user.userName ?: ""
        viewBinding.emailTv.text = user.emailId ?: ""
        viewBinding.contactNumTv.text = user.phoneNo ?: ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun demoNotification() {
        sendNotification(notificationManager, ConstantUtils.CHANNEL_ID_TRANSACTION, "Hello", "How are you?")
        sendNotification(notificationManager, ConstantUtils.CHANNEL_ID_TRANSACTION, "Thanos", "Why is Gamora?")
    }

//    private fun createNotificationChannel(
//        id: String, name: String,
//        description: String
//    ) {
//
//        val importance = NotificationManager.IMPORTANCE_HIGH
//        val channel = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationChannel(id, name, importance)
//        } else {
//            TODO("VERSION.SDK_INT < O")
//        }
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            channel.description = description
//
//            channel.enableLights(true)
//            channel.lightColor = Color.RED
//            channel.enableVibration(true)
//            channel.vibrationPattern =
//                longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
//            notificationManager?.createNotificationChannel(channel)
//        }
//    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    fun sendNotification() {
//
//        val notificationID = 101
//
//        val channelID = "com.ebookfrenzy.notifydemo.news"
//
//        val notification = Notification.Builder(
//            requireContext(),
//            channelID
//        )
//            .setContentTitle("Example Notification")
//            .setContentText("This is an  example notification.")
//            .setSmallIcon(android.R.drawable.ic_dialog_info)
//            .setChannelId(channelID)
//            .build()
//
//        notificationManager?.notify(notificationID, notification)
//    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}