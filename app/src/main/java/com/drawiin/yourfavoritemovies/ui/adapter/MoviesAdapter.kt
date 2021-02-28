package com.drawiin.yourfavoritemovies.ui.adapter

import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.drawiin.yourfavoritemovies.R
import com.drawiin.yourfavoritemovies.model.ApiMovie

class MoviesAdapter(
    val onClick: (item: ApiMovie) -> Unit
) : ListAdapter<ApiMovie, MovieViewHolder>(DiffUtilItemCallBack) {
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        getItem(position)?.let { movie ->
            holder.onBind(movie)
            holder.favorite.setOnClickListener {
                holder.favorite.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        holder.itemView.resources,
                        R.drawable.ic_favorite,
                        null
                    )
                )
                onClick(movie)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MovieViewHolder.inflate(parent)

    companion object DiffUtilItemCallBack : DiffUtil.ItemCallback<ApiMovie>() {
        override fun areItemsTheSame(oldItem: ApiMovie, newItem: ApiMovie) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ApiMovie, newItem: ApiMovie) = oldItem == newItem
    }
}


