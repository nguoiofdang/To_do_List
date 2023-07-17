package com.example.to_dolist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_dolist.models.Task
import com.example.to_dolist.repository.Repository
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repo: Repository
) : ViewModel() {

    fun insertTask(task: Task) = viewModelScope.launch {
        repo.insertTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repo.deleteTask(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repo.updateTask(task)
    }

    fun getUnFinishTask(isCheck: Boolean) = repo.getUnFinishTask(isCheck)

    fun getTaskMountStar(isStar: Boolean) = repo.getTaskMountStar(isStar)

    fun getTaskOfTheDay(isCheck: Boolean, daySelected: Int, monthSelected: Int, yearSelected: Int) =
        repo.getTaskOfTheDay(isCheck, daySelected, monthSelected, yearSelected)
}