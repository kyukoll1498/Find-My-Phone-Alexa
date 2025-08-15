package com.mtg.tool.findmyphone.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mtg.tool.findmyphone.data.dao.SoundDao
import com.mtg.tool.findmyphone.data.model.SoundItem

@Database(entities = [SoundItem::class], version = 2, exportSchema = false)
abstract class RoomSoundDB : RoomDatabase() {
    abstract fun soundDao(): SoundDao?

    companion object {
        private var instance: RoomSoundDB? = null
        fun getDatabase(): RoomSoundDB? {
            return instance
        }

        fun initDatabase(context: Context) {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context!!,
                    RoomSoundDB::class.java, "sql_db"
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration().build()
            }
        }
    }
}