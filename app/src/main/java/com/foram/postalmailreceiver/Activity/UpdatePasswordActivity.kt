package com.foram.postalmailreceiver.Activity

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.foram.postalmailreceiver.Model.UserModel
import com.foram.postalmailreceiver.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class UpdatePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    lateinit var sp: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    lateinit var userModel:UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference.child("users")

        sp = getSharedPreferences("my_sp", MODE_PRIVATE)
        editor = sp.edit()

        val gson = Gson()
        val json = sp.getString("user_model", null)
        val type: Type = object : TypeToken<UserModel>() {}.type

        userModel = gson.fromJson<Any>(json, type) as UserModel

        binding.btnResetPassword.setOnClickListener {
            if (isValidateUserFields()) {
                resetPassword()
            }
        }
    }

    private fun resetPassword() {
        val id = auth.currentUser?.uid.toString()
        database.child(id).child("password").setValue(binding.etPassword.text.toString()).addOnCompleteListener {
            auth.currentUser?.updatePassword(binding.etPassword.text.toString())
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {

                        userModel.password = binding.etPassword.text.toString()

                        val gson = Gson()
                        val json = gson.toJson(userModel)
                        editor.putString("user_model", json)
                        editor.apply()

                        Toast.makeText(
                            this@UpdatePasswordActivity,
                            "Password changed successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@UpdatePasswordActivity,
                            "Failed to change password. Please try again later.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun isValidateUserFields(): Boolean {
        val isValid: Boolean
        if (TextUtils.isEmpty(binding.etPassword.text.toString().trim())) {
            binding.etPassword.error = "Please enter your Password."
            isValid = false
        } else if (TextUtils.isEmpty(binding.etConfirmPassword.text.toString().trim())) {
            binding.etConfirmPassword.error = "Please enter confirm Password."
            isValid = false
        } else if (binding.etPassword.text.toString() != binding.etConfirmPassword.text.toString()) {
            Toast.makeText(
                this,
                "Password does not match. Please check your password.",
                Toast.LENGTH_SHORT
            ).show()
            isValid = false
        } else {
            isValid = true
        }
        return isValid
    }
}