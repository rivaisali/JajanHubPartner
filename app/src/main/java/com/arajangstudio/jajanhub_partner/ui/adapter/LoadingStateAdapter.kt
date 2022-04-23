package com.arajangstudio.jajanhub_partner.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arajangstudio.jajanhub_partner.databinding.NetworkStateItemBinding

class LoadingStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<LoadingStateAdapter.LoadStateViewHolder>() {

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {

        val progress = holder.binding.progressBar
        if (loadState is LoadState.Loading) {
            progress.isVisible = true


        } else {
            progress.isVisible = false

        }

        if (loadState is LoadState.Error) {

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        return LoadStateViewHolder(
            NetworkStateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    inner class LoadStateViewHolder(val binding: NetworkStateItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}