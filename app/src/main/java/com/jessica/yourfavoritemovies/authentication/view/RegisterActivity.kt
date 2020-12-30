package com.jessica.yourfavoritemovies.authentication.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.jessica.yourfavoritemovies.MovieUtil
import com.jessica.yourfavoritemovies.R
import com.jessica.yourfavoritemovies.authentication.viewmodel.AuthenticationViewModel
import com.jessica.yourfavoritemovies.databinding.ActivityRegisterBinding
import com.jessica.yourfavoritemovies.home.view.HomeActivity
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val viewModel: AuthenticationViewModel by lazy {
        ViewModelProvider(this).get(
            AuthenticationViewModel::class.java
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        setupUi()
        subscribeUi()
    }

    private fun setupUi() {
        binding.run {
            btnRegister.setOnClickListener {
                val name = etvNameRegister.text.toString()
                val email = etvEmailRegister.text.toString()
                val password = etvPasswordRegister.text.toString()

                when {
                    MovieUtil.validateNameEmailPassword(name, email, password) -> {
                        viewModel.registerUser(email, password)
                    }
                }
            }
        }
    }

    private fun subscribeUi() {
        viewModel.stateRegister.observe(this) { state ->
            state?.let {
                navigateToHome(it)
            }
        }

        viewModel.loading.observe(this) { loading ->
            loading?.let {
                showLoading(loading)
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                showErrorMessage(error)
            }
        }
    }


    private fun navigateToHome(status: Boolean) {
        when {
            status -> {
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }
    }

    private fun showErrorMessage(message: String) {
        Snackbar.make(btn_register, message, Snackbar.LENGTH_LONG).show()
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