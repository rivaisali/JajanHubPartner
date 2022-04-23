package com.arajangstudio.jajanhub_partner.ui.setting.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.arajangstudio.jajanhub_partner.R
import com.arajangstudio.jajanhub_partner.ui.merchant.MerchantViewModel
import com.arajangstudio.jajanhub_partner.ui.setting.fragment.EditMenuFragment
import com.arajangstudio.jajanhub_partner.ui.setting.fragment.ListMenuFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BottomSheetSuggestion : BottomSheetDialogFragment() {

    private lateinit var bottomSheet:  ViewGroup
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var btnSend: Button
    private lateinit var txtTitle: TextInputEditText
    private lateinit var txtMessage: TextInputEditText
    private lateinit var progressCircular: CircularProgressIndicator
    private val viewModel: MerchantViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        bottomSheet = dialog!!.findViewById(com.google.android.material.R.id.design_bottom_sheet) as ViewGroup
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
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
        val myview: View = inflater.inflate(R.layout.bottomsheet_suggestion, container, false)
        txtTitle = myview.findViewById(R.id.txtTitle)
        txtMessage = myview.findViewById(R.id.txtMessage)
        progressCircular = myview.findViewById(R.id.progress_circular)
        btnSend = myview.findViewById(R.id.btnSend)

        val bundle = this.arguments
        val user_uuid = bundle!!.getString("user_uuid")

        btnSend.setOnClickListener {
            if(txtTitle.text.toString().trim() == ""){
                Toast.makeText(context, "Judul tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            else if(txtMessage.text.toString().trim() == ""){
                Toast.makeText(context, "Saran tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }else{
                progressCircular.visibility = View.VISIBLE
                lifecycleScope.launch {
                    viewModel.createSuggestion(user_uuid!!, txtTitle.text.toString().trim(),
                        txtMessage.text.toString().trim())
                    progressCircular.visibility = View.GONE
                    Toast.makeText(context, "Terima kasih, Saran anda berhasil dikirim", Toast.LENGTH_SHORT).show()
                    dismiss()
                }

            }
        }
        return myview
    }
}