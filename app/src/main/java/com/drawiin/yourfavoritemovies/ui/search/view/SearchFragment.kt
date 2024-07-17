package com.drawiin.yourfavoritemovies.ui.search.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.drawiin.yourfavoritemovies.R
import com.drawiin.yourfavoritemovies.coreui.viewBinding
import com.drawiin.yourfavoritemovies.databinding.FragmentSearchBinding
import com.drawiin.yourfavoritemovies.databinding.MovieSkeletonLayoutBinding
import com.drawiin.yourfavoritemovies.domain.models.Movie
import com.drawiin.yourfavoritemovies.ui.adapter.PagedMoviesAdapter
import com.drawiin.yourfavoritemovies.ui.home.view.HomeFragment
import com.drawiin.yourfavoritemovies.ui.search.viewmodel.SearchViewModel
import com.drawiin.yourfavoritemovies.utils.getDeviceHeight
import com.drawiin.yourfavoritemovies.utils.getDeviceWidth
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.ceil


@AndroidEntryPoint
class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModels()
    private val adapter: PagedMoviesAdapter by lazy {
        PagedMoviesAdapter(this::moveToWatchList)
    }

    private val binding by viewBinding(FragmentSearchBinding::inflate)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupUi()
        subscribeUi()
        return binding.root
    }

    private fun setupUi() = with(binding) {
        btnSubmit.setOnClickListener {
            Log.d("SEARCH_C", inputQuery.text.toString())
            inputQuery
                .text
                .toString()
                .let(viewModel::searchMovies)


        }

        btnClose.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.rvMovies.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvMovies.adapter = adapter
    }

    private fun subscribeUi() {
        viewModel.loading.observe(viewLifecycleOwner) {
            it?.let(::showLoading)
        }
        viewModel.stateList.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { flow ->
                lifecycleScope.launch {
                    flow.collectLatest(adapter::submitData)
                }
            }
        }
        viewModel.message.observe(viewLifecycleOwner) { error ->
            error?.getContentIfNotHandled()?.let {
                showMessage(it)
            }
        }
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.rvMovies, message, Snackbar.LENGTH_LONG).show()
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
        val itemWidth = (getDeviceWidth() / HomeFragment.GRID_SPAN_COUNT) - (2 * itemMargin)
        val itemHeight = itemWidth * HomeFragment.GRID_ITEM_RATIO_H
        val moviesCount =
            ceil((getDeviceHeight() / (itemHeight + itemMargin * 2)) * HomeFragment.GRID_SPAN_COUNT).toInt()

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

    private fun moveToWatchList(movie: Movie) {
        viewModel.saveToWatchList(movie)
    }
}