package com.mtg.tool.findmyphone.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import com.mtg.tool.findmyphone.data.dao.SoundDao
import com.mtg.tool.findmyphone.data.model.SoundItem

@Database(entities = [SoundItem::class], version = 2, exportSchema = false)
abstract class RoomDatabase : androidx.room.RoomDatabase() {
    abstract fun soundDao(): SoundDao?

    companion object {
        private var instance: RoomDatabase? = null
        fun getDatabase(): RoomDatabase? {
            return instance
        }

        fun initDatabase(context: Context){
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context!!,
                    RoomDatabase::class.java, "sql_db"
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration().build()
            }
        }
    }
}