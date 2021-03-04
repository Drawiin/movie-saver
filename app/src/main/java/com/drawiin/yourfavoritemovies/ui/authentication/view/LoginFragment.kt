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
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.drawiin.yourfavoritemovies.R
import com.drawiin.yourfavoritemovies.databinding.FragmentLoginBinding
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding


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
        binding = FragmentLoginBinding.inflate(inflater, container, false)
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

    private fun setupUi() {
        binding.run {
            btLogin.setOnClickListener {
                val email = inputEmail.text.toString()
                val password = inputPassword.text.toString()
                when {
                    MovieUtil.validateEmailPassword(email, password) -> {
                        viewModel.logInUser(email, password)
                    }
                }
            }

            buttonGoToLogin.setOnClickListener {
                val extras = FragmentNavigatorExtras(
                    binding.layoutEmail to "layout_email",
                    binding.layoutPassword to "layout_password",
                    binding.separator to "separator",
                    binding.btnGoogle to "btn_google",
                    binding.btnFacebook to "btn_facebook",
                    binding.btLogin to "btn_action"
                )
                findNavController().navigate(
                    R.id.action_loginFragment2_to_registerFragment,
                    null,
                    null,
                    extras
                )
            }

            btnGoogle.setOnClickListener {
                startGoogleLogIn()
            }

            btnFacebook.setOnClickListener {
                startFacebookLogin()
            }
        }
        viewModel.loadLoggedUser()
    }


    private fun subscribeUi() {
        viewModel.passwordLoading.observe(viewLifecycleOwner) { loading ->
            loading?.let {
                showRegisterLoading(loading)
            }
        }

        viewModel.googleLoading.observe(viewLifecycleOwner) { loading ->
            loading?.let {
                showGoogleLoading(loading)
            }
        }

        viewModel.facebookLoading.observe(viewLifecycleOwner) { loading ->
            loading?.let {
                showFacebookLoading(loading)
            }
        }

        viewModel.stateLogin.observe(viewLifecycleOwner) { state ->
            state?.let {
                navigateToHome(it)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showErrorMessage(it)
            }
        }
    }

    private fun navigateToHome(status: Boolean) {
        if (!status) return
        when {
            status -> {
                findNavController().navigate(R.id.action_loginFragment2_to_homeFragment)
            }
        }
    }


    //TODO - NOVO JEITO DE STARTAR UMA ACTIVITY FOR RESULT
    private fun registerGoogleLogInActivity() {
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleGoogleSignInResult(task)
                }
                else -> viewModel.hideGoogleLoading()
            }
        }
    }

    private fun startGoogleLogIn() {
        viewModel.showGoogleLoading()
        resultLauncher.launch(googleSignInClient.signInIntent)
    }


    private fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) = try {
        val account = task.getResult(ApiException::class.java)
        viewModel.firebaseAuthWithGoogle(account, requireActivity())
    } catch (e: ApiException) {
        Log.w("GOOGLE_SIGN_IN", "signInResult:failed code=" + e.statusCode)
        viewModel.hideGoogleLoading()
    }

    private fun startFacebookLogin() = LoginManager.getInstance().run {
        viewModel.showFacebookLoading()
        logInWithReadPermissions(this@LoginFragment, listOf("email", "public_profile"))
        registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                viewModel.firebaseAuthWithFacebook(loginResult.accessToken)

            }

            override fun onCancel() {
                viewModel.hideFacebookLoading()
            }

            override fun onError(error: FacebookException) {
                viewModel.hideFacebookLoading()
            }

        })
    }

    private fun showRegisterLoading(status: Boolean) {
        when {
            status -> {
                showButtonLoading(binding.btLogin, binding.loginProgress)
            }
            else -> {
                hideButtonLoading(
                    binding.btLogin,
                    binding.loginProgress,
                    getString(R.string.register)
                )
            }
        }
    }

    private fun showGoogleLoading(status: Boolean) {
        when {
            status -> {
                showButtonLoading(binding.btnGoogle, binding.googleProgress)
            }
            else -> {
                hideButtonLoading(
                    binding.btnGoogle,
                    binding.googleProgress,
                    getString(R.string.continue_with_google),
                    R.drawable.fui_ic_googleg_color_24dp
                )
            }
        }
    }

    private fun showFacebookLoading(status: Boolean) {
        when {
            status -> {
                showButtonLoading(binding.btnFacebook, binding.facebookProgress)
            }
            else -> {
                hideButtonLoading(
                    binding.btnFacebook,
                    binding.facebookProgress,
                    getString(R.string.continue_with_facebook),
                    R.drawable.ic_baseline_facebook_24
                )
            }
        }
    }

    private fun showButtonLoading(button: Button, progress: CircularProgressIndicator) {
        progress.show()
        button.text = ""
        if (button is MaterialButton) {
            button.icon = null
        }
    }

    private fun hideButtonLoading(
        button: Button,
        progress: CircularProgressIndicator,
        text: String,
        drawable: Int? = null
    ) {
        progress.hide()
        button.alpha = 1.0f
        button.text = text
        if (button is MaterialButton && drawable != null) {
            button.icon = ResourcesCompat.getDrawable(requireContext().resources, drawable, null)
        }
    }

    private fun showErrorMessage(message: String) {
        Snackbar.make(binding.btLogin, message, Snackbar.LENGTH_LONG).show()
    }

}