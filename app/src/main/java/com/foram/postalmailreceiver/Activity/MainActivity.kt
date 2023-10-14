package com.foram.postalmailreceiver.Activity

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.foram.postalmailreceiver.Model.UserModel
import com.foram.postalmailreceiver.Model.UserType
import com.foram.postalmailreceiver.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    lateinit var sp: SharedPreferences
    lateinit var editor: Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference.child("users")

        binding.tvRegisterHere.setOnClickListener {
            startActivity(
                Intent(this, RegistrationActivity::class.java)
            )
            finish()
        }

        sp = getSharedPreferences("my_sp", MODE_PRIVATE)
        editor = sp.edit()

        binding.btnLogin.setOnClickListener {
            if (isValidateUserFields()) {
                LoginUsingFirebase()
            }
        }
    }

    private fun LoginUsingFirebase() {
        auth.signInWithEmailAndPassword(
            binding.etEmail.text.toString(),
            binding.etPassword.text.toString()
        ).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                val id = auth.currentUser?.uid
                if (id != null) {
                    database.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val value = snapshot.child(id).getValue(UserModel::class.java)
                            if (value != null) {

                                var token: String
                                FirebaseMessaging.getInstance().token.addOnCompleteListener {
                                    if (!it.isSuccessful) {
                                        Log.w(
                                            "TAG",
                                            "Fetching FCM registration token failed",
                                            it.exception
                                        )
                                    }

                                    token = it.result

                                    // Adding generated firebase cloud messaging token to firebase database
                                    val userRef =
                                        Firebase.database.reference.child("users").child(id)
                                    userRef.child("FCM").setValue(token).addOnSuccessListener {
                                        value.FCM = token
                                    }

                                    val gson = Gson()
                                    val json = gson.toJson(value)
                                    editor.putString("user_model", json)
                                    editor.apply()

                                    if (value.userType.toString() == UserType.MailReceiver.toString()) {
                                        startActivity(
                                            Intent(
                                                this@MainActivity,
                                                ReceiverHomeActivity::class.java
                                            )
                                        )
                                    } else {
                                        startActivity(
                                            Intent(
                                                this@MainActivity,
                                                CreatorHomeActivity::class.java
                                            )
                                        )
                                    }
                                    finish()
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                this@MainActivity,
                                "Failed to login",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                }
                Toast.makeText(this, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidateUserFields(): Boolean {
        val isValid: Boolean
        if (TextUtils.isEmpty(binding.etEmail.text.toString().trim())) {
            binding.etEmail.error = "Please enter your Email."
            isValid = false
        } else if (TextUtils.isEmpty(binding.etPassword.text.toString().trim())) {
            binding.etPassword.error = "Please enter your Password."
            isValid = false
        } else {
            isValid = true
        }
        return isValid
    }
}
