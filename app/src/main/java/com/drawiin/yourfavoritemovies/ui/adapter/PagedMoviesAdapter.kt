package com.drawiin.yourfavoritemovies.ui.adapter

import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.drawiin.yourfavoritemovies.R
import com.drawiin.yourfavoritemovies.domain.models.Movie

class PagedMoviesAdapter(
    val onClick: (item: Movie) -> Unit
) : PagingDataAdapter<Movie, MovieViewHolder>(DiffUtilItemCallBack) {
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

    companion object DiffUtilItemCallBack : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie) = oldItem == newItem
    }
}


