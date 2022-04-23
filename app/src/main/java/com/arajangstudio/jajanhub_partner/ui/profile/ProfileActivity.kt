package com.arajangstudio.jajanhub_partner.ui.profile

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.arajangstudio.jajanhub_partner.data.remote.models.Merchant
import com.arajangstudio.jajanhub_partner.databinding.ActivityProfileBinding
import com.arajangstudio.jajanhub_partner.ui.SplashScreenActivity
import com.arajangstudio.jajanhub_partner.ui.auth.AuthViewModel
import com.arajangstudio.jajanhub_partner.ui.home.HomeViewModel
import com.arajangstudio.jajanhub_partner.ui.setting.SettingActivity
import com.arajangstudio.jajanhub_partner.ui.setting.bottomsheet.BottomSheetSuggestion
import com.arajangstudio.jajanhub_partner.utils.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.net.URLEncoder


@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    lateinit var activityProfileBinding: ActivityProfileBinding
    lateinit var auth: FirebaseAuth
    private val viewModel: AuthViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityProfileBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(activityProfileBinding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""

        auth= FirebaseAuth.getInstance()
        userPreferences = UserPreferences(this)

        val user = auth.currentUser
        if(user!!.uid != ""){
            lifecycleScope.launch {
                viewModel.validate(user.uid)
            }
        }

        val handler = CoroutineExceptionHandler { _, exception ->
            println("CoroutineExceptionHandler got $exception")
        }
        lifecycleScope.launch(handler) {
            homeViewModel.setSelected(auth.currentUser!!.uid)
            homeViewModel.getMerchant().collect {
                populateView(it)
             }
            }

        activityProfileBinding.btnSuggestion.setOnClickListener {
            val bottomSheet = BottomSheetSuggestion()
            val bundle = Bundle()
            bundle.putString("user_uuid", user.uid)
            bottomSheet.arguments = bundle
            bottomSheet.show(supportFragmentManager,"TAG1")
        }


        activityProfileBinding.btnAbout.setOnClickListener {
            val url = "https://jajanhub.id"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        activityProfileBinding.btnLogout.setOnClickListener {
            auth.signOut()
            lifecycleScope.launch {
                userPreferences.clear()
            }

            lifecycleScope.launch {
                userPreferences.clearCurrentLocation()
            }

            val intent = Intent(this, SplashScreenActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        activityProfileBinding.btnReviewPlaystore.setOnClickListener {
            val uri: Uri = Uri.parse("market://details?id=com.arajangstudio.jajanhub_partner")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=com.arajangstudio.jajanhub_partner")))
            }
        }

        lifecycleScope.launch {
            viewModel.user.observe(this@ProfileActivity){
                activityProfileBinding.progressCircular.visibility = View.GONE
                activityProfileBinding.tvFullName.text = it.full_name
                activityProfileBinding.tvEmail.text = it.email
            }
        }
    }

    private fun populateView(merchant: Merchant) {
        activityProfileBinding.btnContactUs.setOnClickListener {
            contactUs(
                "6282293126799",
                "Halo Min, Saya dari *${merchant.merchant_name}* mau bertanya ?"
            )
        }

        activityProfileBinding.btnSetting.setOnClickListener {
            val intent = Intent(this@ProfileActivity, SettingActivity::class.java)
            intent.putExtra("merchant_uuid", merchant.uuid)
            startActivity(intent)
        }
    }

    private fun contactUs(number: String, message: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        val url = "https://api.whatsapp.com/send?phone="+ number +"&text=" + URLEncoder.encode(message, "UTF-8")
        intent.data = Uri.parse(url)
        intent.setPackage("com.whatsapp")
        System.out.println("napa"+intent.resolveActivity(packageManager))
        if (intent.resolveActivity(packageManager) == null) {
            Toast.makeText(this,
                "Silahkan install whatsapp terlebih dahulu.",
                Toast.LENGTH_SHORT).show()
            return
        }
        startActivity(intent)

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}