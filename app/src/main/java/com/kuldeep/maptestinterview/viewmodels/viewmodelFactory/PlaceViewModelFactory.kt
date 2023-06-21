package com.kuldeep.maptestinterview.viewmodels.viewmodelFactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kuldeep.maptestinterview.repository.PlaceRepository
import com.kuldeep.maptestinterview.viewmodels.PlaceViewmodel

class PlaceViewModelFactory(private val placeRepository: PlaceRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlaceViewmodel(placeRepository) as T
    }
}