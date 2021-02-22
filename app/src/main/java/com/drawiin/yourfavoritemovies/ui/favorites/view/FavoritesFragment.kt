package com.drawiin.yourfavoritemovies.ui.favorites.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.snackbar.Snackbar
import com.drawiin.yourfavoritemovies.R
import com.drawiin.yourfavoritemovies.databinding.FragmentFavoritesBinding
import com.drawiin.yourfavoritemovies.databinding.MovieSkeletonLayoutBinding
import com.drawiin.yourfavoritemovies.getDeviceHeight
import com.drawiin.yourfavoritemovies.getDeviceWidth
import com.drawiin.yourfavoritemovies.model.Result
import com.drawiin.yourfavoritemovies.ui.adapter.MovieAdapter
import com.drawiin.yourfavoritemovies.ui.favorites.viewmodel.FavoriteViewModel
import kotlin.math.ceil

class FavoritesFragment : Fragment() {
    private var resultRemove = Result()
    private var isAppBarExpanded = true

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
                showListFavorites(it as MutableList<Result>)
            }
        }

        viewModel.stateRemoveFavorite.observe(viewLifecycleOwner) { removed ->
            removed?.let {
                showMessageRemovedFavorite(it)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            showLoading(it)
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