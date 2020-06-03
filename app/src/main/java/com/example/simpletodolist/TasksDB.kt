package com.wiseassblog.jetpacknotesmvvmkotlin.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.simpletodolist.Task
import com.example.simpletodolist.writeTasks

private const val DATABASE = "tasks"

@Database(
    entities = [Task::class],
    version = 1
)

abstract class RoomNoteDatabase : RoomDatabase() {

    abstract fun roomNoteDao(): writeTasks

    //code below courtesy of https://github.com/googlesamples/android-sunflower; it     is open
    //source just like this application.
    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: RoomNoteDatabase? = null

        fun getInstance(context: Context): RoomNoteDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): RoomNoteDatabase {
            return Room.databaseBuilder(context, RoomNoteDatabase::class.java, DATABASE)
                .build()
        }
    }
}