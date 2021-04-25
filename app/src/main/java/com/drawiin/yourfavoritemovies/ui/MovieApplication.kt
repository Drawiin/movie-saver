package com.drawiin.yourfavoritemovies.ui

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MovieApplication @Inject constructor(): Application() {}