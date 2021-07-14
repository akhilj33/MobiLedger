package com.example.mobiledger.presentation.aboutUs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.databinding.FragmentAboutUsBinding
import com.example.mobiledger.presentation.onBoarding.OnBoardingNavigator


class AboutUsFragment : BaseFragment<FragmentAboutUsBinding, AboutUsNavigator>(R.layout.fragment_about_us, StatusBarColor.BLUE) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnCLickListener()
        Glide.with(this).load(R.raw.akj).circleCrop().into(viewBinding.imgDev1)
        Glide.with(this).load(R.raw.anant).circleCrop().into(viewBinding.imgDev2)

//        navigator?.sendEmail(
//            EmailEntity(
//                email = emailItem.emailId,
//                subject = emailSubject,
//                bodyText = emailExtraText,
//                attachmentPath = emailItem.documentUri
//            )
//        )
    }

    override fun isBottomNavVisible(): Boolean = false

    private fun setOnCLickListener() {

        viewBinding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

        viewBinding.logoFb1.setOnClickListener {
            val webIntent: Intent = Uri.parse("https://www.facebook.com/akj.iet.33").let { webpage ->
                Intent(Intent.ACTION_VIEW, webpage)
            }
            startActivity(webIntent)
        }

        viewBinding.logoLinkedin1.setOnClickListener {
            val webIntent: Intent = Uri.parse("https://www.linkedin.com/in/akhil-jain-279382121/").let { webpage ->
                Intent(Intent.ACTION_VIEW, webpage)
            }
            startActivity(webIntent)
        }
        viewBinding.logoGithub1.setOnClickListener {
            val webIntent: Intent = Uri.parse("https://github.com/akhilj33").let { webpage ->
                Intent(Intent.ACTION_VIEW, webpage)
            }
            startActivity(webIntent)
        }
        viewBinding.logoinsta1.setOnClickListener {
            val webIntent: Intent = Uri.parse("https://www.instagram.com/akhilj333/").let { webpage ->
                Intent(Intent.ACTION_VIEW, webpage)
            }
            startActivity(webIntent)
        }

        viewBinding.logoFb2.setOnClickListener {
            val webIntent: Intent = Uri.parse("https://www.facebook.com/anantramanindia").let { webpage ->
                Intent(Intent.ACTION_VIEW, webpage)
            }
            startActivity(webIntent)
        }

        viewBinding.logoLinkedin2.setOnClickListener {
            val webIntent: Intent = Uri.parse("https://www.linkedin.com/in/anantramanindia/").let { webpage ->
                Intent(Intent.ACTION_VIEW, webpage)
            }
            startActivity(webIntent)
        }
        viewBinding.logoGithub2.setOnClickListener {
            val webIntent: Intent = Uri.parse("https://github.com/Anant-Raman").let { webpage ->
                Intent(Intent.ACTION_VIEW, webpage)
            }
            startActivity(webIntent)
        }
        viewBinding.logoinsta2.setOnClickListener {
            val webIntent: Intent = Uri.parse("https://www.instagram.com/anantraman/").let { webpage ->
                Intent(Intent.ACTION_VIEW, webpage)
            }
            startActivity(webIntent)
        }
    }

    companion object {

        fun newInstance() = AboutUsFragment()

    }
}