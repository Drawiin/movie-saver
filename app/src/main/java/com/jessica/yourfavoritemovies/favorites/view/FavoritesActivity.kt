package com.jessica.yourfavoritemovies.favorites.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.jessica.yourfavoritemovies.R
import com.jessica.yourfavoritemovies.adapter.MovieAdapter
import com.jessica.yourfavoritemovies.databinding.ActivityFavoritesBinding
import com.jessica.yourfavoritemovies.favorites.viewmodel.FavoriteViewModel
import com.jessica.yourfavoritemovies.model.Result
import kotlinx.android.synthetic.main.activity_favorites.*

class FavoritesActivity : AppCompatActivity() {
    private var resultRemove = Result()
    private val adapter: MovieAdapter by lazy {
        MovieAdapter(
            ArrayList(), this::removeFavoriteMovie
        )
    }

    private val viewModel: FavoriteViewModel by lazy {
        ViewModelProvider(this).get(
            FavoriteViewModel::class.java
        )
    }

    private lateinit var binding: ActivityFavoritesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_favorites)
        setupUi()
        subscribeUi()
    }

    private fun setupUi() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        binding.rvMoviesFavorites.apply {
            adapter = this@FavoritesActivity.adapter
            layoutManager = LinearLayoutManager(this@FavoritesActivity)
        }
    }

    private fun subscribeUi() {
        viewModel.stateList.observe(this) {favorites ->
            favorites?.let {
                showListFavorites(it as MutableList<Result>)
            }
        }

        viewModel.stateRemoveFavorite.observe(this) {removed ->
            removed?.let {
                showMessageRemovedFavorite(it)
            }
        }
    }

    private fun removeFavoriteMovie(result: Result) {
        viewModel.removeFavorite(result)
    }


    private fun showListFavorites(list: MutableList<Result>) {
        adapter.removeItem(resultRemove)
        adapter.updateList(list)
    }

    private fun showMessageRemovedFavorite(result: Result) {
        resultRemove = result
        Snackbar.make(
            rv_movies_favorites,
            resources.getString(R.string.removed_movie, result.title),
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}