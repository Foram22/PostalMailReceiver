package com.foram.postalmailreceiver.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.foram.postalmailreceiver.R
import com.foram.postalmailreceiver.databinding.ActivityMailDetailBinding

class MailDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMailDetailBinding

    var imageUrl: String? = null
    var senderName: String? = null
    var senderEmail: String? = null
    var datetime: String? = null
    var isFav: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMailDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("item_image")) {
            imageUrl = intent.getStringExtra("item_image")
        }
        if (intent.hasExtra("item_sender_name")) {
            senderName = intent.getStringExtra("item_sender_name")
        }
        if (intent.hasExtra("item_sender_email")) {
            senderEmail = intent.getStringExtra("item_sender_email")
        }
        if (intent.hasExtra("item_datetime")) {
            datetime = intent.getStringExtra("item_datetime")
        }
        if (intent.hasExtra("item_isFav")) {
            isFav = intent.getBooleanExtra("item_isFav", false)
        }

        binding.tvDateTime.text = datetime
        binding.tvSenderEmail.text = senderEmail
        binding.tvSenderName.text = senderName

        // Using Glide to load image from URL into ImageView
        Glide.with(this)
            .load(imageUrl)
            .into(binding.ivItemImage)

        if (isFav){
            binding.btnFavUnfav.text = this.getString(R.string.unfavourite)
        }else{
            binding.btnFavUnfav.text = this.getString(R.string.favourite)
        }

        binding.ibBack.setOnClickListener {
            finish()
        }
    }
}