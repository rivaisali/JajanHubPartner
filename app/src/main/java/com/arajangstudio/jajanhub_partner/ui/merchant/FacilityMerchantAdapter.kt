package com.arajangstudio.jajanhub_partner.ui.merchant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arajangstudio.jajanhub_partner.data.remote.models.FacilityAll
import com.arajangstudio.jajanhub_partner.databinding.ItemFacilitesBinding
import java.util.*
import kotlin.collections.ArrayList

class FacilityMerchantAdapter(var facilitiesList: ArrayList<FacilityAll>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = facilitiesList[position]
        (holder as FacilityViewHolder).bind(item)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacilityViewHolder {

        return FacilityViewHolder(
            ItemFacilitesBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    class FacilityViewHolder(private val binding: ItemFacilitesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(facility: FacilityAll) {
            with(binding) {
                checkFacility.text = facility.facility
                checkFacility.isChecked = facility.isSelected
                checkFacility.setOnClickListener  {
                    facility.isSelected = !facility.isSelected
                }


            }

        }
    }


    override fun getItemCount(): Int {
            return facilitiesList.size
    }

}