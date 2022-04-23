package com.arajangstudio.jajanhub_partner.ui.setting.bottomsheet

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.arajangstudio.jajanhub_partner.R
import com.arajangstudio.jajanhub_partner.ui.merchant.MerchantViewModel
import com.arajangstudio.jajanhub_partner.utils.RoundedImageView
import com.arajangstudio.jajanhub_partner.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.kroegerama.imgpicker.BottomSheetImagePicker
import com.kroegerama.imgpicker.ButtonType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.DecimalFormat


@AndroidEntryPoint
class BottomSheetMerchant : BottomSheetDialogFragment(), BottomSheetImagePicker.OnImagesSelectedListener  {

    private lateinit var bottomSheet: ViewGroup
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var ivMerchantImage : RoundedImageView
    private lateinit var btnChange: LinearLayout
    private lateinit var btnSave: Button
    private lateinit var progressCircular: CircularProgressIndicator
    private lateinit var storageRef: StorageReference
    private lateinit var merchant_uuid: String
    private lateinit var currentFile: String
    var myList: ArrayList<Uri> = arrayListOf()
    private val storage = Firebase.storage
    private val MEGABYTE = 1024L * 1024L

    private val viewModel: MerchantViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        bottomSheet =
            dialog!!.findViewById(com.google.android.material.R.id.design_bottom_sheet) as ViewGroup
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(view: View, i: Int) {
                if (BottomSheetBehavior.STATE_EXPANDED == i) {

                }
                if (BottomSheetBehavior.STATE_COLLAPSED == i) {

                }
                if (BottomSheetBehavior.STATE_HIDDEN == i) {
                    dismiss()
                }
            }

            override fun onSlide(view: View, v: Float) {}
        })

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myview: View = inflater.inflate(R.layout.bottomsheet_merchant, container, false)
        ivMerchantImage = myview.findViewById(R.id.ivMerchantImage)
        btnChange = myview.findViewById(R.id.btnchange)
        btnSave = myview.findViewById(R.id.btnSave)
        progressCircular = myview.findViewById(R.id.progress_circular)

        storageRef = storage.getReference("uploads/merchants")

        val bundle = this.arguments
        merchant_uuid = bundle?.getString("merchant_uuid")!!
        val bundlePut = Bundle()
        bundlePut.putString("merchant_uuid", merchant_uuid)

        loadMerchant()

        btnChange.setOnClickListener {
            BottomSheetImagePicker.Builder(getString(R.string.file_provider))
                .galleryButton(ButtonType.Button)           //style of the gallery link
                .singleSelectTitle(R.string.pick_single)    //header text
                .peekHeight(R.dimen.peekHeight)             //peek height of the bottom sheet
                .columnSize(R.dimen.columnSize)             //size of the columns (will be changed a little to fit)
                .requestTag("single")                       //tag can be used if multiple pickers are used
                .show(this@BottomSheetMerchant.childFragmentManager)
        }

        return myview
    }

    private fun loadMerchant(){
        val handler = CoroutineExceptionHandler { _, exception ->
            println("CoroutineExceptionHandler got $exception")
        }
        lifecycleScope.launch(handler) {
            viewModel.setSelected(merchant_uuid!!)
            viewModel.getDetailMerchant().collect { merchant ->
                currentFile = merchant.photo
                Glide.with(this@BottomSheetMerchant)
                    .load(merchant.photo)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(
                        RequestOptions
                            .placeholderOf(R.drawable.placeholder_background)
                            .error(R.drawable.placeholder_background)
                            .apply(RequestOptions.bitmapTransform(RoundedCorners(14)))

                    )
                    .centerCrop()
                    .into(ivMerchantImage)
                btnSave.visibility = View.GONE

                ivMerchantImage.setOnClickListener {
                    Utils.goToDetail(requireContext(), arrayListOf(merchant.photo), 0)
                }

            }
        }
    }

    private fun getFileExtension(uri: Uri?): String? {
        val contentResolver = context?.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver?.getType(uri!!))
    }

    override fun onImagesSelected(uris: List<Uri>, tag: String?) {

        uris.forEach { uri ->
            myList.add(uri)
            Glide.with(this)
                .load(uri)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.bitmapTransform(RoundedCorners(14)))
                .into(ivMerchantImage)

            btnSave.setOnClickListener {
                progressCircular.visibility = View.VISIBLE
                val fileExtension = getFileExtension(uri)
                val fileName = System.currentTimeMillis().toString() + ".$fileExtension"
                val fileReference = storageRef.child(fileName)
                val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)
                val byteArrayOutputStream = ByteArrayOutputStream()
                val pfd = context?.contentResolver?.openFileDescriptor(uri, "r")
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
                            viewModel.updateMerchantPhoto(merchant_uuid, fileName)
                            storage.getReferenceFromUrl(currentFile).delete()
                            Toast.makeText(context, "Foto Berhasil di Ganti", Toast.LENGTH_SHORT).show()
                            loadMerchant()
                            progressCircular.visibility = View.GONE
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
            }

        }

        btnSave.visibility = View.VISIBLE

    }

}