package com.arajangstudio.jajanhub_partner.ui.merchant

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.arajangstudio.jajanhub_partner.data.remote.models.Schedule
import com.arajangstudio.jajanhub_partner.databinding.ItemScheduleBinding
import java.util.*
import kotlin.collections.ArrayList

class DialogScheduleAdapter(var scheduleList: List<Schedule>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = scheduleList[position]
        (holder as MenuViewHolder).bind(item)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {

        return MenuViewHolder(
            ItemScheduleBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    class MenuViewHolder(private val binding: ItemScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(schedule: Schedule) {
            with(binding) {
                checkSchedule.text = schedule.schedule
                checkSchedule.isChecked = schedule.isSelected
                checkSchedule.setOnClickListener  {
                    schedule.isSelected = !schedule.isSelected
                }


            }

        }
    }

    override fun getItemCount(): Int {
            return scheduleList.size

    }

}