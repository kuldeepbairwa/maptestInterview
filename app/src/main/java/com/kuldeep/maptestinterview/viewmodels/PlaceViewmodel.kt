package com.kuldeep.maptestinterview.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Index
import com.kuldeep.maptestinterview.repository.PlaceRepository
import com.kuldeep.maptestinterview.room.PlaceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaceViewmodel(private val placeRepository: PlaceRepository):ViewModel() {
    private var _order = MutableLiveData<Index.Order>()
    val order get() = _order
    lateinit var livePlaces : LiveData<List<PlaceEntity>>
    fun getPlaceList(order : Index.Order){
        viewModelScope.launch {
            livePlaces =  placeRepository.getPlaceList(order)
        }
    }


    fun deleteById(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            placeRepository.deleteById(id)
        }
    }


    init {
        viewModelScope.launch {
            livePlaces = placeRepository.getPlaceList()
        }
    }

    fun changeOrder(order: Index.Order){
        _order.postValue(order)
    }






}