package com.example.to_dolist.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity("tasK")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var titleTask: String? = null,
    var hour: Int? = null,
    var minute: Int? = null,
    var day: Int? = null,
    var month: Int? = null,
    var year: Int? = null,
    var finish: Boolean = false,
    var star: Boolean = false
) : java.io.Serializable
