package com.kuldeep.maptestinterview.viewmodels.viewmodelFactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kuldeep.maptestinterview.repository.MapRepository
import com.kuldeep.maptestinterview.viewmodels.MapViewModel

class MapViewModelFactory(private val context: Context, private val mapRepository: MapRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapViewModel(mapRepository) as T
    }
}