package com.jessica.yourfavoritemovies.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jessica.yourfavoritemovies.R
import com.jessica.yourfavoritemovies.databinding.ItemRecyclerViewBinding
import com.jessica.yourfavoritemovies.model.Result
import com.jessica.yourfavoritemovies.utils.Constants.BASE_IMAGE_URL

class MovieAdapter(
    var listMovie: MutableList<Result>,
    val onClick: (item: Result) -> Unit
) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.inflate(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(listMovie[position])
        holder.favorite.setOnClickListener {
            holder.favorite.setImageDrawable(
                ResourcesCompat.getDrawable(
                    holder.itemView.resources,
                    R.drawable.ic_favorite,
                    null
                )
            )
            onClick(listMovie[position])
        }
    }

    override fun getItemCount(): Int = listMovie.size

    fun updateList(results: MutableList<Result>) {
        listMovie = results
        notifyDataSetChanged()
    }

    fun removeItem(result: Result) {
        listMovie.remove(result)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val favorite: ImageView = itemView.findViewById(R.id.iv_favorite)

        fun onBind(result: Result) {
            Glide.with(itemView.context)
                .load("$BASE_IMAGE_URL${result.posterPath}")
                .placeholder(R.mipmap.ic_launcher)
                .into(binding.ivMovie)
        }

        companion object {
            fun inflate(parent: ViewGroup) = ViewHolder(
                ItemRecyclerViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}