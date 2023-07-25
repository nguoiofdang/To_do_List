package com.example.to_dolist.models

import java.io.Serializable

data class Notification(
    val titleTask: String,
    val descriptionTask: String
): Serializable
