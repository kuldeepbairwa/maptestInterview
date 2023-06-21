package com.kuldeep.maptestinterview.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Index
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.kuldeep.maptestinterview.room.MyRoomDB
import com.kuldeep.maptestinterview.room.PlaceEntity
import com.kuldeep.maptestinterview.utils.AppConstants
import com.kuldeep.maptestinterview.utils.AppConstants.MAPS_API_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MapRepository(private val room: MyRoomDB) {

    suspend fun getLatLng(
        placesClient: PlacesClient?,
        query: String,
        callback: (ArrayList<PlaceEntity>) -> Unit
    ) {


        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()

            .setCountry("in")
            .setTypeFilter(TypeFilter.ADDRESS)
            .setSessionToken(token)
            .setQuery(query)
            .build()

        placesClient?.findAutocompletePredictions(request)!!
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->

                val list = arrayListOf<PlaceEntity>()

                for (prediction in response.autocompletePredictions) {
                    list.add(
                        PlaceEntity(
                            cityName = prediction.getPrimaryText(null).toString(),
                            address = prediction.getSecondaryText(null).toString(),
                            placeId = prediction.placeId,
                            lat = .0,
                            long = .0,
                            distance = .0
                        )
                    )


                }

                callback.invoke(list)
            }
    }

    suspend fun savePlace(placeEntity: PlaceEntity) {
        room.placeDao().insert(placeEntity)

    }


    suspend fun getDirections(origin: LatLng, destination: LatLng, callback: (String) -> Unit) {

        try {

            val url =
                "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${destination.latitude},${destination.longitude}&key=$MAPS_API_KEY"

            val myUrl = URL(url)
            val httpURLConnection =
                withContext(Dispatchers.IO) {
                    myUrl.openConnection()
                } as HttpURLConnection
            withContext(Dispatchers.IO) {
                httpURLConnection.connect()
            }
            val istm = httpURLConnection.inputStream
            val bufferedReader = BufferedReader(InputStreamReader(istm))
            val stringBuilder = StringBuilder()
            var line: String? = ""
            while (line != null) {
                line = withContext(Dispatchers.IO) {
                    bufferedReader.readLine()
                }
                stringBuilder.append(line)
            }

            val data = stringBuilder.toString()
            val jsonObject = JSONObject(data)
            val jsonObject1 = jsonObject.getJSONArray("routes").getJSONObject(0)
            val result = jsonObject1.getJSONObject("overview_polyline").getString("points")
            callback(result)
        } catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
        }
    }

suspend fun getLatLangPlace(placeId: String, callback: (LatLng?) -> Unit) {

        try {

            val baseUrl =
                "https://maps.googleapis.com/maps/api/place/details/json?placeid=$placeId&key=${AppConstants.MAPS_API_KEY}"

            OkHttpClient().newCall(
                Request.Builder().url(baseUrl).build()
            ).execute().also {
                val resultObj = it.body?.string()
                    ?.let { it1 -> JSONObject(it1).getJSONObject("result") }

                val geometryObj = resultObj?.getJSONObject("geometry")
                val locationObj = geometryObj?.getJSONObject("location")
                val latitude = locationObj?.get("lat").toString().toDouble()
                val longitude = locationObj?.get("lng").toString().toDouble()

                withContext(Dispatchers.Main){
                    callback(LatLng(latitude, longitude))
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main){
                callback(null)
            }
        }

    }

    fun getPlaceList(order: Index.Order): LiveData<List<PlaceEntity>> {

        return if (order == Index.Order.ASC){
            room.placeDao().getLocationsAsc()
        } else{
            room.placeDao().getLocationsDesc()
        }
    }
}