package com.kuldeep.maptestinterview.view

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.kuldeep.maptestinterview.R
import com.kuldeep.maptestinterview.utils.AppConstants
import com.kuldeep.maptestinterview.utils.AppFunctions

class MainActivity : AppCompatActivity() {


    companion object {

        val isLocationPermissionGranted = MutableLiveData<Boolean>()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppFunctions.checkLocationPermissions(this) {
            isLocationPermissionGranted.postValue(it)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == AppConstants.ACCESS_FINE_LOCATION_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionGranted.postValue(true)
        } else {
            isLocationPermissionGranted.postValue(false)
        }

    }
}