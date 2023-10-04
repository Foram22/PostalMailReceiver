package com.foram.postalmailreceiver.Model

data class PostalMailModel(
    val id: String = "",
    val imageUri: String = "",
    val senderName: String = "",
    val senderEmail: String = "",
    val receiverName: String = "",
    val receiverEmail: String = "",
    val received: Boolean = false,
    val dateTime: String = "",
    val favourite: Boolean = false
) : java.io.Serializable