package com.drawiin.yourfavoritemovies.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.drawiin.yourfavoritemovies.R
import com.drawiin.yourfavoritemovies.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_YourFavoriteMovies)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        window.run {
            navigationBarColor = ContextCompat.getColor(this@MainActivity, R.color.purple)
            statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.green)
        }
    }
}