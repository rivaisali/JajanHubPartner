package com.arajangstudio.jajanhub_partner.ui.merchant

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arajangstudio.jajanhub_partner.R
import com.arajangstudio.jajanhub_partner.data.remote.models.Merchant
import com.arajangstudio.jajanhub_partner.databinding.ListNearbyBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

class MerchantAdapter : PagingDataAdapter<Merchant, MerchantAdapter.MerchantViewHolder>(COMPARATOR) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<Merchant>() {
            override fun areItemsTheSame(oldItem: Merchant, newItem: Merchant): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Merchant, newItem: Merchant): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onBindViewHolder(holder: MerchantViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MerchantViewHolder {
        return MerchantViewHolder(
            ListNearbyBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    inner class MerchantViewHolder(
        private val binding: ListNearbyBinding

    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Merchant) {
            with(binding) {
                tvMerchantName.text = data.merchant_name
                if(data.badge == "verified")
                    tvMerchantName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_verified,0,0,0)

                tvMerchantAddress.text = data.location_name
                tvRating.text = data.rating_total

                Glide.with(itemView.context)
                    .load(data.photo)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(
                        RequestOptions
                            .placeholderOf(R.drawable.placeholder_background)
                            .error(R.drawable.placeholder_background)
                            .apply(RequestOptions.bitmapTransform(RoundedCorners(14)))
                    )
                    .into(ivMerchantImage)

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailMerchantActivity::class.java)
                    intent.putExtra(DetailMerchantActivity.EXTRA_ID, data.uuid)
                    itemView.context.startActivity(intent)
                }

            }

        }
    }


}