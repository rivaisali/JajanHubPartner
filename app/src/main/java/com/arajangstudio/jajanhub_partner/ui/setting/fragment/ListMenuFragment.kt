package com.arajangstudio.jajanhub_partner.ui.setting.fragment

import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
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
class ListMenuFragment : Fragment() {

    private val viewModel: MerchantViewModel by viewModels()
    private lateinit var listMenuAdapter: ListMenuAdapter
    private val menuList = ArrayList<Menus>()
    private lateinit var rvMenu:RecyclerView
    lateinit var uuid: String
    lateinit var progressCircular: CircularProgressIndicator
    lateinit var ivEmpty: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_menu, container,false)
        rvMenu = view.findViewById(R.id.rvMenu)
        progressCircular = view.findViewById(R.id.progress_circular)
        ivEmpty = view.findViewById(R.id.ivEmpty)

        val bundle = this.arguments
         uuid = bundle!!.getString("merchant_uuid")!!
        viewModel.setSelected(uuid)
        menuList.clear()
        loadMenu()

        return view
    }

    private fun loadMenu(){
        menuList.clear()
        progressCircular.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.getDetailMerchant().collect {
                progressCircular.visibility = View.GONE
                it.menus.forEach { menu ->
                    menuList.add(menu)
                    listMenuAdapter = ListMenuAdapter(true, menuList, ListMenuAdapter.OnClickListener {
                        progressCircular.visibility = View.VISIBLE
                        lifecycleScope.launch {
                            viewModel.deleteMenu(it.merchant_menu_id)
                            progressCircular.visibility = View.GONE
                            loadMenu()
                            Toast.makeText(context, "Berhasil dihapus", Toast.LENGTH_SHORT).show()
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
                if(menuList.size > 0){
                ivEmpty.visibility = View.GONE
                }else{
                    ivEmpty.visibility = View.VISIBLE
                }
            }
        }

    }
}