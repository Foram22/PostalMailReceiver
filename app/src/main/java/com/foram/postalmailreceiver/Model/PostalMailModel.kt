package com.foram.postalmailreceiver.Model

data class PostalMailModel(
    val id: String,
    val imageUri: String,
    val senderName: String,
    val senderEmail: String,
    val receiverName: String,
    val receiverEmail: String,
    val isReceived: Boolean,
    val dateTime: String,
    val isFavourite: Boolean
)