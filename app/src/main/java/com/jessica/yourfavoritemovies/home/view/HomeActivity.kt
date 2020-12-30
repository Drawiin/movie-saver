package com.jessica.yourfavoritemovies.home.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.jessica.yourfavoritemovies.Constants.LANGUAGE_PT_BR
import com.jessica.yourfavoritemovies.R
import com.jessica.yourfavoritemovies.adapter.MovieAdapter
import com.jessica.yourfavoritemovies.authentication.view.LoginActivity
import com.jessica.yourfavoritemovies.databinding.ActivityHomeBinding
import com.jessica.yourfavoritemovies.favorites.view.FavoritesActivity
import com.jessica.yourfavoritemovies.home.viewmodel.HomeViewModel
import com.jessica.yourfavoritemovies.model.Result
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
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

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        setupUi()
        subscribeUi()
    }

    private fun setupUi() {
        binding.rvMovies.apply {
            adapter = this@HomeActivity.adapter
            layoutManager = GridLayoutManager(this@HomeActivity, 2)
        }
    }

    private fun subscribeUi() {
        viewModel.stateList.observe(this) { state ->
            state?.let {
                showListMovies(it as MutableList<Result>)
            }
        }

        viewModel.stateFavorite.observe(this) { favorite ->
            favorite?.let {
                showFavoriteMessage(it)
            }
        }

        viewModel.loading.observe(this) { loading ->
            loading?.let {
                showLoading(it)
            }
        }

        viewModel.error.observe(this) { loading ->
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
                pb_movies.visibility = View.VISIBLE
            }
            else -> {
                pb_movies.visibility = View.GONE
            }
        }
    }

    private fun showErrorMessage(message: String) {
        Snackbar.make(rv_movies, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_favoritos -> {
                startActivity(Intent(this, FavoritesActivity::class.java))
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
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                startActivity(
                    Intent(
                        this,
                        LoginActivity::class.java
                    )
                )
                finish()
            }

    }
}