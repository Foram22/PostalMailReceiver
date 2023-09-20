package com.foram.postalmailreceiver.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.foram.postalmailreceiver.R
import com.foram.postalmailreceiver.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding

    private val spinnerItems = listOf("Select User Type", "Mail Receiver", "Mail Creator")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSignInHere.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val adapter = ArrayAdapter(this, R.layout.layout_dropdown_menu, spinnerItems)
        binding.spinner.adapter = adapter
    }
}