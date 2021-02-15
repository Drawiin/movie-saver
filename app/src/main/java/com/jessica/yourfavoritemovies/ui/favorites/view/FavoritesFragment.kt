package com.jessica.yourfavoritemovies.ui.favorites.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.jessica.yourfavoritemovies.R
import com.jessica.yourfavoritemovies.databinding.FragmentFavoritesBinding
import com.jessica.yourfavoritemovies.model.Result
import com.jessica.yourfavoritemovies.ui.adapter.MovieAdapter
import com.jessica.yourfavoritemovies.ui.favorites.viewmodel.FavoriteViewModel

class FavoritesFragment : Fragment() {
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

    private lateinit var binding: FragmentFavoritesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        subscribeUi()
    }

    private fun setupUi() {
        binding.rvMoviesFavorites.apply {
            adapter = this@FavoritesFragment.adapter
            layoutManager = GridLayoutManager(context, 3)
        }
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun subscribeUi() {
        viewModel.stateList.observe(viewLifecycleOwner) { favorites ->
            favorites?.let {
                showListFavorites(it as MutableList<Result>)
            }
        }

        viewModel.stateRemoveFavorite.observe(viewLifecycleOwner) { removed ->
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
            binding.rvMoviesFavorites,
            resources.getString(R.string.removed_movie, result.title),
            Snackbar.LENGTH_LONG
        ).show()
    }
}