package com.foram.postalmailreceiver.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import com.foram.postalmailreceiver.Model.UserModel
import com.foram.postalmailreceiver.Model.UserType
import com.foram.postalmailreceiver.R
import com.foram.postalmailreceiver.databinding.ActivityRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var binding: ActivityRegistrationBinding

    private val spinnerItems = listOf("Select User Type", "Mail Receiver", "Mail Creator")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference.child("users")

        binding.tvSignInHere.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val adapter = ArrayAdapter(this, R.layout.layout_dropdown_menu, spinnerItems)
        binding.spinner.adapter = adapter

        binding.btnRegister.setOnClickListener {
            if (isValidateUserFields()) {
                CreateUserIntoFirebase()
            }
        }
    }

    private fun CreateUserIntoFirebase() {
        auth.createUserWithEmailAndPassword(
            binding.etEmail.text.toString(),
            binding.etPassword.text.toString()
        ).addOnCompleteListener(this@RegistrationActivity) {
            if (it.isSuccessful) {
                val id = auth.currentUser?.uid
                database.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (id != null) {
                            var userType: UserType = UserType.MailReceiver
                            if (binding.spinner.selectedItemPosition == 1) {
                                userType = UserType.MailReceiver
                            } else if (binding.spinner.selectedItemPosition == 2) {
                                userType = UserType.MailCreator
                            }
                            val userModel = UserModel(
                                binding.etName.text.toString(),
                                binding.etEmail.text.toString(),
                                binding.etPassword.text.toString(),
                                userType,
                                binding.etPhone.text.toString()
                            )
                            database.child(id).setValue(userModel)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@RegistrationActivity,
                            "Failed to register",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
                Toast.makeText(
                    this@RegistrationActivity,
                    "Successfully Singed Up",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                Toast.makeText(this@RegistrationActivity, "Singed Up Failed!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun isValidateUserFields(): Boolean {
        val isValid: Boolean
        if (TextUtils.isEmpty(binding.etName.text.toString().trim())) {
            binding.etName.error = "Please enter your name."
            isValid = false
        } else if (TextUtils.isEmpty(binding.etEmail.text.toString().trim())) {
            binding.etEmail.error = "Please enter your email."
            isValid = false
        } else if (TextUtils.isEmpty(binding.etPhone.text.toString().trim())) {
            binding.etPhone.error = "Please enter your phone number."
            isValid = false
        } else if (binding.etPhone.length() != 10) {
            Toast.makeText(this, "Please enter valid phone number.", Toast.LENGTH_SHORT).show()
            isValid = false
        } else if (binding.spinner.selectedItemPosition == 0) {
            Toast.makeText(this, "Please select user type.", Toast.LENGTH_SHORT).show()
            isValid = false
        } else if (TextUtils.isEmpty(binding.etPassword.text.toString().trim())) {
            binding.etPassword.error = "Please enter password."
            isValid = false
        } else if (TextUtils.isEmpty(binding.etConfirmPassword.text.toString().trim())) {
            binding.etConfirmPassword.error = "Please enter confirm password."
            isValid = false
        } else if (binding.etPassword.text.toString() != binding.etConfirmPassword.text.toString()) {
            Toast.makeText(this, "Password does not match.", Toast.LENGTH_SHORT).show()
            isValid = false
        } else {
            isValid = true
        }

        return isValid
    }
}