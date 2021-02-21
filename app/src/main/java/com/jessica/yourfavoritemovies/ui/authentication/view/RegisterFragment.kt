package com.jessica.yourfavoritemovies.ui.authentication.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.jessica.yourfavoritemovies.R
import com.jessica.yourfavoritemovies.databinding.FragmentRegisterBinding
import com.jessica.yourfavoritemovies.ui.authentication.viewmodel.AuthenticationViewModel
import com.jessica.yourfavoritemovies.utils.MovieUtil

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

    private val viewModel: AuthenticationViewModel by lazy {
        ViewModelProvider(this).get(
            AuthenticationViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        subscribeUi()
    }

    private fun setupUi() = binding.run {
        buttonGoToLogin.setOnClickListener {
            activity?.onBackPressed()
        }
        btnRegister.setOnClickListener {
            val name = inputName.text.toString()
            val email = inputEmail.text.toString()
            val password = inputPassword.text.toString()

            when {
                MovieUtil.validateNameEmailPassword(name, email, password) -> {
                        viewModel.registerUser(email, password)
                    }
                }
            }
        }


    private fun subscribeUi() {
        viewModel.stateRegister.observe(viewLifecycleOwner) { state ->
            state?.let {
                navigateToHome(it)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            loading?.let {
                showLoading(loading)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showErrorMessage(error)
            }
        }
    }


    private fun navigateToHome(status: Boolean) {
        when {
            status -> {
                findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
            }
        }
    }

    private fun showErrorMessage(message: String) {
        Snackbar.make(binding.btnRegister, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showLoading(status: Boolean) {
        when {
            status -> {
                binding.pbRegister.visibility = View.VISIBLE
            }
            else -> {
                binding.pbRegister.visibility = View.GONE
            }
        }
    }
}