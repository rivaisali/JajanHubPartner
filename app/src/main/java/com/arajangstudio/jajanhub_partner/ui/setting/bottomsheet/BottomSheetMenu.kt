package com.arajangstudio.jajanhub_partner.ui.setting.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.arajangstudio.jajanhub_partner.R
import com.arajangstudio.jajanhub_partner.ui.merchant.MerchantViewModel
import com.arajangstudio.jajanhub_partner.ui.setting.fragment.EditMenuFragment
import com.arajangstudio.jajanhub_partner.ui.setting.fragment.ListMenuFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BottomSheetMenu : BottomSheetDialogFragment() {

    private lateinit var bottomSheet:  ViewGroup
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var btnAdd: ImageView
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
        val myview: View = inflater.inflate(R.layout.bottomsheet_menu, container, false)
        val btnAdd = myview.findViewById<ImageView>(R.id.btnAdd)

        val bundle = this.arguments
        val merchant_uuid = bundle!!.getString("merchant_uuid")
        val bundlePut = Bundle()
        bundlePut.putString("merchant_uuid", merchant_uuid)

        val listMenuFragment = ListMenuFragment()
        listMenuFragment.arguments = bundlePut
        childFragmentManager.beginTransaction()
            .addToBackStack(null)
            .setCustomAnimations(
                R.anim.enter_from_left,
                R.anim.exit_to_left,
                 R.anim.enter_from_right,
               R.anim.exit_to_right
            )
            .replace(R.id.container_fragment, listMenuFragment, "ListMenu")
            .commit()


        btnAdd.setOnClickListener {
            val editMenuFragment = EditMenuFragment()
            editMenuFragment.arguments = bundlePut
            childFragmentManager.beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                .replace(R.id.container_fragment, editMenuFragment, "CreateMenu")
                .commit()
        }

        return myview
    }
}