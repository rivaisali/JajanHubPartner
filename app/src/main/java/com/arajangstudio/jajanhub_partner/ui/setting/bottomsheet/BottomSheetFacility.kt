package com.arajangstudio.jajanhub_partner.ui.setting.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.arajangstudio.jajanhub_partner.R
import com.arajangstudio.jajanhub_partner.data.remote.models.Facilities
import com.arajangstudio.jajanhub_partner.data.remote.models.Facility
import com.arajangstudio.jajanhub_partner.data.remote.models.FacilityAll
import com.arajangstudio.jajanhub_partner.ui.merchant.FacilityMerchantAdapter
import com.arajangstudio.jajanhub_partner.ui.merchant.MerchantViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BottomSheetFacility : BottomSheetDialogFragment() {

    private lateinit var bottomSheet: ViewGroup
    lateinit var facilityMerchantAdapter: FacilityMerchantAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private val facilityList = ArrayList<FacilityAll>()
    private var facilities: ArrayList<Facilities> = arrayListOf()
    private val selectedFacilityList = ArrayList<Facility>()
    private lateinit var rvFacility: RecyclerView
    private lateinit var btnFacility: Button
    private lateinit var doneView: LottieAnimationView
    private lateinit var progressCircular: CircularProgressIndicator
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
        val myview: View = inflater.inflate(R.layout.bottomsheet_facility, container, false)
        rvFacility = myview.findViewById(R.id.rvFacility)
        btnFacility = myview.findViewById(R.id.btnUpdateFacility)
        progressCircular = myview.findViewById(R.id.progress_circular)
        doneView = myview.findViewById(R.id.animation_view)

        val bundle = this.arguments
        val merchant_uuid = bundle!!.getString("merchant_uuid")
        val bundlePut = Bundle()
        bundlePut.putString("merchant_uuid", merchant_uuid)

        val handler = CoroutineExceptionHandler { _, exception ->
            println("CoroutineExceptionHandler got $exception")
        }
        lifecycleScope.launch(handler) {
            viewModel.setSelected(merchant_uuid!!)
            viewModel.getDetailMerchant().collect { merchant ->
                merchant.facilities.forEach {
                   selectedFacilityList.add(it)
               }
                loadFacilityMerchant()

                btnFacility.setOnClickListener {
                    progressCircular.visibility = View.VISIBLE
                    facilityList.forEach {
                        if (it.isSelected) {
                            facilities.add(Facilities(it.id))
                        }
                    }
                    val gson = Gson()
                    val jsonFacilities = gson.toJson(facilities)
                    lifecycleScope.launch {
                        viewModel.createFacility(merchant.uuid, jsonFacilities)
                        progressCircular.visibility = View.GONE
                        doneView.visibility = View.VISIBLE
                        rvFacility.visibility = View.GONE
                        btnFacility.visibility = View.GONE
                    }
                }
            }
        }

        return myview
    }

    private fun loadFacilityMerchant() {
        lifecycleScope.launch {
            facilityList.clear()
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
                    GridLayoutManager(context, 2)

                rvFacility.apply {
                    layoutManager = mLayoutManager
                    setHasFixedSize(true)
                    adapter = facilityMerchantAdapter
                    doOnPreDraw { startPostponedEnterTransition() }
                }

            }
        }
    }
}