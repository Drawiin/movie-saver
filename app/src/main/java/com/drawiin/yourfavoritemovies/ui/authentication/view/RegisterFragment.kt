package com.drawiin.yourfavoritemovies.ui.authentication.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.drawiin.yourfavoritemovies.R
import com.drawiin.yourfavoritemovies.databinding.FragmentRegisterBinding
import com.drawiin.yourfavoritemovies.ui.authentication.viewmodel.AuthenticationViewModel
import com.drawiin.yourfavoritemovies.ui.authentication.viewmodel.AuthenticationViewModel.Companion.TAG
import com.drawiin.yourfavoritemovies.utils.MovieUtil
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

    private val viewModel: AuthenticationViewModel by lazy {
        ViewModelProvider(this).get(
            AuthenticationViewModel::class.java
        )
    }

    private val googleSignInClient by lazy {
        GoogleSignIn.getClient(requireActivity(), viewModel.googleSignInOptions)
    }

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var callbackManager: CallbackManager


    override fun onAttach(context: Context) {
        super.onAttach(context)
        registerGoogleLogInActivity()
        callbackManager = CallbackManager.Factory.create()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setupUi() = binding.run {
        buttonGoToLogin.setOnClickListener {
            findNavController().popBackStack(R.id.loginFragment2, false)
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
        btnGoogle.setOnClickListener { startGoogleLogIn() }
        btnFacebook.setOnClickListener { startFacebookLogin() }
    }


    private fun subscribeUi() {
        viewModel.stateRegister.observe(viewLifecycleOwner) { state ->
            state?.let {
                navigateToHome(it)
            }
        }

        viewModel.passwordLoading.observe(viewLifecycleOwner) { loading ->
            loading?.let {
                showRegisterLoading(loading)
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

    private fun registerGoogleLogInActivity() {
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleGoogleSignInResult(task)
                }
            }
        }
    }

    private fun startGoogleLogIn() = resultLauncher.launch(googleSignInClient.signInIntent)

    private fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) = try {
        val account = task.getResult(ApiException::class.java)
        viewModel.firebaseAuthWithGoogle(account, requireActivity())
    } catch (e: ApiException) {
        Log.w("GOOGLE_SIGN_IN", "signInResult:failed code=" + e.statusCode)
    }

    private fun startFacebookLogin() = LoginManager.getInstance().run {
        logInWithReadPermissions(this@RegisterFragment, listOf("email", "public_profile"))
        registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                viewModel.firebaseAuthWithFacebook(loginResult.accessToken)

            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
            }

        })
    }

    private fun showErrorMessage(message: String) {
        Snackbar.make(binding.btnRegister, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showRegisterLoading(status: Boolean) {
        when {
            status -> {
                showButtonLoading(binding.btnRegister, binding.registerProgress)
            }
            else -> {
                hideButtonLoading(binding.btnRegister, binding.registerProgress)
            }
        }
    }

    private fun showButtonLoading(button: Button, progress: CircularProgressIndicator) {
        progress.show()
        button.text = ""
    }

    private fun hideButtonLoading(button: Button, progress: CircularProgressIndicator) {
        progress.hide()
        button.alpha = 1.0f
    }
}