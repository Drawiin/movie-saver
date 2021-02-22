package com.drawiin.yourfavoritemovies

import android.content.Context

fun Context.getDeviceHeight() = resources.displayMetrics.heightPixels

fun Context.getDeviceWidth() = resources.displayMetrics.widthPixels
