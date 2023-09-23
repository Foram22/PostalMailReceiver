package com.foram.postalmailreceiver.Model

data class UserModel (
    val name: String, val email: String, val password: String, val userType: UserType, val phone: String
)

enum class UserType{
    MailReceiver, MailCreator
}