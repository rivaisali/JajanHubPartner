package com.arajangstudio.jajanhub_partner.ui.merchant

import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arajangstudio.jajanhub_partner.R
import com.arajangstudio.jajanhub_partner.data.remote.models.*
import com.arajangstudio.jajanhub_partner.databinding.ActivityCompleteBinding
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class CompleteActivity : AppCompatActivity(), BottomSheetImagePicker.OnImagesSelectedListener {

    lateinit var binding: ActivityCompleteBinding
    lateinit var menuMerchantAdapter: MenuMerchantAdapter
    lateinit var facilityMerchantAdapter: FacilityMerchantAdapter
    private val viewModel: MerchantViewModel by viewModels()
    private var photos : ArrayList<Photo> = arrayListOf()
    private var menus : ArrayList<Menu> = arrayListOf()
    private var facilities : ArrayList<Facilities> = arrayListOf()
    var myList: ArrayList<Uri> = arrayListOf()
    private val menuList = ArrayList<Menus>()
    private val facilityList = ArrayList<FacilityAll>()
    private lateinit var dialog: AlertDialog
    private lateinit var storageRef: StorageReference
    private val MEGABYTE = 1024L * 1024L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.title = null
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val storage = Firebase.storage
        storageRef = storage.getReference("uploads/menus")

        dialog = Utils.getAlertDialog(
            this, R.layout.custom_dialog,
            setCancellationOnTouchOutside = false
        )

        loadMenuMerchant()
        loadFacilityMerchant()
        facilities.clear()
        menus.clear()



        binding.btnSave.setOnClickListener {
            val extras = intent.extras
            if (extras != null) {
                val merchantUUID = extras.getString("merchant_uuid")!!
                createMenu(merchantUUID)
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

    private fun loadMenuMerchant() {
        lifecycleScope.launch {
            menuList.clear()
            menus.clear()
            viewModel.getMenus().collectLatest {
                it.forEach { dt ->
                    menuList.add(dt)
                }
                menuMerchantAdapter = MenuMerchantAdapter(menuList)

                val mLayoutManager: RecyclerView.LayoutManager =
                    GridLayoutManager(this@CompleteActivity, 2)

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
                    facilityList.add(dt)
                }
                facilityMerchantAdapter = FacilityMerchantAdapter(facilityList)

                val mLayoutManager: RecyclerView.LayoutManager =
                    GridLayoutManager(this@CompleteActivity, 2)

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


    private fun createMenu(merchant_uuid: String
    ) {
        dialog.show()
        myList.forEach { uri ->
            val fileExtension = getFileExtension(uri)
            val fileName = System.currentTimeMillis().toString() + ".$fileExtension"
            val fileReference = storageRef.child(fileName)
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            val byteArrayOutputStream = ByteArrayOutputStream()
            val pfd = contentResolver?.openFileDescriptor(uri, "r")
            val size = pfd?.statSize?.toDouble()?.div(MEGABYTE)
            if (size != null) {
                if(size < 1) bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                else if(size > 1 && size < 5) bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
                else if(size > 5) bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
            }
            val reducedImage: ByteArray = byteArrayOutputStream.toByteArray()
            fileReference.putBytes(reducedImage)
                .addOnSuccessListener {
                    photos.add(Photo(fileName))
                    if (photos.size == myList.size) {
                        val gson = Gson()
                        menuList.forEach {
                            if(it.isSelected){
                                menus.add(Menu(it.menu_id))
                            }
                        }

                        facilityList.forEach {
                            if(it.isSelected){
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
                            val dialog = BottomSheetDialog(this@CompleteActivity)
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
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}