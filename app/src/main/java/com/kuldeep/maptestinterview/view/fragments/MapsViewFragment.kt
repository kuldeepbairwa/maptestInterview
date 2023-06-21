package com.kuldeep.maptestinterview.view.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.PolyUtil
import com.kuldeep.maptestinterview.R
import com.kuldeep.maptestinterview.databinding.FragmentMapsViewBinding
import com.kuldeep.maptestinterview.repository.MapRepository
import com.kuldeep.maptestinterview.room.PlaceEntity
import com.kuldeep.maptestinterview.utils.AppConstants
import com.kuldeep.maptestinterview.utils.AppConstants.IS_ROUTE_MODE
import com.kuldeep.maptestinterview.utils.AppConstants.LOCATION_ID
import com.kuldeep.maptestinterview.utils.OnItemClick
import com.kuldeep.maptestinterview.view.adapters.AdapterSearchPlace
import com.kuldeep.maptestinterview.viewmodels.MapViewModel
import com.kuldeep.maptestinterview.viewmodels.viewmodelFactory.MapViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsViewFragment : BaseFragment<FragmentMapsViewBinding>(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMarkerDragListener,
    GoogleMap.OnInfoWindowClickListener {
    private lateinit var mapViewModel: MapViewModel
    private var googleMap: GoogleMap? = null
    private var location: PlaceEntity? = null
    private var locationId: Long? = null
    private var isEdit: Boolean = false
    private var isRouteMode: Boolean = false
    private var placesClient: PlacesClient? = null

    override fun initVariables() {
        Places.initialize(mContext, AppConstants.MAPS_API_KEY)
        placesClient = Places.createClient(mContext)
        val supportMapFragment =
            childFragmentManager.findFragmentByTag("mapFragment") as SupportMapFragment?
        supportMapFragment?.getMapAsync(this)



        isRouteMode = arguments?.getBoolean(IS_ROUTE_MODE, false) ?: false
        isEditMode()
    }

    override fun bindViewModels() {
        mapViewModel =
            ViewModelProvider(
                this,
                MapViewModelFactory(mContext, MapRepository(room!!))
            )[MapViewModel::class.java]
    }


    private fun isEditMode() {

        locationId = arguments?.getLong(LOCATION_ID)

        Log.d("isEditMode", "ifEditMode: $locationId")

        if (locationId != null) {
            location = room?.placeDao()?.getLocationById(locationId!!)
            if (location != null && location?.id != null) {
                isEdit = true
                Log.d("IS EDIT MODE", "isEditMode: ${location?.distance}")
            }
        }

    }

    override fun getViewBinding() = FragmentMapsViewBinding.inflate(layoutInflater)

    override fun listeners() {
        binding.tb.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.save.setOnClickListener {
            if (location != null) {
                mapViewModel.savePlace(location)
                findNavController().popBackStack()
            }
        }


        binding.etSearch.addTextChangedListener {

            binding.rvSearch.visibility = View.VISIBLE
            findPlaces(it.toString())
        }
    }

    private fun findPlaces(query: String) {

        mapViewModel.getLatLng(placesClient, query) { list ->
            location = if (location == null) PlaceEntity() else location


            binding.rvSearch.adapter =
                AdapterSearchPlace(list, object : OnItemClick<PlaceEntity> {

                    override fun onClick(item: PlaceEntity) {
                        binding.rvSearch.visibility = View.GONE
                        binding.map.requestFocus()
                        item.placeId?.let {
                            mapViewModel.getLatLangPlace(placeId = it) { latLng ->

                                location = if (location == null) PlaceEntity() else location
                                location.apply {
                                    this?.distance = item.distance
                                    this?.address = item.address
                                    this?.cityName = item.cityName
                                    this?.placeId = item.placeId
                                    this?.lat = latLng?.latitude ?: .0
                                    this?.long = latLng?.longitude ?: .0
                                    this?.distance = mapViewModel.getDistance(
                                        latLng,
                                        activity as AppCompatActivity
                                    )
                                }
                                Log.d(
                                    "FIND PLACES",
                                    "findPlaces: ${latLng?.latitude} ${latLng?.longitude}"
                                )

                                focusOnPlace()
                                binding.saveContainer.isVisible = true

                            }
                        }

                    }

                })
        }
    }


    @SuppressLint("PotentialBehaviorOverride")
    private fun focusOnPlace() {

        CoroutineScope(Main).launch {
            with(googleMap) {
                this?.mapType = GoogleMap.MAP_TYPE_TERRAIN
                this?.setInfoWindowAdapter(CustomInfoWindowAdapter())
                this?.setOnMarkerClickListener(this@MapsViewFragment)
                this?.setOnInfoWindowClickListener(this@MapsViewFragment)
                this?.setOnMarkerDragListener(this@MapsViewFragment)
                this?.setContentDescription("Map with marker.")
            }

            if (location != null) {
                addMarker(location!!)
            }
        }



    }

    private fun addMarker(location: PlaceEntity) {
        googleMap?.addMarker(
            MarkerOptions()
                .position(LatLng(location.lat, location.long))
                .title(location.cityName)
                .icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.pin)
                )
                .snippet(location.address)
                .rotation(-25f)
        )?.showInfoWindow()
        googleMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(location.lat, location.long),
                if (isRouteMode) 10f else 16f
            )
        )
    }

    private fun addMarkerAtMyLocation() {
        googleMap?.addMarker(
            MarkerOptions()
                .position(mapViewModel.getMyLatLng(activity as AppCompatActivity))
                .icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.pin)
                )
                .rotation(-25f)
        )
    }

    internal inner class CustomInfoWindowAdapter : GoogleMap.InfoWindowAdapter {

        private val window: View =
            layoutInflater.inflate(android.R.layout.simple_list_item_1, null)

        override fun getInfoWindow(marker: Marker): View {
            render(marker, window)
            return window
        }

        override fun getInfoContents(marker: Marker): View? {
            return null
        }

        private fun render(marker: Marker, view: View) {
            view.background = ContextCompat.getDrawable(mContext, R.drawable.rounded_bg_10)
            val title: String? = marker.title
            val titleUi = view.findViewById<TextView>(android.R.id.text1)

            titleUi.text = title ?: ""

        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        this.googleMap = googleMap
        val indiaBounds = LatLngBounds(
            LatLng(8.17, 68.75), LatLng(37.09, 97.40)
        )

        googleMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(indiaBounds, 0))

        if (isEdit) {
            focusOnPlace()
        }

        Log.d("onMapReady", "onMapReady: $isRouteMode")
        if (isRouteMode) {
            createMapRoutes()
        }

    }

    private fun createMapRoutes() {


        addMarkerAtMyLocation()

        mapViewModel.livePlaces.observe(this) { loc ->

            if (loc != null && loc.isNotEmpty()) {
                val locationsLatLng = ArrayList<LatLng>()
                loc.mapIndexed { index, locationsEntity ->
                    locationsLatLng.add(LatLng(locationsEntity.lat, locationsEntity.long))
                    addMarker(locationsEntity)
                    getDirections(
                        if (index == 0) mapViewModel.getMyLatLng(activity as AppCompatActivity) else LatLng(
                            loc[index - 1].lat,
                            loc[index - 1].long
                        ), LatLng(locationsEntity.lat, locationsEntity.long)
                    )
                }
            }
        }

    }

    private fun getDirections(origin: LatLng, destination: LatLng) {
        mapViewModel.getDirections(origin, destination) { result ->
            val list = PolyUtil.decode(result)
            googleMap?.addPolyline(PolylineOptions().addAll(list).color(Color.RED).width(10f))
        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        Log.d("onMarkerClick", "onMarkerClick: ")
        return false
    }

    override fun onMarkerDragStart(p0: Marker?) {
        Log.d("onMarkerDragStart", "onMarkerDragStart: ")
    }

    override fun onMarkerDrag(p0: Marker?) {
        Log.d("onMarkerDrag", "onMarkerDrag: ")
    }

    override fun onMarkerDragEnd(p0: Marker?) {
        Log.d("onMarkerDragEnd", "onMarkerDragEnd: ")
    }

    override fun onInfoWindowClick(p0: Marker?) {
        Log.d("onInfoWindowClick", "onInfoWindowClick: ")
    }


}