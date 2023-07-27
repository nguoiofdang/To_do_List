package com.example.to_dolist.repository

import androidx.lifecycle.LiveData
import com.example.to_dolist.db.Database
import com.example.to_dolist.models.Task

class Repository(
    private val db: Database
) {

    suspend fun insertTask(task: Task) = db.taskDao.insertTask(task)

    suspend fun deleteTask(task: Task) = db.taskDao.deleteTask(task)

    suspend fun updateTask(task: Task) = db.taskDao.updateTask(task)

    fun getUnFinishTask(isCheck: Boolean): LiveData<List<Task>> =
        db.taskDao.getUnFinishTask(isCheck)

    fun getTaskMountStar(isStar: Boolean): LiveData<List<Task>> =
        db.taskDao.getTaskMountStar(isStar)

    fun getTaskOfTheDay(
        isFinish: Boolean,
        daySelected: Int,
        monthSelected: Int,
        yearSelected: Int
    ): LiveData<List<Task>> =
        db.taskDao.getTaskOfTheDay(isFinish, daySelected, monthSelected, yearSelected)
}