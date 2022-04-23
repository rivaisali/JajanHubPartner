package com.arajangstudio.jajanhub_partner.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.arajangstudio.jajanhub_partner.databinding.ActivityRegisterBinding
import com.arajangstudio.jajanhub_partner.ui.MainActivity
import com.arajangstudio.jajanhub_partner.utils.UserPreferences
import com.arajangstudio.jajanhub_partner.utils.Utils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var registerBinding: ActivityRegisterBinding
    lateinit var userPreferences: UserPreferences
    private val viewModel: AuthViewModel by viewModels()
    var uuid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)
        auth= FirebaseAuth.getInstance()
        val user = auth.currentUser
        userPreferences = UserPreferences(this)


        registerBinding.btnRegister.setOnClickListener {
            Utils.hideSoftKeyBoard(this, registerBinding.root.rootView)
            var fullName = registerBinding.txtFullName.text.trim().toString()
            var email = registerBinding.txtEmail.text.trim().toString()
            var phone = user?.phoneNumber
            var password = "12345678"
            var uuid = user?.uid

            if (fullName.isEmpty()
                || fullName.isBlank()
                || email.isEmpty()
                || email.isBlank()
            ) {
                Snackbar.make(registerBinding.constraintLayout, "Nama atau Email tidak boleh kosong", Snackbar.LENGTH_SHORT).show()
            }else{
                registerBinding.progressBar.visibility = View.VISIBLE
                register(fullName, phone, email, password, password, uuid)

            }

        }

    }


    private fun register(full_name:String?,phone:String?,email:String?,password:String?,password_confirm:String?, uuid:String?){
        val handler = CoroutineExceptionHandler { _, exception ->
            Snackbar.make(registerBinding.constraintLayout, exception.localizedMessage, Snackbar.LENGTH_SHORT).show()
            registerBinding.progressBar.visibility = View.GONE

        }
        lifecycleScope.launch(handler) {
          viewModel.create(full_name!!, phone!!, email!!, password!!, password_confirm!!, uuid!!, "3")
          registerBinding.progressBar.visibility = View.GONE
          lifecycleScope.launch {
                userPreferences.save(auth.currentUser!!.uid)
            }
            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
      }


    }


}