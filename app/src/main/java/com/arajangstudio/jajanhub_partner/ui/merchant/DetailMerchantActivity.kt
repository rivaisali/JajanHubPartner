package com.arajangstudio.jajanhub_partner.ui.merchant

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.arajangstudio.jajanhub_partner.R
import com.arajangstudio.jajanhub_partner.data.remote.models.Merchant
import com.arajangstudio.jajanhub_partner.databinding.ActivityDetailMerchantBinding
import com.arajangstudio.jajanhub_partner.ui.adapter.LoadingStateAdapter
import com.arajangstudio.jajanhub_partner.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DetailMerchantActivity : AppCompatActivity() {

    lateinit var activityDetailMerchantBinding: ActivityDetailMerchantBinding
    lateinit var reviewAdapter: ReviewAdapter
    private val viewModel: MerchantViewModel by viewModels()
    private lateinit var dialog: AlertDialog
    private lateinit var uuid:String
    private  val photos = arrayListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDetailMerchantBinding = ActivityDetailMerchantBinding.inflate(layoutInflater)

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {

            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }



        if (Build.VERSION.SDK_INT >= 21) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }
        setContentView(activityDetailMerchantBinding.root)

        setSupportActionBar(activityDetailMerchantBinding.toolbar)

        activityDetailMerchantBinding.appBarLayout.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, offset ->
            val upArrow = ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_back, null)
            if (offset < -200) {
                upArrow!!.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP)
                supportActionBar!!.setHomeAsUpIndicator(upArrow)

            } else {
                upArrow!!.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP)
                supportActionBar!!.setHomeAsUpIndicator(upArrow)
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            }
        })

        dialog = Utils.getAlertDialog(
            this, R.layout.custom_dialog,
            setCancellationOnTouchOutside = false
        )

        dialog.show()

        reviewAdapter = ReviewAdapter()
        val extras = intent.extras
        if (extras != null) {
            uuid = extras.getString(EXTRA_ID)!!
            if(uuid != null){
                lifecycleScope.launch {
                    viewModel.setSelected(uuid)
                    viewModel.getDetailMerchant().collect {
                        populateMerchant(it)
                        dialog.dismiss()
                    }
                }
            }
        }

        setupReviewAdapter()
        loadListReview()
        activityDetailMerchantBinding.swipeRefresh.setOnRefreshListener {
            activityDetailMerchantBinding.otherInfoMerchant.linearLayout.removeAllViews()
            activityDetailMerchantBinding.menuMerchant.imageContainer.removeAllViews()
            if(activityDetailMerchantBinding.swipeRefresh.isRefreshing) {
                lifecycleScope.launch {
                    viewModel.setSelected(uuid)
                    viewModel.getDetailMerchant().collect {
                        populateMerchant(it)

                    }
                }
                reviewAdapter = ReviewAdapter()
                setupReviewAdapter()
                loadListReview()
                activityDetailMerchantBinding.swipeRefresh.isRefreshing = false
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun populateMerchant(merchant: Merchant){
        activityDetailMerchantBinding.tvMerchantName.text = merchant.merchant_name
        if(merchant.badge == "verified")
            activityDetailMerchantBinding.tvMerchantName.
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_verified,0,0,0)

        activityDetailMerchantBinding.tvLocationName.text = merchant.location_name
        activityDetailMerchantBinding.collapseToolbar.title = merchant.merchant_name
        activityDetailMerchantBinding.infoMerchant.tvPhone.text = " "+merchant.phone
        activityDetailMerchantBinding.infoMerchant.tvTimeOpen.text = " "+merchant.schedule
        activityDetailMerchantBinding.infoMerchant.tvMerchantAddress.text = " "+merchant.location_address
        activityDetailMerchantBinding.tvRatting.text = merchant.rating_total
        activityDetailMerchantBinding.tvCountReview.text = merchant.review_total
        activityDetailMerchantBinding.rattingMerchant.tvRattingTaste.text = merchant.rating_flavor
        activityDetailMerchantBinding.rattingMerchant.tvRattingAtmospher.text = merchant.rating_atmosphere
        activityDetailMerchantBinding.rattingMerchant.tvRattingPriceTaste.text = merchant.rating_price_vs_flavor
        activityDetailMerchantBinding.rattingMerchant.tvRattingService.text = merchant.rating_service
        activityDetailMerchantBinding.rattingMerchant.tvRattingClean.text = merchant.rating_cleanliness

        merchant.facilities.forEach {
            val iv = LayoutInflater.from(this).inflate(R.layout.item_facility,
                activityDetailMerchantBinding.otherInfoMerchant.linearLayout, false) as TextView
            activityDetailMerchantBinding.otherInfoMerchant.linearLayout.addView(iv)
            iv.text = " "+it.facility
        }

        if(merchant.menu_photos.isNullOrEmpty()){
            activityDetailMerchantBinding.menuMerchant.ivEmpty.visibility = View.VISIBLE
        }else{
            for (i in 0 until merchant.menu_photos.size) {
                val iv = LayoutInflater.from(this).inflate(R.layout.item_menu_image,
                    activityDetailMerchantBinding.menuMerchant.imageContainer, false) as ImageView
                activityDetailMerchantBinding.menuMerchant.imageContainer.addView(iv)
                photos.add(merchant.menu_photos[i].photo)
                Glide.with(this)
                    .load(merchant.menu_photos[i].photo)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(
                        RequestOptions
                            .placeholderOf(R.drawable.placeholder_background)
                            .error(R.drawable.placeholder_background)
                            .apply(RequestOptions.bitmapTransform(RoundedCorners(14)))

                    )
                    .centerCrop()
                    .into(iv)
                iv.setOnClickListener {
                        Utils.goToDetail(this, photos, i)

                }
            }
            activityDetailMerchantBinding.menuMerchant.ivEmpty.visibility = View.GONE
        }

        Glide.with(this)
            .load(merchant.photo)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(
                RequestOptions
                    .placeholderOf(R.drawable.placeholder_background)
                    .error(R.drawable.placeholder_background)
            )
            .into(activityDetailMerchantBinding.imgBackdrop)

        activityDetailMerchantBinding.infoMerchant.btnGotoLocation.setOnClickListener {
            val gmmIntentUri =  Uri.parse("geo:0,0?q=${merchant.location_latitude},${merchant.location_longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)

        }

    }

    private fun loadListReview() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Snackbar.make(activityDetailMerchantBinding.rootView, exception.localizedMessage, Snackbar.LENGTH_SHORT).show()
        }
        lifecycleScope.launch(handler) {
            viewModel.getReviewMerchants().collectLatest {
                reviewAdapter.submitData(lifecycle, it)
            }
        }
    }

    private fun setupReviewAdapter() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        activityDetailMerchantBinding.reviewMerchant.rvReviews.apply {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
            adapter = reviewAdapter
            doOnPreDraw { startPostponedEnterTransition() }
        }

        activityDetailMerchantBinding.reviewMerchant.rvReviews.adapter =
            reviewAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter { }
            )

        reviewAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Loading) {
                activityDetailMerchantBinding.reviewMerchant.shimmerLayout.visibility = View.VISIBLE
                activityDetailMerchantBinding.reviewMerchant.shimmerLayout.startShimmer()
            } else if (loadState.append.endOfPaginationReached) {
                if (reviewAdapter.itemCount < 1) {
                    activityDetailMerchantBinding.reviewMerchant.layoutEmpty.visibility = View.VISIBLE
                    activityDetailMerchantBinding.reviewMerchant.shimmerLayout.visibility = View.GONE
                    activityDetailMerchantBinding.reviewMerchant.shimmerLayout.stopShimmer()
                }
            }else {
                activityDetailMerchantBinding.reviewMerchant.layoutEmpty.visibility = View.GONE
                activityDetailMerchantBinding.reviewMerchant.shimmerLayout.visibility = View.GONE
                activityDetailMerchantBinding.reviewMerchant.shimmerLayout.stopShimmer()
                val errorState = when {
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                    loadState.refresh is LoadState.Error -> {
                        loadState.refresh as LoadState.Error
                    }
                    else -> null
                }
                errorState?.let {

                }
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.share_nav, menu)
        return super.onCreateOptionsMenu(menu)
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
        const val EXTRA_ID = "extra_id"
    }
}