package com.arajangstudio.jajanhub_partner.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.arajangstudio.jajanhub_partner.R
import com.arajangstudio.jajanhub_partner.ui.auth.AuthViewModel
import com.arajangstudio.jajanhub_partner.ui.auth.RegisterActivity
import com.arajangstudio.jajanhub_partner.utils.UserPreferences
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StartupActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var userPreferences: UserPreferences
    var uuid: String = ""
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }

        setContentView(R.layout.activity_startup)

        auth = FirebaseAuth.getInstance()
        auth.setLanguageCode("id")


        userPreferences = UserPreferences(this)
        lifecycleScope.launch {
            userPreferences.uuid.collect { dt ->
                if (dt == null) {
                    uuid = ""
                } else {
                    uuid = dt
                }
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            val token = it.result
            lifecycleScope.launch {
                viewModel.updateToken(auth.uid!!, token)
            }
        }

            var currentUser = auth.currentUser
                    Handler().postDelayed({
                        if(uuid != ""){
                            val intent = Intent(this@StartupActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                        }else{
                            viewModel.validate(currentUser!!.uid)
                        }
                    }, 2000)
                    isValidate()
                }


    private fun isValidate(){
        viewModel.status.observe(this) {
            when(it){
                true -> {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }
                false -> {
                    startActivity(Intent(applicationContext, RegisterActivity::class.java))
                    finish()
                }
            }
        }
    }


    private fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
        val win: Window = activity.window
        val winParams: WindowManager.LayoutParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

}