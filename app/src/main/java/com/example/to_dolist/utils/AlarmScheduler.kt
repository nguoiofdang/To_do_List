package com.example.to_dolist.utils

import com.example.to_dolist.models.Task

interface AlarmScheduler {
    fun scheduler(item: Task)
    fun cancel(item: Task)
}