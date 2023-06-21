package com.kuldeep.maptestinterview.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlaceDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PlaceEntity)

    @Update
    suspend fun update(entity: PlaceEntity)

    @Query("SELECT * FROM place_table ORDER BY distance ASC")
    fun getLocationsAsc(): LiveData<List<PlaceEntity>>

    @Query("SELECT * FROM place_table ORDER BY distance DESC")
    fun getLocationsDesc(): LiveData<List<PlaceEntity>>


    @Query("DELETE FROM place_table WHERE id == :id")
    fun deleteById(id: Long)

    @Query("SELECT * FROM place_table WHERE id == :id LIMIT 1")
    fun getLocationById(id: Long): PlaceEntity?
}