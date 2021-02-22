package com.drawiin.yourfavoritemovies.ui.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.firebase.ui.auth.AuthUI
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.drawiin.yourfavoritemovies.R
import com.drawiin.yourfavoritemovies.databinding.FragmentHomeBinding
import com.drawiin.yourfavoritemovies.databinding.MovieSkeletonLayoutBinding
import com.drawiin.yourfavoritemovies.getDeviceHeight
import com.drawiin.yourfavoritemovies.getDeviceWidth
import com.drawiin.yourfavoritemovies.model.Result
import com.drawiin.yourfavoritemovies.ui.adapter.MovieAdapter
import com.drawiin.yourfavoritemovies.ui.home.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlin.math.ceil


class HomeFragment : Fragment() {
    private var isAppBarExpanded = true

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

    override fun onResume() {
        super.onResume()
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(
                viewLifecycleOwner,
                getOnBackPressedCallback()
            )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        subscribeUi()
    }

    private fun getOnBackPressedCallback() =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() =
                if (binding.nestedScroll.scrollY > 0 || !isAppBarExpanded) {
                    binding.nestedScroll.smoothScrollTo(0, 0)
                    binding.appbar.setExpanded(true, true)
                } else {
                    isEnabled = false
                    requireActivity().finishAndRemoveTask()
                }

        }

    private fun setupUi() {
        binding.rvMovies.apply {
            adapter = this@HomeFragment.adapter
            layoutManager = GridLayoutManager(activity, GRID_SPAN_COUNT)
        }
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_favoritos -> {
                    findNavController().navigate(R.id.action_homeFragment_to_favoritesFragment)
                    true
                }
                R.id.action_logout -> {
                    logout()
                    true
                }
                else -> false
            }
        }

        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset: Int ->
            isAppBarExpanded = verticalOffset == 0
        })
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
            loading?.getContentIfNotHandled()?.let {
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
        Snackbar.make(rv_movies, message, Snackbar.LENGTH_LONG).show()
    }

    private fun logout() = context?.let {
        AuthUI.getInstance()
            .signOut(it)
            .addOnCompleteListener {
                findNavController().navigate(R.id.goToLogin)
            }
    }


    companion object {
        private const val GRID_SPAN_COUNT = 3
        private const val GRID_ITEM_RATIO_H = 1.5f
    }
}