package com.foram.postalmailreceiver.Model

data class PostalMailModel(
    val id: String,
    val imageUri: String,
    val senderName: String,
    val senderEmail: String,
    val dateTime: String,
    val isFavourite: Boolean
)