package com.example.to_dolist.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.to_dolist.models.Date
import com.example.to_dolist.models.Task
import com.example.to_dolist.repository.Repository
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repo: Repository
) : ViewModel() {

    private val dateSelected: MutableLiveData<Date> = MutableLiveData()
    var onClickListenerCalendar: MediatorLiveData<List<Task>> = MediatorLiveData()
    val list: LiveData<List<Task>> = dateSelected.switchMap { date ->
        repo.getTaskOfTheDay(false, date.day, date.month, date.year)
    }

    /*init {
        onClickListenerCalendar.addSource(dateSelected) { date ->
            Log.e("date", date.toString() + "onClickListenerCalendar")
            onClickListenerCalendar.postValue(getTaskOfTheDay(false, date.day, date.month, date.year).value ?: listOf())
            onClickListenerCalendar.value = getTask(false).value ?: listOf()
            Log.e("date", getTaskOfTheDay(false, date.day, date.month, date.year).value.toString() + "getTaskOfTheDay")
            Log.e("date", onClickListenerCalendar.value.toString() + "onClickListenerCalendar")
        }
    }*/

    fun setDateSelected(date: Date) {
        dateSelected.value = date
        Log.e("date", dateSelected.value.toString() + "setDateSelected")
    }

    fun insertTask(task: Task) = viewModelScope.launch {
        repo.insertTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repo.deleteTask(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repo.updateTask(task)
    }

    /*fun getListTaskOfDay: MutableLiveData<List<Task>> = onClickListenerCalendar*/

    fun getTask(isFinish: Boolean) = repo.getUnFinishTask(isFinish)

    fun getTaskMountStar(isStar: Boolean) = repo.getTaskMountStar(isStar)

    private fun getTaskOfTheDay(
        isFinish: Boolean,
        daySelected: Int,
        monthSelected: Int,
        yearSelected: Int
    )= repo.getTaskOfTheDay(isFinish, daySelected, monthSelected, yearSelected)
}