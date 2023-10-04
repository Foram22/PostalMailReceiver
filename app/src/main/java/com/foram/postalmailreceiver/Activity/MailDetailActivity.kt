package com.foram.postalmailreceiver.Activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.foram.postalmailreceiver.Model.PostalMailModel
import com.foram.postalmailreceiver.Model.UserModel
import com.foram.postalmailreceiver.R
import com.foram.postalmailreceiver.databinding.ActivityMailDetailBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class MailDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMailDetailBinding

//    var imageUrl: String? = null
//    var senderName: String? = null
//    var senderEmail: String? = null
//    var datetime: String? = null
//    var isFav: Boolean = false
//    var id: String? = null

    lateinit var postalMail: PostalMailModel

    lateinit var userModel: UserModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMailDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sp = getSharedPreferences("my_sp", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sp?.getString("user_model", null)
        val type: Type = object : TypeToken<UserModel>() {}.type

        userModel = gson.fromJson<Any>(json, type) as UserModel

//        if (intent.hasExtra("item_image")) {
//            imageUrl = intent.getStringExtra("item_image")
//        }
//        if (intent.hasExtra("item_sender_name")) {
//            senderName = intent.getStringExtra("item_sender_name")
//        }
//        if (intent.hasExtra("item_sender_email")) {
//            senderEmail = intent.getStringExtra("item_sender_email")
//        }
//        if (intent.hasExtra("item_datetime")) {
//            datetime = intent.getStringExtra("item_datetime")
//        }
//        if (intent.hasExtra("item_isFav")) {
//            isFav = intent.getBooleanExtra("item_isFav", false)
//        }
//
//        if (intent.hasExtra("item_id")){
//            id = intent.getStringExtra("item_id")
//        }
        postalMail = (intent.getSerializableExtra("postal_mail_model") as PostalMailModel?)!!

        binding.tvDateTime.text = postalMail.dateTime
        binding.tvSenderEmail.text = postalMail.senderEmail
        binding.tvSenderName.text = postalMail.senderName

        // Using Glide to load image from URL into ImageView
        Glide.with(this)
            .load(postalMail.imageUri)
            .into(binding.ivItemImage)

        if (postalMail.favourite){
            binding.btnFavUnfav.text = "Unfavourite"
        }else{
            binding.btnFavUnfav.text = "Favourite"
        }

        binding.ibBack.setOnClickListener {
            finish()
        }

        binding.btnFavUnfav.setOnClickListener {
            val userRef = Firebase.database.reference.child("postal_mail").child(postalMail.id)
            userRef.child("favourite").setValue(!postalMail.favourite).addOnSuccessListener {
                Toast.makeText(this, "Mail added as Favourite", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}