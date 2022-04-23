package com.arajangstudio.jajanhub_partner.ui.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.arajangstudio.jajanhub_partner.R
import com.arajangstudio.jajanhub_partner.databinding.ActivityCompleteBinding
import com.arajangstudio.jajanhub_partner.databinding.ActivitySettingBinding
import com.arajangstudio.jajanhub_partner.ui.setting.bottomsheet.BottomSheetFacility
import com.arajangstudio.jajanhub_partner.ui.setting.bottomsheet.BottomSheetMenu
import com.arajangstudio.jajanhub_partner.ui.setting.bottomsheet.BottomSheetMerchant
import com.arajangstudio.jajanhub_partner.ui.setting.bottomsheet.BottomSheetPhotoMenu
import com.arajangstudio.jajanhub_partner.ui.setting.fragment.ListMenuFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""
        supportActionBar!!.elevation = 0F



        binding.btnCategoryMenu.setOnClickListener {
            val extras = intent.extras
            if (extras != null) {
           val merchantUUID = extras.getString("merchant_uuid")!!

            val bundle = Bundle()
            bundle.putString("merchant_uuid", merchantUUID)

            val bottomSheet = BottomSheetMenu()
            bottomSheet.arguments = bundle
            bottomSheet.show(supportFragmentManager,"TAG1")
            }
        }

        binding.btnFacility.setOnClickListener {
            val extras = intent.extras
            if (extras != null) {
                val merchantUUID = extras.getString("merchant_uuid")!!

                val bundle = Bundle()
                bundle.putString("merchant_uuid", merchantUUID)

                val bottomSheet = BottomSheetFacility()
                bottomSheet.arguments = bundle
                bottomSheet.show(supportFragmentManager,"TAG1")
            }
        }

        binding.btnMerchantImage.setOnClickListener {

            val extras = intent.extras
            if (extras != null) {
                val merchantUUID = extras.getString("merchant_uuid")!!

                val bundle = Bundle()
                bundle.putString("merchant_uuid", merchantUUID)

                val bottomSheet = BottomSheetMerchant()
                bottomSheet.arguments = bundle
                bottomSheet.show(supportFragmentManager,"TAG1")
            }
        }

        binding.btnPhotoMenu.setOnClickListener {
            val extras = intent.extras
            if (extras != null) {
                val merchantUUID = extras.getString("merchant_uuid")!!

                val bundle = Bundle()
                bundle.putString("merchant_uuid", merchantUUID)

                val bottomSheet = BottomSheetPhotoMenu()
                bottomSheet.arguments = bundle
                bottomSheet.show(supportFragmentManager,"TAG1")
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}