package com.arajangstudio.jajanhub_partner.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.arajangstudio.jajanhub_partner.R
import com.arajangstudio.jajanhub_partner.ui.auth.LoginActivity
import com.arajangstudio.jajanhub_partner.utils.PermissionUtils
import com.arajangstudio.jajanhub_partner.utils.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.hilt.android.AndroidEntryPoint


class SplashScreenActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var userPreferences: UserPreferences
    private lateinit var remoteConfig: FirebaseRemoteConfig

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
        setContentView(R.layout.activity_splash_screen)
        userPreferences = UserPreferences(this)
        auth = FirebaseAuth.getInstance()
        auth.setLanguageCode("id")

        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        checkForUpdate()

    }




    private fun setUpLocationListener() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
    }

    override fun onStart() {
        super.onStart()
        when {
            PermissionUtils.isAccessFineLocationGranted(this) -> {
                when {
                    PermissionUtils.isLocationEnabled(this) -> {
                        setUpLocationListener()
                    }
                    else -> {
                        PermissionUtils.showGPSNotEnabledDialog(this)
                    }
                }
            }
            else -> {
                PermissionUtils.requestAccessFineLocationPermission(
                    this,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun checkForUpdate() {

        val appVersion: String = getAppVersion(this)
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                val minimunVersion =
                    remoteConfig.getString("min_version_jajanhub_partner")
                val latestVersion =
                    remoteConfig.getString("latest_version_jajanhub_partner")




                if (!TextUtils.isEmpty(latestVersion) && !TextUtils.isEmpty(appVersion) && checkMandateVersionApplicable(
                        getAppVersionWithoutAlphaNumeric(latestVersion),
                        getAppVersionWithoutAlphaNumeric(appVersion)
                    )
                ) {
                    onUpdateNeeded(true)

                }
                else if (!TextUtils.isEmpty(minimunVersion) && !TextUtils.isEmpty(appVersion) && !TextUtils.equals(
                        minimunVersion,
                        appVersion
                    )
                ) {

                    onUpdateNeeded(false)
                }else {
                    moveForward()
                }
            }
        }
    }


    private fun checkMandateVersionApplicable(
        minVersion: String,
        appVersion: String
    ): Boolean {
        return try {
            val minVersionInt = minVersion.toInt()
            val appVersionInt = appVersion.toInt()
            minVersionInt > appVersionInt
        } catch (exp: NumberFormatException) {
            false
        }
    }

    private fun getAppVersion(context: Context): String {
        var result: String? = ""
        try {
            result = context.packageManager
                .getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("TAG", e.message.toString())
        }
        return result ?: ""
    }

    private fun getAppVersionWithoutAlphaNumeric(result: String): String {
        var versionStr = ""
        versionStr = result.replace(".", "")
        return versionStr
    }

    private fun onUpdateNeeded(isMandatoryUpdate: Boolean) {
        val dialogBuilder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            .setTitle(getString(R.string.update_app))
            .setCancelable(false)
            .setMessage(if (isMandatoryUpdate) getString(R.string.dialog_update_available_message) else "Halo, sekarang tersedia versi baru.")
            .setPositiveButton(getString(R.string.update_now))

            { _, _ ->
                openAppOnPlayStore(this, "com.arajangstudio.jajanhub_partner")
            }

        if (!isMandatoryUpdate) {
            dialogBuilder.setNegativeButton(getString(R.string.later)) { dialog, _ ->
                moveForward()
                dialog?.dismiss()
            }.create()
        }
        val dialog: AlertDialog = dialogBuilder.create()
        dialog.show()
    }

    private fun moveForward() {
        var currentUser = auth.currentUser
        Handler().postDelayed({
            if(currentUser?.uid.isNullOrEmpty()) {

                val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()

            }else{
                val intent = Intent(this@SplashScreenActivity, StartupActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000)
    }

    private fun openAppOnPlayStore(ctx: Context, package_name: String?) {
        var package_name = package_name
        if (package_name == null) {
            package_name = ctx.packageName
        }
        val uri = Uri.parse("market://details?id=$package_name")
        openURI(ctx, uri, "Play Store not found in your device")
    }

    private fun openURI(
        ctx: Context,
        uri: Uri?,
        error_msg: String?
    ) {
        val i = Intent(Intent.ACTION_VIEW, uri)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        if (ctx.packageManager.queryIntentActivities(i, 0).size > 0) {
            ctx.startActivity(i)
        } else if (error_msg != null) {
            Toast.makeText(this, error_msg, Toast.LENGTH_SHORT).show()
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

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
    }
}
