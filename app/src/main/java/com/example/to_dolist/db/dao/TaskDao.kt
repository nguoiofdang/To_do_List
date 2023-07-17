package com.example.to_dolist.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.to_dolist.models.Task

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Query("SELECT * FROM task WHERE finish=:isCheck")
    fun getUnFinishTask(isCheck: Boolean): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE star=:isStar")
    fun getTaskMountStar(isStar: Boolean): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE finish=:isCheck AND day=:daySelected AND month=:monthSelected AND year=:yearSelected")
    fun getTaskOfTheDay(
        isCheck: Boolean,
        daySelected: Int,
        monthSelected: Int,
        yearSelected: Int
    ): LiveData<List<Task>>

}