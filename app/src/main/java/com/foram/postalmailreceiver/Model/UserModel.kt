package com.foram.postalmailreceiver.Model

data class UserModel (
    val name: String, val email: String, val password: String, val userType: UserType
)

enum class UserType{
    MailReceiver, MailCreator
}