package com.arajangstudio.jajanhub_partner.ui.merchant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.arajangstudio.jajanhub_partner.R
import com.arajangstudio.jajanhub_partner.data.remote.models.Review
import com.arajangstudio.jajanhub_partner.databinding.ListReviewMerchantBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

class ReviewAdapter : PagingDataAdapter<Review, ReviewAdapter.ReviewViewHolder>(COMPARATOR) {

    companion object {
        val COMPARATOR = object : DiffUtil.ItemCallback<Review>() {
            override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder(
            ListReviewMerchantBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    inner class ReviewViewHolder(
        private val binding: ListReviewMerchantBinding

    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Review) {
            with(binding) {
            tvFullName.text = data.full_name
                tvReview.text = data.message
                tvRatting.text = "${data.total_rating} "
                tvCountReview.text = "${data.total_review} Ulasan"
                Glide.with(itemView.context)
                    .load(R.drawable.ic_blank_photo)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(
                        RequestOptions.placeholderOf(R.drawable.ic_loading)
                            .error(R.drawable.ic_error)
                            .apply(RequestOptions.circleCropTransform())
                    )
                    .into(ivPhoto)

                val photoReviewAdapter = PhotoReviewAdapter(data.photos)
                    rvPhotos.apply {
                        setHasFixedSize(true)
                        layoutManager =  StaggeredGridLayoutManager(
                            2, // span count
                            StaggeredGridLayoutManager.VERTICAL // orientation
                        )
                        adapter = photoReviewAdapter
                    }
            }

        }
    }


}