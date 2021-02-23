package com.drawiin.yourfavoritemovies.ui.authentication.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.drawiin.yourfavoritemovies.R
import com.drawiin.yourfavoritemovies.databinding.FragmentLoginBinding
import com.drawiin.yourfavoritemovies.ui.authentication.viewmodel.AuthenticationViewModel
import com.drawiin.yourfavoritemovies.utils.MovieUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleGoogleSignInResult(task)

            }
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
                findNavController().navigate(R.id.action_loginFragment2_to_registerFragment)
            }

            btnGoogle.setOnClickListener {
                startGoogleLogIn()
            }
        }
        viewModel.loadLoggedUser()
    }


    private fun subscribeUi() {
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

    private fun startGoogleLogIn() {
        resultLauncher.launch(googleSignInClient.signInIntent)
    }

    private fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) = try {
        val account = task.getResult(ApiException::class.java)
        firebaseAuthWithGoogle(account)
    } catch (e: ApiException) {
        Log.w("GOOGLE_SIGN_IN", "signInResult:failed code=" + e.statusCode)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) =
        account?.idToken?.let { token ->
            val credential = GoogleAuthProvider.getCredential(token, null)
            Firebase.auth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity()) { task ->
                    when {
                        task.isSuccessful -> {
                            MovieUtil.saveUserId(
                                requireContext(),
                                Firebase.auth.currentUser?.uid
                            )
                            navigateToHome(true)
                        }
                        else -> {
                            Snackbar.make(
                                binding.btnGoogle,
                                "Authentication Failed.",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
        }


    private fun showErrorMessage(message: String) {
        Snackbar.make(binding.btLogin, message, Snackbar.LENGTH_LONG).show()
    }
}