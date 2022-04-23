package com.arajangstudio.jajanhub_partner.ui.merchant

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arajangstudio.jajanhub_partner.R
import com.arajangstudio.jajanhub_partner.data.remote.models.*
import com.arajangstudio.jajanhub_partner.databinding.ActivityEditMerchantBinding
import com.arajangstudio.jajanhub_partner.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.kroegerama.imgpicker.BottomSheetImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditMerchantActivity : AppCompatActivity(), BottomSheetImagePicker.OnImagesSelectedListener {

    lateinit var binding: ActivityEditMerchantBinding
    lateinit var menuMerchantAdapter: MenuMerchantAdapter
    lateinit var facilityMerchantAdapter: FacilityMerchantAdapter
    private val viewModel: MerchantViewModel by viewModels()
    private var photos: ArrayList<Photo> = arrayListOf()
    private var menus: ArrayList<Menu> = arrayListOf()
    private var facilities: ArrayList<Facilities> = arrayListOf()
    var myList: ArrayList<Uri> = arrayListOf()
    private val menuList = ArrayList<Menus>()
    private val facilityList = ArrayList<FacilityAll>()
    private val selectedFacilityList = ArrayList<Facility>()
    private val selectedMenuList = ArrayList<Menus>()
    private lateinit var dialog: AlertDialog
    private lateinit var storageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMerchantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.title = null
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val storage = Firebase.storage
        storageRef = storage.getReference("uploads/menus")

        dialog = Utils.getAlertDialog(
            this, R.layout.custom_dialog,
            setCancellationOnTouchOutside = false
        )

        facilities.clear()
        menus.clear()
        selectedFacilityList.clear()
        selectedMenuList.clear()

        val extras = intent.extras
        if (extras != null) {
            val merchantUUID = extras.getString("merchant_uuid")!!

            val handler = CoroutineExceptionHandler { _, exception ->
                println("CoroutineExceptionHandler got $exception")
            }
            lifecycleScope.launch(handler) {
                viewModel.setSelected(merchantUUID)
                viewModel.getDetailMerchant().collect {
                    populateMerchant(it)
                }
            }
        }





        binding.imageContainer.setOnClickListener {
            BottomSheetImagePicker.Builder(getString(R.string.file_provider))
                .multiSelect(1, 3)                  //user has to select 3 to 6 images
                .multiSelectTitles(
                    R.plurals.pick_multi,           //"you have selected <count> images
                    R.plurals.pick_multi_more,      //"You have to select <min-count> more images"
                    R.string.pick_multi_limit       //"You cannot select more than <max> images"
                )
                .peekHeight(R.dimen.peekHeight)     //peek height of the bottom sheet
                .columnSize(R.dimen.columnSize)     //size of the columns (will be changed a little to fit)
                .requestTag("multi")                //tag can be used if multiple pickers are used
                .show(supportFragmentManager)
        }

    }

    private fun populateMerchant(merchant: Merchant){

        merchant.menu_photos.forEach {
            val iv = LayoutInflater.from(this).inflate(
                R.layout.item_image,
                binding.imageContainer, false
            ) as ImageView
            iv.id = binding.imageContainer.childCount
            binding.imageContainer.addView(iv)
            binding.imageContainer.background = null

            Glide.with(this)
                .load(it.photo)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.bitmapTransform(RoundedCorners(14)))
                .into(iv)
        }

        binding.btnSave.setOnClickListener {
                createMenu(merchant.uuid)
        }

        merchant.facilities.forEach {
          selectedFacilityList.add(it)
        }

        merchant.menus.forEach {
            selectedMenuList.add(it)
        }

        loadFacilityMerchant()
        loadMenuMerchant()

    }

    private fun loadMenuMerchant() {
        lifecycleScope.launch {
            menuList.clear()
            menus.clear()
            viewModel.getMenus().collectLatest { it ->
                it.forEach { dt ->
                    var selected = false
                    selectedMenuList.forEach {
                        if(it.menu_id == dt.menu_id){
                            selected = true
                        }
                    }

                    val menu = Menus(dt.menu_id, dt.menu_category_id, dt.merchant_menu_id, dt.title, dt.category, dt.status,dt.count, selected)
                    menuList.add(menu)
                }

                menuList.sortByDescending { it.isSelected }
                menuMerchantAdapter = MenuMerchantAdapter(menuList)

                val mLayoutManager: RecyclerView.LayoutManager =
                    GridLayoutManager(this@EditMerchantActivity, 2)

                binding.rvMenu.apply {
                    layoutManager = mLayoutManager
                    setHasFixedSize(true)
                    adapter = menuMerchantAdapter
                    doOnPreDraw { startPostponedEnterTransition() }
                }
                binding.searchView.setOnQueryTextListener(object :
                    SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        menuMerchantAdapter.filter.filter(newText)
                        return false
                    }
                })


            }
        }
    }

    private fun loadFacilityMerchant() {
        lifecycleScope.launch {
            facilityList.clear()
            facilities.clear()
            viewModel.getFacilities().collectLatest {

                it.forEach { dt ->
                    var selected = false
                    selectedFacilityList.forEach {
                        if(it.facility_id == dt.id){
                            selected = true
                        }
                    }

                    val facility = FacilityAll(dt.id, dt.facility, selected)
                    facilityList.add(facility)
                }

                facilityMerchantAdapter = FacilityMerchantAdapter(facilityList)

                val mLayoutManager: RecyclerView.LayoutManager =
                    GridLayoutManager(this@EditMerchantActivity, 2)

                binding.rvFacility.apply {
                    layoutManager = mLayoutManager
                    setHasFixedSize(true)
                    adapter = facilityMerchantAdapter
                    doOnPreDraw { startPostponedEnterTransition() }
                }

                binding.progressCircular.visibility = View.GONE
            }
        }
    }


    private fun createMenu(
        merchant_uuid: String
    ) {
        dialog.show()
                        val gson = Gson()
                        menuList.forEach {
                            if (it.isSelected) {
                                menus.add(Menu(it.menu_id))
                            }
                        }

                        facilityList.forEach {
                            if (it.isSelected) {
                                facilities.add(Facilities(it.id))
                            }
                        }


                        val jsonMenus = gson.toJson(menus)

                        val jsonFacilities = gson.toJson(facilities)
                        val jsonPhotos = gson.toJson(photos)
                        lifecycleScope.launch {
                            viewModel.createMenuMerchant(
                                merchant_uuid,
                                jsonMenus.toString(),
                                jsonFacilities.toString(),
                                jsonPhotos.toString(),
                            )
                            dialog.dismiss()
                            photos.clear()
                            val dialog = BottomSheetDialog(this@EditMerchantActivity)
                            val view = layoutInflater.inflate(R.layout.bottomsheet_done, null)
                            dialog.setCancelable(false)
                            dialog.setContentView(view)
                            dialog.show()
                            val btnDone = view.findViewById<Button>(R.id.btnDone)
                            btnDone.setOnClickListener {
                                dialog.dismiss()
                                finish()
                            }
                        }
    }

    fun getFileExtension(uri: Uri?): String? {
        val contentResolver = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    override fun onImagesSelected(uris: List<Uri>, tag: String?) {
        binding.imageContainer.removeAllViews()
        myList.clear()
        photos.clear()
        uris.forEach { uri ->
            myList.add(uri)
            val iv = LayoutInflater.from(this).inflate(
                R.layout.item_image,
                binding.imageContainer, false
            ) as ImageView
            iv.id = binding.imageContainer.childCount
            binding.imageContainer.addView(iv)
            binding.imageContainer.background = null

            Glide.with(this)
                .load(uri)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.bitmapTransform(RoundedCorners(14)))
                .into(iv)

            iv.setOnClickListener {
                binding.imageContainer.removeView(it)
                if (binding.imageContainer.childCount == 0) {
                    binding.imageContainer.background =
                        ContextCompat.getDrawable(this, R.drawable.ic_add_image)
                }

            }
        }

    }
}