package com.drawiin.yourfavoritemovies.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drawiin.yourfavoritemovies.databinding.ItemProfileBinding
import com.drawiin.yourfavoritemovies.domain.models.Profile

class ProfileViewHolder(val binding: ItemProfileBinding) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(profile: Profile, onClick: (Profile) -> Unit, onClickToDelete: (Profile) -> Unit) {
       binding.btnProfile.text = profile.name
        binding.btnProfile.setOnClickListener {
            onClick(profile)
        }
        binding.btnDelete.setOnClickListener {
            onClickToDelete(profile)
        }
    }

    companion object {
        fun inflate(parent: ViewGroup) = ProfileViewHolder(
            ItemProfileBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}