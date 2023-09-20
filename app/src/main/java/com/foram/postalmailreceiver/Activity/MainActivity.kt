package com.foram.postalmailreceiver.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.foram.postalmailreceiver.Model.UserModel
import com.foram.postalmailreceiver.Model.UserType
import com.foram.postalmailreceiver.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userModel: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userModel = UserModel("Foram","foram@gmail.com", "Foram@123", UserType.MailReceiver)

        binding.tvRegisterHere.setOnClickListener {
            startActivity(
                Intent(this, RegistrationActivity::class.java)
            )
            finish()
        }

        binding.btnLogin.setOnClickListener {
            if (userModel.userType.equals(UserType.MailCreator))
            {
                startActivity(Intent(this, CreatorHomeActivity::class.java))
            }
            else
            {
                startActivity(Intent(this, ReceiverHomeActivity::class.java))
            }
            finish()
        }
    }
}
