package com.kuldeep.maptestinterview.repository


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Index
import com.kuldeep.maptestinterview.room.MyRoomDB
import com.kuldeep.maptestinterview.room.PlaceEntity

class PlaceRepository(private val room: MyRoomDB) {


    fun getPlaceList(order: Index.Order = Index.Order.ASC): LiveData<List<PlaceEntity>> {

        return if (order == Index.Order.ASC){
            room.placeDao().getLocationsAsc()
        } else{
            room.placeDao().getLocationsDesc()
        }
    }

    fun deleteById(id: Long){
        room.placeDao().deleteById(id)
    }

}