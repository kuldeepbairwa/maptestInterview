package com.kuldeep.maptestinterview.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kuldeep.maptestinterview.utils.AppConstants

@Entity(tableName = AppConstants.TABLE_PLACE)
data class PlaceEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var cityName: String? = null,
    var address: String? = null,
    var placeId: String? = null,
    var lat: Double = .0,
    var long: Double = .0,
    var distance: Double = .0
)