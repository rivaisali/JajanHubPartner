package com.arajangstudio.jajanhub_partner.ui.merchant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arajangstudio.jajanhub_partner.R
import com.arajangstudio.jajanhub_partner.data.remote.models.PhotoReview
import com.arajangstudio.jajanhub_partner.databinding.ListPhotoReviewBinding
import com.arajangstudio.jajanhub_partner.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

class PhotoReviewAdapter(private val photos: List<PhotoReview>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = photos[position]
        (holder as PhotoViewHolder).bind(item)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {

        return PhotoViewHolder(
            ListPhotoReviewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    class PhotoViewHolder(private val binding: ListPhotoReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(photoReview: PhotoReview) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(photoReview.photo)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(
                        RequestOptions.placeholderOf(R.drawable.ic_loading)
                            .error(R.drawable.ic_error)
                            .apply(RequestOptions.bitmapTransform(RoundedCorners(14)))
                    )
                    .into(ivPhoto)
                itemView.setOnClickListener {
               Utils.goToDetail(itemView.context, arrayListOf(photoReview.photo),0)
            }


            }

        }
    }

    override fun getItemCount(): Int {
        val limit = 4
        if(photos.size > limit){
            return  limit
        }
        else
        {
            return photos.size
        }

    }


}