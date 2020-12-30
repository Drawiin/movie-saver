package com.jessica.yourfavoritemovies.authentication.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.jessica.yourfavoritemovies.MovieUtil
import com.jessica.yourfavoritemovies.R
import com.jessica.yourfavoritemovies.authentication.viewmodel.AuthenticationViewModel
import com.jessica.yourfavoritemovies.databinding.ActivityLoginBinding
import com.jessica.yourfavoritemovies.home.view.HomeActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding


    private val viewModel: AuthenticationViewModel by lazy {
        ViewModelProvider(this).get(
            AuthenticationViewModel::class.java
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        setupUi()
        subscribeUi()
    }

    private fun setupUi() {
        binding.run {
            btLogin.setOnClickListener {
                val email = etvEmail.text.toString()
                val password = etvPassword.text.toString()
                when {
                    MovieUtil.validateEmailPassword(email, password) -> {
                        viewModel.logInUser(email, password)
                    }
                }
            }

            tvLoginRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }
    }

    private fun subscribeUi() {
        viewModel.stateLogin.observe(this) { state ->
            state?.let {
                navigateToHome(it)
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                showErrorMessage(it)
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
        Snackbar.make(bt_login, message, Snackbar.LENGTH_LONG).show()
    }
}