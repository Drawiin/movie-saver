package com.drawiin.yourfavoritemovies.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.drawiin.yourfavoritemovies.R
import com.drawiin.yourfavoritemovies.coreui.viewBinding
import com.drawiin.yourfavoritemovies.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_YourFavoriteMovies)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.run {
            navigationBarColor = ContextCompat.getColor(this@MainActivity, R.color.purple)
            statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.green)
        }
    }
}