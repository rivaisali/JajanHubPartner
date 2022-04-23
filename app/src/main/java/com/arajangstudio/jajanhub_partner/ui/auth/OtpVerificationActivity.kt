package com.arajangstudio.jajanhub_partner.ui.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.arajangstudio.jajanhub_partner.R
import com.arajangstudio.jajanhub_partner.databinding.ActivityOtpVerificationBinding
import com.arajangstudio.jajanhub_partner.ui.MainActivity
import com.arajangstudio.jajanhub_partner.ui.StartupActivity
import com.arajangstudio.jajanhub_partner.utils.GenericTextWatcher
import com.arajangstudio.jajanhub_partner.utils.UserPreferences
import com.arajangstudio.jajanhub_partner.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var otpVerificationBinding: ActivityOtpVerificationBinding
    lateinit var auth: FirebaseAuth
    lateinit var userPreferences: UserPreferences
    private lateinit var dialog: AlertDialog

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth=FirebaseAuth.getInstance()
        userPreferences = UserPreferences(this)
        val storedVerificationId=intent.getStringExtra("storedVerificationId")
        val phoneNumber=intent.getStringExtra("phoneNumber")

        dialog = Utils.getAlertDialog(
            this, R.layout.custom_dialog,
            setCancellationOnTouchOutside = false
        )



        otpVerificationBinding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(otpVerificationBinding.root)


        val timer = object: CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                otpVerificationBinding.tvTime.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                otpVerificationBinding.btnRetry.visibility = View.VISIBLE
                otpVerificationBinding.tvTime.text = ""
            }
        }
        timer.start()

        otpVerificationBinding.btnRetry.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


        otpVerificationBinding.tvNumber.text = phoneNumber

        var otp_textbox_one = otpVerificationBinding.otpEditBox1
        var otp_textbox_two = otpVerificationBinding.otpEditBox2
        var otp_textbox_three = otpVerificationBinding.otpEditBox3
        var otp_textbox_four = otpVerificationBinding.otpEditBox4
        var otp_textbox_five = otpVerificationBinding.otpEditBox5
        var otp_textbox_six = otpVerificationBinding.otpEditBox6

        val edit = arrayOf(otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four, otp_textbox_five, otp_textbox_six)

        otp_textbox_one.addTextChangedListener(GenericTextWatcher(otp_textbox_one, edit))
        otp_textbox_two.addTextChangedListener(GenericTextWatcher(otp_textbox_two, edit))
        otp_textbox_three.addTextChangedListener(GenericTextWatcher(otp_textbox_three, edit))
        otp_textbox_four.addTextChangedListener(GenericTextWatcher(otp_textbox_four, edit))
        otp_textbox_five.addTextChangedListener(GenericTextWatcher(otp_textbox_five, edit))
        otp_textbox_six.addTextChangedListener(GenericTextWatcher(otp_textbox_six, edit))

        otpVerificationBinding.btnVerification.setOnClickListener {
            dialog.show()
            var otp = "${otp_textbox_one.text}${otp_textbox_two.text}${otp_textbox_three.text}${otp_textbox_four.text}${otp_textbox_five.text}${otp_textbox_six.text}"
            val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                storedVerificationId.toString(), otp)
            signInWithPhoneAuthCredential(credential)
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    lifecycleScope.launch {
                        userPreferences.uuid.collect { dt ->
                            if(dt.isNullOrEmpty()){
                                startActivity(Intent(applicationContext, StartupActivity::class.java))
                                finish()
                            }else{
                                startActivity(Intent(applicationContext, MainActivity::class.java))
                                finish()
                            }

                        }
                    }
dialog.dismiss()
                } else {
// Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
// The verification code entered was invalid
                        dialog.dismiss()
                        Toast.makeText(this,"Kode OTP Tidak Valid",Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}