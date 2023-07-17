package com.example.to_dolist.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.to_dolist.db.dao.TaskDao
import com.example.to_dolist.models.Task

@androidx.room.Database(
    entities = [Task::class],
    version = 1
)
abstract class Database : RoomDatabase() {

    abstract val taskDao: TaskDao

    companion object{
        private var instance: Database? = null
        private val LOOK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOOK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                Database::class.java,
                "task_db.db"
            ).build()
    }
}