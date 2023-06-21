package com.kuldeep.maptestinterview.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.room.Index
import com.kuldeep.maptestinterview.R
import com.kuldeep.maptestinterview.databinding.FragmentPlacesBinding
import com.kuldeep.maptestinterview.repository.PlaceRepository
import com.kuldeep.maptestinterview.room.MyRoomDB
import com.kuldeep.maptestinterview.utils.AppConstants
import com.kuldeep.maptestinterview.utils.AppConstants.IS_ROUTE_MODE
import com.kuldeep.maptestinterview.utils.OnItemClick
import com.kuldeep.maptestinterview.view.adapters.AdapterPlaces
import com.kuldeep.maptestinterview.viewmodels.PlaceViewmodel
import com.kuldeep.maptestinterview.viewmodels.viewmodelFactory.PlaceViewModelFactory


class PlacesFragment : BaseFragment<FragmentPlacesBinding>() {

    private lateinit var placeViewmodel: PlaceViewmodel
    override fun getViewBinding() = FragmentPlacesBinding.inflate(layoutInflater)


    override fun listeners() {

        binding.addPOI.setOnClickListener {
            findNavController().navigate(R.id.action_places_to_map)
        }

        binding.addPOIMore.setOnClickListener {
            findNavController().navigate(R.id.action_places_to_map)
        }

        binding.getDirections.setOnClickListener {
            findNavController().navigate(R.id.action_places_to_map, Bundle().apply { putBoolean(IS_ROUTE_MODE, true) })
        }

        binding.tb.menu[0].setOnMenuItemClickListener {
            AlertDialog.Builder(mContext)
                .setTitle("Sort Places")
                .setMessage("Choose a Sorting Order")
                .setPositiveButton("Ascending Order") { _, _ ->
                    placeViewmodel.getPlaceList(Index.Order.ASC)
                }.setNegativeButton("Descending Order") { _, _ ->
                    placeViewmodel.getPlaceList(Index.Order.DESC)
                }.create().show()
            false
        }
    }

    override fun bindViewModels() {
        placeViewmodel =
            ViewModelProvider(
                this,
                PlaceViewModelFactory(PlaceRepository(room!!))
            )[PlaceViewmodel::class.java]
    }

    override fun attachObservers() {
        placeViewmodel.livePlaces.observe(this) {
            if (it.isNotEmpty()) {
                binding.addPOIMore.visibility = View.VISIBLE
                binding.rv.visibility = View.VISIBLE
                binding.noResultsContainer.visibility = View.GONE
                binding.rv.adapter = AdapterPlaces(
                    ArrayList(it), object : OnItemClick<Long> {
                        override fun onClick(item: Long) {
                            findNavController().navigate(
                                R.id.action_places_to_map, Bundle().apply {
                                    putLong(AppConstants.LOCATION_ID, item)
                                }
                            )
                        }
                    }, object : OnItemClick<Long> {
                        override fun onClick(item: Long) {

                            AlertDialog.Builder(mContext)
                                .setTitle("Delete")
                                .setMessage("Are you sure you want to delete this location?")
                                .setPositiveButton("Yes") { _, _ ->
                                    placeViewmodel.deleteById(item)
                                }.setNegativeButton("No", null).create().show()
                        }
                    }
                )

            } else {
                binding.addPOIMore.visibility = View.GONE
                binding.rv.visibility = View.GONE
                binding.noResultsContainer.visibility = View.VISIBLE
            }
        }

        placeViewmodel.order.observe(this){order->
            placeViewmodel.getPlaceList(order)
        }

    }

    override fun updateUi() {
        attachObservers()

    }


}