package com.foram.postalmailreceiver.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import com.foram.postalmailreceiver.Model.UserModel
import com.foram.postalmailreceiver.Model.UserType
import com.foram.postalmailreceiver.R
import com.foram.postalmailreceiver.databinding.ActivitySplashBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    var isUserVerified = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val alphaAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_animation)
        binding.llSplash.startAnimation(alphaAnim)

        val sp = getSharedPreferences("my_sp", MODE_PRIVATE)
        val gson = Gson()

        isUserVerified = sp.getBoolean("is_user_verified", false)

        Handler().postDelayed({
//            if (sp.contains("is_user_verified") && isUserVerified) {
                if (sp.contains("user_model")) {
                    val json = sp?.getString("user_model", null)
                    val type: Type = object : TypeToken<UserModel>() {}.type
                    val userModel = gson.fromJson<Any>(json, type) as UserModel

                    if (userModel.userType == UserType.MailReceiver) {
                        startActivity(Intent(this, ReceiverHomeActivity::class.java))
                    } else {
                        startActivity(Intent(this, CreatorHomeActivity::class.java))
                    }
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                }
//            } else if (sp.contains("is_user_verified") && !isUserVerified) {
//                startActivity(Intent(this, EmailVerificationActivity::class.java))
//            }
//            else {
//                startActivity(Intent(this, MainActivity::class.java))
//            }

            finish()
        }, 3000)
    }
}