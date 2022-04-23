package com.arajangstudio.jajanhub_partner.ui.home

import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.arajangstudio.jajanhub_partner.R
import com.arajangstudio.jajanhub_partner.data.remote.models.Merchant
import com.arajangstudio.jajanhub_partner.databinding.FragmentHomeBinding
import com.arajangstudio.jajanhub_partner.ui.merchant.CompleteActivity
import com.arajangstudio.jajanhub_partner.ui.merchant.DetailMerchantActivity
import com.arajangstudio.jajanhub_partner.ui.merchant.MerchantActivity
import com.arajangstudio.jajanhub_partner.ui.merchant.MerchantAdapter
import com.arajangstudio.jajanhub_partner.ui.setting.SettingActivity
import com.arajangstudio.jajanhub_partner.utils.Utils
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class HomeFragment : Fragment() {

    lateinit var merchantAdapter: MerchantAdapter
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    lateinit var auth: FirebaseAuth
    private lateinit var dialog: AlertDialog
    private lateinit var stopwatch: Chronometer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return fragmentHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        auth.setLanguageCode("id")

        merchantAdapter = MerchantAdapter()
        stopwatch = Chronometer(context)

        dialog = Utils.getAlertDialog(
            requireContext(), R.layout.custom_dialog,
            setCancellationOnTouchOutside = false
        )

        loadMerchant()

        fragmentHomeBinding.btnAddMerchant.setOnClickListener {
            val intent = Intent(context, MerchantActivity::class.java)
            intent.putExtra("user_uid", auth.currentUser!!.uid)
            startActivity(intent)
        }

        BubbleShowCaseBuilder(requireActivity()) //Activity instance
            .title("Informasi") //Any title for the bubble view
            .description("Klik disini melihat detail dari usaha anda")
            .targetView(fragmentHomeBinding.tvMerchantName) //View to point out
            .textColor(Color.BLACK)
            .closeActionImage(ContextCompat.getDrawable(requireContext(), R.drawable.ic_closed))
            .backgroundColor(Color.parseColor("#EEEEEE")) //Bubble background color
            .show()
    }
    override fun onStart() {
        super.onStart()
        loadMerchant()

    }

    override fun onResume() {
        super.onResume()
        loadMerchant()
    }

    override fun onPause() {
        super.onPause()
        loadMerchant()
    }

    private fun loadMerchant() {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("CoroutineExceptionHandler got $exception")
        }

        lifecycleScope.launch(handler) {
            viewModel.setSelected(auth.currentUser!!.uid)
            viewModel.getMerchant().collect {

                if (!it.result) {
                    fragmentHomeBinding.btnAddMerchant.visibility = View.VISIBLE
                    fragmentHomeBinding.animationView.visibility = View.VISIBLE
                    fragmentHomeBinding.cardOpenClose.visibility = View.GONE
                    fragmentHomeBinding.tvInfo.text =
                        "Anda belum mempunyai usaha yang terdaftar, silahkan klik tombol Daftarkan Usaha dibawah ini"
                } else {
                    populateMerchant(it)
                    fragmentHomeBinding.btnAddMerchant.visibility = View.GONE
                    fragmentHomeBinding.tvInfo.visibility = View.GONE
                    fragmentHomeBinding.animationView.visibility = View.GONE
                    fragmentHomeBinding.tvTitleMerchant.visibility = View.VISIBLE
                    fragmentHomeBinding.tvRating.visibility = View.VISIBLE
                    fragmentHomeBinding.tvCountReview.visibility = View.VISIBLE
                    fragmentHomeBinding.tvRating.visibility = View.VISIBLE
                    fragmentHomeBinding.tvTitleMerchant.visibility = View.VISIBLE
                    fragmentHomeBinding.tvRating.visibility = View.VISIBLE
                }
                fragmentHomeBinding.progressCircular.visibility = View.GONE
            }
        }

    }

    private fun startTimer(time: String) {
        var isPlaying = false
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currentTimeOpen = dateFormat.parse("$date $time")
        val currentTime = Date()
        val timeCountInMilliSeconds = currentTime.time - currentTimeOpen.time
        val chronoMeter = fragmentHomeBinding.tvTime
        chronoMeter.base = SystemClock.elapsedRealtime() - timeCountInMilliSeconds
        if (!isPlaying) {
            chronoMeter.start()
            true
        } else {
            chronoMeter.stop()
            false
        }

    }


    private fun updateStatus(merchant_uuid: String, status: String) {
        lifecycleScope.launch {
            viewModel.updateStatuserchant(merchant_uuid, status).collect {
                loadMerchant()
                dialog.dismiss()
            }

        }
    }

    private fun populateMerchant(merchant: Merchant) {
        fragmentHomeBinding.tvMerchantName.text = merchant.merchant_name
        fragmentHomeBinding.tvRating.text = merchant.rating_total
        fragmentHomeBinding.tvCountReview.text = merchant.review_total + " Ulasan "

        if (merchant.complete == 0) {
            fragmentHomeBinding.tvCloseTitle.visibility = View.GONE
            fragmentHomeBinding.tvTime.visibility = View.GONE
            fragmentHomeBinding.cardOpenClose.visibility = View.GONE
            fragmentHomeBinding.tvInfoComplete.text =
                "Data usaha anda belum lengkap, silahkan lengkap terlebih dahulu dengan mengklik tombol lengkapi"
            fragmentHomeBinding.titleButton.text = " Lengkapi "
            fragmentHomeBinding.cardComplate.visibility = View.VISIBLE
        } else if (merchant.active == "deactivated" || merchant.active == "banned") {
            fragmentHomeBinding.tvCloseTitle.visibility = View.GONE
            fragmentHomeBinding.tvTime.visibility = View.GONE
            fragmentHomeBinding.cardOpenClose.visibility = View.GONE
            fragmentHomeBinding.tvInfoComplete.text =
                "Usaha anda belum diaktifkan atau mungkin dinonaktifkan admin"
            fragmentHomeBinding.cardComplate.visibility = View.GONE
        } else {
            fragmentHomeBinding.titleButton.visibility = View.VISIBLE
            fragmentHomeBinding.tvCloseTitle.visibility = View.VISIBLE
            fragmentHomeBinding.tvTime.visibility = View.VISIBLE
            fragmentHomeBinding.titleButton.text = " Ubah Profil "
            fragmentHomeBinding.cardComplate.visibility = View.GONE
            fragmentHomeBinding.tvInfoComplete.visibility = View.GONE
            fragmentHomeBinding.cardOpenClose.visibility = View.VISIBLE
            fragmentHomeBinding.tvInfo.visibility = View.GONE

        }

        if(merchant.status == "open" && merchant.recent_open_time == null){
            updateStatus(merchant.uuid, "close")
        }


        if (merchant.status == "close") {
            fragmentHomeBinding.tvTime.stop()
            fragmentHomeBinding.tvCloseTitle.text = ""
            fragmentHomeBinding.tvStatus.text = "Tekan dan Tahan untuk Mulai Jualan"
            fragmentHomeBinding.tvCloseTitle.setBackgroundResource(0)
            fragmentHomeBinding.tvTime.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )

        fragmentHomeBinding.cardOpenClose.setCardBackgroundColor(ContextCompat.getColor(
            requireContext(),
            R.color.colorAccent
        ))

        } else {
            startTimer(merchant.recent_open_time)
            fragmentHomeBinding.tvTime.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )

            fragmentHomeBinding.cardOpenClose.setCardBackgroundColor(ContextCompat.getColor(
                requireContext(),
                R.color.success
            ))

            fragmentHomeBinding.tvStatusOpen.text = "Waktu buka anda"
            fragmentHomeBinding.tvCloseTitle.text = " Anda sedang aktif jualan "
            fragmentHomeBinding.tvStatus.text = "Tekan dan tahan untuk Tutup Jualan"
            fragmentHomeBinding.tvCloseTitle.setBackgroundResource(R.drawable.open_rounded)
        }

        fragmentHomeBinding.tvMerchantName.setOnClickListener {
            val intent = Intent(context, DetailMerchantActivity::class.java)
            intent.putExtra(DetailMerchantActivity.EXTRA_ID, merchant.uuid)
            context?.startActivity(intent)
        }

        fragmentHomeBinding.titleButton.setOnClickListener {
                val intent = Intent(context, SettingActivity::class.java)
                intent.putExtra("merchant_uuid", merchant.uuid)
                startActivity(intent)
        }

        fragmentHomeBinding.cardComplate.setOnClickListener {
            val intent = Intent(context, CompleteActivity::class.java)
            intent.putExtra("merchant_uuid", merchant.uuid)
            context?.startActivity(intent)
        }

        val toVibrate = context?.getSystemService(VIBRATOR_SERVICE) as Vibrator

        fragmentHomeBinding.cardOpenClose.setOnLongClickListener {
            if (merchant.status == "open") {
                fragmentHomeBinding.progressBar.progressDrawable =
                    ContextCompat.getDrawable(requireContext(), R.drawable.close_progress_bar)
            } else {
                fragmentHomeBinding.progressBar.progressDrawable =
                    ContextCompat.getDrawable(requireContext(), R.drawable.open_progress_bar)
            }

            toVibrate.vibrate(10)
            object : CountDownTimer(2000, 1) {
                override fun onTick(millisUntilFinished: Long) {
                    if (!fragmentHomeBinding.cardOpenClose.isPressed) {
                        fragmentHomeBinding.progressBar.progress = 0
                        cancel()
                    } else {
                        fragmentHomeBinding.progressBar.progress =
                            1 + fragmentHomeBinding.progressBar.progress

                    }
                }

                override fun onFinish() {
                    fragmentHomeBinding.progressBar.progress = 0
                    dialog.show()
                    if (merchant.status == "open") {
                        updateStatus(merchant.uuid, "close")
                    } else {
                        updateStatus(merchant.uuid, "open")
                    }
                }
            }.start()
            true
        }
    }

}