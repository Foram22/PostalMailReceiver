package com.foram.postalmailreceiver.Model

data class UserModel(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val userType: UserType = UserType.MailCreator,
    val phone: String = "",
    var FCM: String = ""
)

enum class UserType {
    MailReceiver, MailCreator
}