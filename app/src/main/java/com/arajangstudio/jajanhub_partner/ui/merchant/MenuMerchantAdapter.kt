package com.arajangstudio.jajanhub_partner.ui.merchant

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.arajangstudio.jajanhub_partner.R
import com.arajangstudio.jajanhub_partner.data.remote.models.Menus
import com.arajangstudio.jajanhub_partner.databinding.ItemMenusBinding
import com.google.android.material.checkbox.MaterialCheckBox
import java.util.*
import kotlin.collections.ArrayList

class MenuMerchantAdapter(var menusList: ArrayList<Menus>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    Filterable {

    var menuFilterList = ArrayList<Menus>()
    var counter = 0


    init {
        menuFilterList = menusList
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = menuFilterList[position]
        (holder as MenuViewHolder).bind(item)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {

        return MenuViewHolder(
            ItemMenusBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    class MenuViewHolder(private val binding: ItemMenusBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(menus: Menus) {
            with(binding) {
                checkMenu.text = menus.title
                checkMenu.isChecked = menus.isSelected
                checkMenu.setOnClickListener  {
                    menus.isSelected = !menus.isSelected
                }
            }
        }
   }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    menuFilterList = menusList
                } else {
                    val resultList = ArrayList<Menus>()
                    for (row in menusList) {
                        if (row.title.lowercase(Locale.ROOT)
                                .contains(charSearch.lowercase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    menuFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = menuFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                menuFilterList = results?.values as ArrayList<Menus>
                notifyDataSetChanged()
            }

        }
    }

    override fun getItemCount(): Int {
        val limit = 8
        if (menuFilterList.size > limit) {
            return limit
        } else {
            return menuFilterList.size
        }

    }

}