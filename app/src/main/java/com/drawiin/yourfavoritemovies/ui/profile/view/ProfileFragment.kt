package com.drawiin.yourfavoritemovies.ui.profile.view

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.drawiin.yourfavoritemovies.R
import com.drawiin.yourfavoritemovies.databinding.FragmentProfileBinding
import com.drawiin.yourfavoritemovies.domain.models.Profile
import com.drawiin.yourfavoritemovies.ui.adapter.ProfileAdapter
import com.drawiin.yourfavoritemovies.ui.profile.viewmodel.ProfileViewModel
import com.firebase.ui.auth.AuthUI
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val viewModel: ProfileViewModel by viewModels()
    private val adapter = ProfileAdapter(::goToHome, ::deleteProfile)

    private lateinit var binding: FragmentProfileBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        setupUi()
        subscribeUi()
        return binding.root
    }

    private fun setupUi() = with(binding) {
        toolbar.setOnMenuItemClickListener { onMenuItemClicked(it) }
        rvProfiles.layoutManager = GridLayoutManager(requireContext(), 2)
        rvProfiles.adapter = this@ProfileFragment.adapter
    }

    private fun subscribeUi() {
        viewModel.profiles.observe(viewLifecycleOwner) { profiles ->
            adapter.submitList(profiles)
        }
    }

    private fun onMenuItemClicked(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.action_logout -> {
            logout()
            true
        }
        R.id.action_add -> {
            openCreateProfileDialog()
            true
        }
        else -> false
    }

    private fun goToHome(profile: Profile) {
        viewModel.saveCurrentProfileId(profile.id)
        findNavController().navigate(
            R.id.action_profileFragment_to_homeFragment
        )
    }

    private fun logout() = context?.let {
        AuthUI.getInstance().signOut(requireContext()).addOnCompleteListener {
            findNavController().navigate(R.id.goToLogin)
        }
    }

    private fun openCreateProfileDialog() {
        var name = ""
        val input = EditText(requireContext()).apply {
            inputType = InputType.TYPE_CLASS_TEXT
        }
        AlertDialog.Builder(context).apply {
            setTitle("Criar perfil")
            setView(input)
            setPositiveButton("Criar") { _, _ ->
                name = input.text.toString()
                viewModel.createProfile(name)
            }
            setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
        }.show()
    }

    private fun deleteProfile(profile: Profile) {
        viewModel.deleteProfile(profile)
    }
}