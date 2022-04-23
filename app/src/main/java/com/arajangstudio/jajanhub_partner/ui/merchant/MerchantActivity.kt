package com.arajangstudio.jajanhub_partner.ui.merchant

import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Spanned
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.doOnPreDraw
import androidx.core.view.isEmpty
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arajangstudio.jajanhub_partner.R
import com.arajangstudio.jajanhub_partner.data.remote.models.Schedule
import com.arajangstudio.jajanhub_partner.databinding.ActivityMerchantBinding
import com.arajangstudio.jajanhub_partner.ui.maps.LocationPickerActivity
import com.arajangstudio.jajanhub_partner.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.ChipDrawable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.kroegerama.imgpicker.BottomSheetImagePicker
import com.kroegerama.imgpicker.ButtonType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


@AndroidEntryPoint
class MerchantActivity : AppCompatActivity(), BottomSheetImagePicker.OnImagesSelectedListener {

    lateinit var binding: ActivityMerchantBinding
    private var locationLatitude = 0.0
    private var locationLongitude = 0.0
    private var locationName = ""
    private var locationAddress = ""
    lateinit var auth: FirebaseAuth
    var myList: ArrayList<Uri> = arrayListOf()
    private val viewModel: MerchantViewModel by viewModels()
    private var scheduleSelected: ArrayList<Schedule> = arrayListOf()
    private lateinit var storageRef: StorageReference
    private lateinit var dialog: AlertDialog
    lateinit var dialogScheduleAdapter: DialogScheduleAdapter
    private val MEGABYTE = 1024L * 1024L

    @SuppressLint("SetTextI18n", "RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMerchantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.title = null
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        dialog = Utils.getAlertDialog(
            this, R.layout.custom_dialog,
            setCancellationOnTouchOutside = false
        )

        auth = FirebaseAuth.getInstance()
        auth.setLanguageCode("id")
        val storage = Firebase.storage
        storageRef = storage.getReference("uploads/merchants")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item, arrayListOf("Rumahan", "Warung Makan", "Warung Kopi", "Kedai", "Booth/Stand")
        )

        binding?.typeMerchant?.setAdapter(adapter)

        binding.btnChangeLocation.setOnClickListener {
            val intent = Intent(this, LocationPickerActivity::class.java)
            startForResult.launch(intent)
        }

        binding.txtTimeOpen.setOnClickListener {
            val timePicker = TimePickerDialog(
                this,3,
                timeOpenPickerDialogListener,
                // default hour when the time picker
                12,
                // default minute when the time picker
                10,
                // 24 hours time
                true
            )
            timePicker.show()
        }

        binding.txtTimeClose.setOnClickListener {
            val timePicker = TimePickerDialog(
                this,3,
                timeClosePickerDialogListener,
                // default hour when the time picker
                12,
                // default minute when the time picker
                10,
                // 24 hours time
                true
            )
            timePicker.show()
        }

        binding.imageContainer.setOnClickListener {
            BottomSheetImagePicker.Builder(getString(R.string.file_provider))
                .galleryButton(ButtonType.Button)           //style of the gallery link
                .singleSelectTitle(R.string.pick_single)    //header text
                .peekHeight(R.dimen.peekHeight)             //peek height of the bottom sheet
                .columnSize(R.dimen.columnSize)             //size of the columns (will be changed a little to fit)
                .requestTag("single")                       //tag can be used if multiple pickers are used
                .show(supportFragmentManager)
        }

        binding.txtSchedule.setOnClickListener {
            binding.txtSchedule.setText("")
            val scheduleList = listOf(
                Schedule(id = "1", schedule = "Senin", isSelected = false),
                Schedule(id = "2", schedule = "Selasa", isSelected = false),
                Schedule(id = "3", schedule = "Rabu", isSelected = false),
                Schedule(id = "4", schedule = "Kamis", isSelected = false),
                Schedule(id = "5", schedule = "Jumat", isSelected = false),
                Schedule(id = "6", schedule = "Sabtu", isSelected = false),
                Schedule(id = "7", schedule = "Minggu", isSelected = false),
                Schedule(id = "8", schedule = "Setiap Hari", isSelected = false),
            )
            dialogScheduleAdapter = DialogScheduleAdapter(scheduleList)
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@MerchantActivity)
            val layoutInflater: LayoutInflater = LayoutInflater.from(this@MerchantActivity)
            val customLayout: View = layoutInflater.inflate(R.layout.dialog_schedule, null)
            builder.setView(customLayout, 20, 0, 20, 0)

            val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(this@MerchantActivity, 2)
            val rvDialog = customLayout.findViewById<RecyclerView>(R.id.rvSchedule)
            val btnDone = customLayout.findViewById<CardView>(R.id.btnDone)

            val dialog = builder.create()
            rvDialog.adapter = dialogScheduleAdapter

            rvDialog.apply {
                layoutManager = mLayoutManager
                setHasFixedSize(true)
                doOnPreDraw { startPostponedEnterTransition() }
            }

            btnDone.setOnClickListener {
                scheduleSelected.clear()
                dialogScheduleAdapter.scheduleList.forEach {
                    if(it.isSelected){
                        scheduleSelected.add(it)
                }
             }
                val text = binding.txtSchedule.text!!
                for (i in 0 until scheduleSelected.size) {
                    if(i == scheduleSelected.size - 1){
                        text.append(scheduleSelected[i].schedule)
                    }else{
                        text.append(scheduleSelected[i].schedule+",")
                    }

                }

                dialog.dismiss()
            }



            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
//            dialog.setCanceledOnTouchOutside(false)
//            dialog.setCancelable(false)
            dialog.show()
        }


        binding.btnSave.setOnClickListener {

            when {
                binding.txtMerchantName.text.isNullOrEmpty() -> {
                    binding.layoutMerchantName.error = "Nama Usaha tidak boleh kosong"
                    binding.txtMerchantName.isFocusable
                }
                binding.typeMerchant.text.isNullOrEmpty() -> {
                    binding.menu.error = "Pilih jenis makanan usaha anda"
                    binding.typeMerchant.isFocusable
                }
                binding.txtPhoneNumber.text.isNullOrEmpty() -> {
                    binding.layoutPhone.error = "Pilih jenis makanan usaha anda"
                    binding.txtPhoneNumber.isFocusable
                }

                binding.txtLocationName.text.isNullOrEmpty() -> {
                    binding.layoutLocationName.error = "Nama lokasi tidak boleh kosong"
                    binding.txtLocationName.isFocusable
                }
                binding.btnChangeLocation.text.isNullOrEmpty() -> {
                    binding.layoutChangeLocation.error = "Klik untuk mencari lokasi usaha anda"
                    binding.btnChangeLocation.isFocusable
                }
                binding.txtLocationAddress.text.isNullOrEmpty() -> {
                binding.layoutAddress.error = "Alamat usaha tidak boleh kosong"
                binding.txtLocationAddress.isFocusable
            }
                binding.txtSchedule.text.isNullOrEmpty() -> {
                    binding.layoutSchedule.error = "Jadwal tidak boleh kosong"
                    binding.txtSchedule.isFocusable
                }
                binding.txtTimeOpen.text.isNullOrEmpty() -> {
                    binding.layoutTxtOpen.error = "Jam buka tidak boleh kosong"
                    binding.txtTimeOpen.isFocusable
                }

                binding.txtTimeClose.text.isNullOrEmpty() -> {
                    binding.layoutTimeClose.error = "Jam tutup tidak boleh kosong"
                    binding.txtTimeClose.isFocusable
                }


                binding.imageContainer.isEmpty() -> {
                    Toast.makeText(this, "Foto belum dipilih", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    binding.layoutMerchantName.error = null
                    binding.menu.error = null
                    binding.layoutPhone.error = null
                    binding.layoutLocationName.error = null
                    binding.layoutChangeLocation.error = null
                    binding.layoutAddress.error = null
                    binding.layoutSchedule.error = null
                    binding.layoutTxtOpen.error = null
                    binding.layoutTimeClose.error = null

                    dialog.show()

                    val branch = if (binding.swBranch.isChecked) 1 else 0
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
                                lifecycleScope.launch {
                                    viewModel.createMerchant(
                                        auth.currentUser!!.uid,
                                        binding.typeMerchant.text?.trim().toString(),
                                        binding.txtMerchantName.text?.trim().toString(),
                                        branch,
                                        binding.txtPhoneNumber.text?.trim().toString(),
                                        binding.txtInstagram.text?.trim().toString(),
                                        binding.txtLocationName.text?.trim().toString(),
                                        binding.txtLocationAddress.text?.trim().toString(),
                                        locationLatitude,
                                        locationLongitude,
                                        binding.txtSchedule.text?.trim().toString(),
                                        binding.txtTimeOpen.text.toString(),
                                        binding.txtTimeClose.text.toString(),
                                        fileName
                                    )
                                }
                                dialog.dismiss()
                           val dialog = BottomSheetDialog(this@MerchantActivity)
                            val view = layoutInflater.inflate(R.layout.bottomsheet_done, null)
                            dialog.setCancelable(false)
                            dialog.setContentView(view)
                            dialog.show()
                            val btnDone = view.findViewById<Button>(R.id.btnDone)
                            btnDone.setOnClickListener {
                                dialog.dismiss()
                                finish()
                            } }
                            .addOnFailureListener {
                                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                            }

                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private val timeOpenPickerDialogListener: TimePickerDialog.OnTimeSetListener =
        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            binding.txtTimeOpen.setText("$hourOfDay:$minute")
        }

    @SuppressLint("SetTextI18n")
    private val timeClosePickerDialogListener: TimePickerDialog.OnTimeSetListener =
        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            binding.txtTimeClose.setText("$hourOfDay:$minute")
        }


    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val extras = intent?.extras
            if (extras != null) {
                locationName = extras.getString("locationName")!!
                locationAddress = extras.getString("locationAddress")!!
                locationLatitude = extras.getDouble("locationLatitude")
                locationLongitude = extras.getDouble("locationLongitude")

                binding.txtLocationName.setText(locationName)
                binding.txtLocationAddress.setText(locationAddress)
                binding.btnChangeLocation.setText("$locationLatitude,$locationLongitude")
            }

        }
    }

    fun getFileExtension(uri: Uri?): String? {
        val contentResolver = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onImagesSelected(uris: List<Uri>, tag: String?) {
        binding.imageContainer.removeAllViews()
        myList.clear()
        uris.forEach { uri ->
            myList.add(uri)
            val frameLayout = LayoutInflater.from(this).inflate(
                R.layout.item_image_merchant,
                binding.imageContainer, false
            ) as FrameLayout
            frameLayout.id = binding.imageContainer.childCount
            val iv = frameLayout.findViewById<ImageView>(R.id.ivImage)
            val show = frameLayout.findViewById<TextView>(R.id.btnShow)

            show.setOnClickListener {
            Utils.goToDetail(this, arrayListOf(queryName(uri)!!), 0)
            }

            binding.imageContainer.addView(frameLayout)
            binding.imageContainer.background = null


            Glide.with(this)
                .load(uri)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.bitmapTransform(RoundedCorners(14)))
                .into(iv)
        }

    }

    private fun queryName(uri: Uri): String? {
        val returnCursor: Cursor = contentResolver.query(uri, null, null, null, null) ?: return null
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }



}