package com.kuldeep.maptestinterview.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Index
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.net.PlacesClient
import com.kuldeep.maptestinterview.repository.MapRepository
import com.kuldeep.maptestinterview.room.PlaceEntity
import com.kuldeep.maptestinterview.utils.AppFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel(private val mapRepository: MapRepository) : ViewModel() {

    private var _order = MutableLiveData<Index.Order>()
    val orderLiveData:LiveData<Index.Order> get() = _order
    lateinit var livePlaces : LiveData<List<PlaceEntity>>
    private lateinit var orderObserver: Observer<Index.Order>

    fun getDirections(origin: LatLng, destination: LatLng, callback: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            mapRepository.getDirections(origin, destination, callback)
        }
    }

    fun savePlace(place: PlaceEntity?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (place != null)
                mapRepository.savePlace(place)
        }
    }

    fun getLatLng(
        placesClient: PlacesClient?,
        q: String,
        callback: (ArrayList<PlaceEntity>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            mapRepository.getLatLng(placesClient, q, callback)
        }
    }

    fun getDistance(latLng: LatLng?, activity: AppCompatActivity): Double {

        val destLocation = Location("")

        destLocation.latitude = latLng?.latitude ?: .0
        destLocation.longitude = latLng?.longitude ?: .0

        val distance = getMyLocation(activity).distanceTo(destLocation) //in meters

        return distance.toDouble() / 1000
    }

    fun getMyLocation(activity: AppCompatActivity): Location  {

        val myLatLng = getMyLatLng(activity)
        val myLocation = Location("")
        myLocation.latitude = myLatLng.latitude
        myLocation.longitude = myLatLng.longitude

        return myLocation
    }

    fun getMyLatLng(activity: AppCompatActivity): LatLng {
        val locationManager =
            activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationProvider = LocationManager.NETWORK_PROVIDER

        @SuppressLint("MissingPermission")
        val lastKnownLocation = locationManager.getLastKnownLocation(locationProvider)
        val userLat = lastKnownLocation?.latitude
        val userLong = lastKnownLocation?.longitude

        return LatLng(userLat ?: .0, userLong ?: .0)
    }

    fun getLatLangPlace(placeId:String,callback: (LatLng?) -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            mapRepository.getLatLangPlace(placeId,callback)
        }
    }

    init {
        viewModelScope.launch {
            livePlaces = mapRepository.getPlaceList(Index.Order.ASC)
        }
    }
}