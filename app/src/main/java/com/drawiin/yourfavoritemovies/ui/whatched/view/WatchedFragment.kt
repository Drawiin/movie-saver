package com.drawiin.yourfavoritemovies.ui.whatched.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.drawiin.yourfavoritemovies.R
import com.drawiin.yourfavoritemovies.databinding.FragmentWatchedBinding
import com.drawiin.yourfavoritemovies.databinding.MovieSkeletonLayoutBinding
import com.drawiin.yourfavoritemovies.domain.models.Movie
import com.drawiin.yourfavoritemovies.ui.adapter.FavoritesAdapter
import com.drawiin.yourfavoritemovies.ui.whatched.viewmodel.WatchedViewModel
import com.drawiin.yourfavoritemovies.utils.getDeviceHeight
import com.drawiin.yourfavoritemovies.utils.getDeviceWidth
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.ceil

@AndroidEntryPoint
class WatchedFragment : Fragment() {
    private var resultRemove = Movie()
    private var isAppBarExpanded = true

    private val adapter: FavoritesAdapter by lazy {
        FavoritesAdapter(
            {}
        )
    }

    private val viewModel: WatchedViewModel by viewModels()

    private lateinit var binding:
            FragmentWatchedBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentWatchedBinding.inflate(inflater, container, false)
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
            adapter = this@WatchedFragment.adapter
            layoutManager = GridLayoutManager(context, 3)
        }
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.appbar.addOnOffsetChangedListener(OnOffsetChangedListener { _, verticalOffset ->
            isAppBarExpanded = verticalOffset == 0
        })

        binding.toolbar.setOnMenuItemClickListener { onMenuItemClicked(it) }
    }

    private fun subscribeUi() {
        viewModel.stateList.observe(viewLifecycleOwner) { favorites ->
            favorites?.let {
                showListFavorites(it)
            }
        }


        viewModel.loading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
    }


    private fun onMenuItemClicked(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.action_share -> {
            share()
            true
        }
        else -> false
    }

    private fun showListFavorites(list: List<Movie>) = adapter.submitList(list)

    private fun share() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "Hey olha só eu já assisti ${viewModel.stateList.value?.size ?: 0} filmes no "
            )
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        activity?.startActivity(shareIntent)
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