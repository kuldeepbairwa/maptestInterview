package com.kuldeep.maptestinterview.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.kuldeep.maptestinterview.utils.AppConstants.ACCESS_FINE_LOCATION_PERMISSION
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object AppFunctions {

    fun checkLocationPermissions(activity: AppCompatActivity, isGranted: (isGranted: Boolean) -> Unit) {

        if (ContextCompat.checkSelfPermission(activity,android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            isGranted(false)
            ActivityCompat.requestPermissions(activity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                ACCESS_FINE_LOCATION_PERMISSION)

        } else {
            isGranted(true)
        }
    }
}