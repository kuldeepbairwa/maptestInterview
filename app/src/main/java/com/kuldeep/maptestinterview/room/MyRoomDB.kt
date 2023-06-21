package com.kuldeep.maptestinterview.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kuldeep.maptestinterview.utils.AppConstants

@Database(entities = [PlaceEntity::class], version = 2)
abstract class MyRoomDB : RoomDatabase() {

    abstract fun placeDao(): PlaceDAO

    companion object {
        private var INSTANCE: MyRoomDB? = null
        fun getDatabase(context: Context): MyRoomDB {
            if (INSTANCE == null) {


                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(context, MyRoomDB::class.java, AppConstants.DB_NAME)
                            .allowMainThreadQueries()
                            .build()
                }

            }
            return INSTANCE!!
        }
    }

}