package com.drawiin.yourfavoritemovies.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.drawiin.yourfavoritemovies.R
import com.drawiin.yourfavoritemovies.databinding.ItemRecyclerViewBinding
import com.drawiin.yourfavoritemovies.model.ApiMovie
import com.drawiin.yourfavoritemovies.utils.Constants

class MovieViewHolder(val binding: ItemRecyclerViewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    val favorite: ImageView = itemView.findViewById(R.id.iv_favorite)

    fun onBind(apiMovie: ApiMovie) {
        Glide.with(itemView.context)
            .load("${Constants.BASE_IMAGE_URL}${apiMovie.posterPath}")
            .placeholder(R.drawable.placeholder_bg)
            .into(binding.ivMovie)
    }

    companion object {
        fun inflate(parent: ViewGroup) = MovieViewHolder(
            ItemRecyclerViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}