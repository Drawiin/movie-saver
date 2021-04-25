package com.drawiin.yourfavoritemovies.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.drawiin.yourfavoritemovies.domain.models.Profile

class ProfileAdapter(
    private val onClick: (Profile) -> Unit,
    private val onClickToDelete: (Profile) -> Unit
) : ListAdapter<Profile, ProfileViewHolder>(DiffUtilItemCallBack) {
    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        getItem(position)?.let { profile ->
            holder.onBind(profile, onClick, onClickToDelete)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ProfileViewHolder.inflate(parent)

    companion object DiffUtilItemCallBack : DiffUtil.ItemCallback<Profile>() {
        override fun areItemsTheSame(oldItem: Profile, newItem: Profile) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Profile, newItem: Profile) = oldItem == newItem
    }
}


