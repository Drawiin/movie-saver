package com.drawiin.yourfavoritemovies.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.drawiin.yourfavoritemovies.domain.models.Movie

class FavoritesAdapter(
    val onClick: (item: Movie) -> Unit
) : ListAdapter<Movie, MovieViewHolder>(DiffUtilItemCallBack) {
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        getItem(position)?.let { movie ->
            holder.onBind(movie)
            holder.favorite.setOnClickListener {
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


