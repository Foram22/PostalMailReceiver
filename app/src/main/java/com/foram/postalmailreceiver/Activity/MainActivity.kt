package com.foram.postalmailreceiver.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.foram.postalmailreceiver.Model.UserModel
import com.foram.postalmailreceiver.Model.UserType
import com.foram.postalmailreceiver.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userModel: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userModel = UserModel("Foram","foram@gmail.com", "Foram@123", UserType.MailCreator, "1234567890")

        binding.tvRegisterHere.setOnClickListener {
            startActivity(
                Intent(this, RegistrationActivity::class.java)
            )
        }

        val sp = getSharedPreferences("my_sp", MODE_PRIVATE)
        val editor = sp.edit()

        binding.btnLogin.setOnClickListener {
            if (userModel.userType.equals(UserType.MailCreator))
            {
                startActivity(Intent(this, CreatorHomeActivity::class.java))
            }
            else
            {
                startActivity(Intent(this, ReceiverHomeActivity::class.java))
            }

            // Save User Data to Shared Preference
            val gson = Gson()
            val json: String = gson.toJson(userModel)
            editor.putString("user_model", json)
            editor.apply()

            finish()
        }
    }
}
