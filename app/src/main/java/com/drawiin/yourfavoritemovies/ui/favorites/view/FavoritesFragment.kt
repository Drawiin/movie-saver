package com.drawiin.yourfavoritemovies.ui.favorites.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.drawiin.yourfavoritemovies.R
import com.drawiin.yourfavoritemovies.coreui.viewBinding
import com.drawiin.yourfavoritemovies.databinding.FragmentFavoritesBinding
import com.drawiin.yourfavoritemovies.databinding.MovieSkeletonLayoutBinding
import com.drawiin.yourfavoritemovies.domain.models.Movie
import com.drawiin.yourfavoritemovies.ui.adapter.FavoritesAdapter
import com.drawiin.yourfavoritemovies.ui.favorites.viewmodel.FavoriteViewModel
import com.drawiin.yourfavoritemovies.utils.getDeviceHeight
import com.drawiin.yourfavoritemovies.utils.getDeviceWidth
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.ceil

@AndroidEntryPoint
class FavoritesFragment : Fragment() {
    private var resultRemove = Movie()
    private var isAppBarExpanded = true

    private val adapter: FavoritesAdapter by lazy {
        FavoritesAdapter(
            this::moveToWatchedMovies
        )
    }

    private val viewModel: FavoriteViewModel by viewModels()

    private val binding by viewBinding(FragmentFavoritesBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        subscribeUi()
        viewModel.init()
    }

    override fun onResume() {
        super.onResume()
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(
                viewLifecycleOwner,
                getOnBackPressedCallback()
            )
    }

    private fun getOnBackPressedCallback() =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() =
                if (binding.nestedScroll.scrollY > 0 || !isAppBarExpanded) {
                    binding.nestedScroll.smoothScrollTo(0, 0)
                    binding.appbar.setExpanded(true, true)
                } else {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }

        }

    private fun setupUi() {
        binding.rvMoviesFavorites.apply {
            adapter = this@FavoritesFragment.adapter
            layoutManager = GridLayoutManager(context, 3)
        }
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.appbar.addOnOffsetChangedListener(OnOffsetChangedListener { _, verticalOffset ->
            isAppBarExpanded = verticalOffset == 0
        })
    }

    private fun subscribeUi() {
        viewModel.stateList.observe(viewLifecycleOwner) { favorites ->
            favorites?.let {
                showListFavorites(it)
            }
        }


        viewModel.stateMovedToWatchedMovies.observe(viewLifecycleOwner) { removed ->
            removed?.let {
                showMessageRemovedFavorite(it)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
    }

    private fun moveToWatchedMovies(movie: Movie) {
        viewModel.moveToWatchedMovies(movie)
    }


    private fun showListFavorites(list: List<Movie>) = adapter.submitList(list)

    private fun showMessageRemovedFavorite(movie: Movie) {
        resultRemove = movie
        Snackbar.make(
            binding.rvMoviesFavorites,
            resources.getString(R.string.moved_movie, movie.title),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun showLoading(status: Boolean) {
        when {
            status -> {
                showShimmerLoading()
            }
            else -> {

                hideShimmerLoading()
            }
        }
    }

    private fun showShimmerLoading() = context?.run {
        val shimmerLoading = binding.shimmerLoading
        val itemMargin = resources.getDimension(R.dimen.default_card_spacing)
        val itemWidth = (getDeviceWidth() / GRID_SPAN_COUNT) - (2 * itemMargin)
        val itemHeight = itemWidth * GRID_ITEM_RATIO_H
        val moviesCount =
            ceil((getDeviceHeight() / (itemHeight + itemMargin * 2)) * GRID_SPAN_COUNT).toInt()

        (0..moviesCount).forEach { _ ->
            shimmerLoading.container.addView(
                MovieSkeletonLayoutBinding.inflate(
                    LayoutInflater.from(context),
                    binding.shimmerLoading.container,
                    false
                ).root.apply {
                    layoutParams.width = itemWidth.toInt()
                })
        }
        shimmerLoading.root.visibility = View.VISIBLE
        shimmerLoading.shimmerLayout.startShimmer()
    }

    private fun hideShimmerLoading() = binding.shimmerLoading.run {
        shimmerLayout.stopShimmer()
        root.visibility = View.GONE
    }

    companion object {
        private const val GRID_SPAN_COUNT = 3
        private const val GRID_ITEM_RATIO_H = 1.5f
    }
}