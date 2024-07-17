package com.drawiin.yourfavoritemovies.ui.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.drawiin.yourfavoritemovies.R
import com.drawiin.yourfavoritemovies.coreui.viewBinding
import com.drawiin.yourfavoritemovies.databinding.FragmentHomeBinding
import com.drawiin.yourfavoritemovies.databinding.MovieSkeletonLayoutBinding
import com.drawiin.yourfavoritemovies.domain.models.Movie
import com.drawiin.yourfavoritemovies.ui.adapter.PagedMoviesAdapter
import com.drawiin.yourfavoritemovies.ui.home.viewmodel.HomeViewModel
import com.drawiin.yourfavoritemovies.utils.getDeviceHeight
import com.drawiin.yourfavoritemovies.utils.getDeviceWidth
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.ceil


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    private val adapter: PagedMoviesAdapter by lazy {
        PagedMoviesAdapter(this::moveToWatchList)
    }

    private val binding by viewBinding(FragmentHomeBinding::inflate)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        subscribeUi()
    }

    private fun setupUi() = binding.run {
        rvMovies.apply {
            adapter = this@HomeFragment.adapter
            layoutManager = GridLayoutManager(activity, GRID_SPAN_COUNT)
        }
        toolbar.setOnMenuItemClickListener { onMenuItemClicked(it) }
    }

    private fun onMenuItemClicked(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.action_favorites -> {
            findNavController().navigate(R.id.action_homeFragment_to_favoritesFragment)
            true
        }
        R.id.action_logout -> {
            exitProfile()
            true
        }
        R.id.action_search -> {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
            false
        }

        R.id.action_watched -> {
            findNavController().navigate(R.id.action_homeFragment_to_whatchedFragment)
            false
        }
        else -> false
    }

    private fun subscribeUi() = viewModel.run {
        stateList.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { flow ->
                this@HomeFragment.lifecycleScope.launch {
                    flow.collectLatest(adapter::submitData)
                }
            }
        }

        loading.observe(viewLifecycleOwner) {
            it?.let(::showLoading)
        }

        message.observe(viewLifecycleOwner) { error ->
            error?.getContentIfNotHandled()?.let {
                showErrorMessage(it)
            }
        }

        currentProfile.observe(viewLifecycleOwner) { profile ->
            binding.toolbar.title = profile.name
        }
    }

    private fun moveToWatchList(movie: Movie) {
        viewModel.saveToWatchList(movie)
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
            shimmerLoading.container.addView(MovieSkeletonLayoutBinding.inflate(
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

    private fun showErrorMessage(message: String) {
        Snackbar.make(binding.rvMovies, message, Snackbar.LENGTH_LONG).show()
    }

    private fun exitProfile() {
        findNavController().popBackStack()
    }


    companion object {
        const val GRID_SPAN_COUNT = 3
        const val GRID_ITEM_RATIO_H = 1.5f
    }
}