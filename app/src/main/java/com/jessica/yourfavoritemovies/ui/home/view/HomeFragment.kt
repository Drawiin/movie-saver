package com.jessica.yourfavoritemovies.ui.home.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.jessica.yourfavoritemovies.R
import com.jessica.yourfavoritemovies.databinding.FragmentHomeBinding
import com.jessica.yourfavoritemovies.model.Result
import com.jessica.yourfavoritemovies.ui.adapter.MovieAdapter
import com.jessica.yourfavoritemovies.ui.home.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(
            HomeViewModel::class.java
        )
    }

    private val adapter: MovieAdapter by lazy {
        MovieAdapter(
            ArrayList(), this::favoriteMovie
        )
    }

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setupUi()
        subscribeUi()
    }

    private fun setupUi() {
        binding.rvMovies.apply {
            adapter = this@HomeFragment.adapter
            layoutManager = GridLayoutManager(activity, 2)
        }
    }

    private fun subscribeUi() {
        viewModel.stateList.observe(viewLifecycleOwner) { state ->
            state?.let {
                showListMovies(it as MutableList<Result>)
            }
        }

        viewModel.stateFavorite.observe(viewLifecycleOwner) { favorite ->
            favorite?.let {
                showFavoriteMessage(it)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            loading?.let {
                showLoading(it)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { loading ->
            loading?.let {
                showErrorMessage(it)
            }
        }
    }

    private fun showListMovies(list: MutableList<Result>) {
        adapter.updateList(list)
    }

    private fun showFavoriteMessage(result: Result) {
        Snackbar.make(
            binding.rvMovies,
            "Filme ${result.title} adicionado com sucesso",
            Snackbar.LENGTH_LONG
        )
    }

    private fun favoriteMovie(result: Result) {
        viewModel.saveFavorite(result)
    }

    private fun showLoading(status: Boolean) {
        when {
            status -> {
                binding.pbMovies.visibility = View.VISIBLE

            }
            else -> {
                binding.pbMovies.visibility = View.GONE
            }
        }
    }

    private fun showErrorMessage(message: String) {
        Snackbar.make(rv_movies, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_favoritos -> {
                //TODO - Go TO Favorites
                return true
            }
            R.id.action_logout -> {
                logout()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        context?.let {
            AuthUI.getInstance()
                .signOut(it)
                .addOnCompleteListener {
                    //TODO -> Go to Login
                }
        }
    }
}