package com.arajangstudio.jajanhub_partner.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.arajangstudio.jajanhub_partner.BuildConfig
import com.arajangstudio.jajanhub_partner.R
import com.arajangstudio.jajanhub_partner.databinding.ActivityMainBinding
import com.arajangstudio.jajanhub_partner.ui.home.HomeViewModel
import com.arajangstudio.jajanhub_partner.ui.profile.ProfileActivity
import com.arajangstudio.jajanhub_partner.utils.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var activityMainBinding: ActivityMainBinding
    private val viewModel: HomeViewModel by viewModels()
    lateinit var userPreferences: UserPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        setSupportActionBar(activityMainBinding.toolbar)
        activityMainBinding.appBar.outlineProvider = null
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        auth = FirebaseAuth.getInstance()
        auth.setLanguageCode("id")

        Firebase.messaging.subscribeToTopic("jajanhubPartner")
        userPreferences = UserPreferences(this)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                lifecycleScope.launch {
                    viewModel.updateToken(
                        auth.currentUser!!.uid, token
                    )
                }
            }
        }



    }


    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.right_nav, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    companion object{
        const val  ACTION_STOP_FOREGROUND = "${BuildConfig.APPLICATION_ID}.stopforeground"
    }
}