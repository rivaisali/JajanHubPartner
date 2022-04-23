package com.arajangstudio.jajanhub_partner.ui.setting.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arajangstudio.jajanhub_partner.R
import com.arajangstudio.jajanhub_partner.data.remote.models.Menus
import com.arajangstudio.jajanhub_partner.ui.merchant.MerchantViewModel
import com.arajangstudio.jajanhub_partner.ui.setting.fragment.adapter.ListMenuAdapter
import com.google.android.material.progressindicator.CircularProgressIndicator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditMenuFragment : Fragment() {

    private val viewModel: MerchantViewModel by viewModels()
    private lateinit var listMenuAdapter: ListMenuAdapter
    private val menuList = ArrayList<Menus>()
    private val sizeList = ArrayList<Menus>()
    private lateinit var rvMenu: RecyclerView
    lateinit var uuid: String
    lateinit var progressCircular: CircularProgressIndicator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_menu, container,false)
        rvMenu = view.findViewById(R.id.rvMenu)
        progressCircular = view.findViewById(R.id.progress_circular)


        val bundle = this.arguments
        uuid = bundle!!.getString("merchant_uuid")!!
        viewModel.setSelected(uuid)
        menuList.clear()

        lifecycleScope.launch {
            viewModel.getDetailMerchant().collect { dt ->
                dt.menus.forEach {
                   sizeList.add(it)
                }
            }
        }

        loadMenu()

        return view
    }

    private fun loadMenu(){
        progressCircular.visibility = View.VISIBLE
        lifecycleScope.launch {
            menuList.clear()
            viewModel.getMenuMerchant(uuid).collectLatest { it ->
                it.forEach { dt ->
                    menuList.add(dt)
                    progressCircular.visibility = View.GONE
                }

                listMenuAdapter = ListMenuAdapter(false, menuList, ListMenuAdapter.OnClickListener {

                    if(sizeList.size == 5){
                        Toast.makeText(context, "Kategori menu maksimal hanya 5 hapus terlebih dahulu salah satu", Toast.LENGTH_SHORT).show()
                        progressCircular.visibility = View.GONE
                        parentFragmentManager.popBackStack()
                    }else{
                        lifecycleScope.launch {
                            viewModel.createMenu(uuid, it.menu_id)
                            progressCircular.visibility = View.GONE
                            parentFragmentManager.popBackStack()
                        }
                    }

                })

                val linearLayoutManager = LinearLayoutManager(context)
                linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

                rvMenu.apply {
                    layoutManager = linearLayoutManager
                    setHasFixedSize(true)
                    adapter = listMenuAdapter
                    doOnPreDraw { startPostponedEnterTransition() }
                }
            }
        }

    }


}