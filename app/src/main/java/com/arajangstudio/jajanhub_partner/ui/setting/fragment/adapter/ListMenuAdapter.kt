package com.arajangstudio.jajanhub_partner.ui.setting.fragment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.arajangstudio.jajanhub_partner.R
import com.arajangstudio.jajanhub_partner.data.remote.models.Menus
import com.arajangstudio.jajanhub_partner.databinding.ListItemMenuBinding
import com.arajangstudio.jajanhub_partner.ui.merchant.MerchantViewModel

class ListMenuAdapter(var list:Boolean, var menusList: ArrayList<Menus>, var clickListener: OnClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val item = menusList[position]

        val btnAdd = holder.itemView.findViewById<TextView>(R.id.btnAdd)
        val btnDelete = holder.itemView.findViewById<TextView>(R.id.btnDelete)

        if(list) {
            btnAdd.visibility = View.GONE
            btnDelete.visibility = View.VISIBLE
        }
        else{
            btnDelete.visibility = View.GONE
            btnAdd.visibility = View.VISIBLE
        }

        btnAdd.setOnClickListener {
                clickListener.onClick(item)


        }

        btnDelete.setOnClickListener {
            clickListener.onClick(item)
        }

        (holder as MenuViewHolder).bind(item)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {

        return MenuViewHolder(
            ListItemMenuBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    class MenuViewHolder(private val binding: ListItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(menu: Menus) {
            with(binding) {
                tvTitle.text = menu.title
            }

        }
    }

    class OnClickListener(val clickListener: (menu: Menus) -> Unit) {
        fun onClick(menu: Menus) = clickListener(menu)
    }


    override fun getItemCount(): Int {
        return menusList.size
    }

}