package com.foram.postalmailreceiver.Activity

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.foram.postalmailreceiver.Model.PostalMailModel
import com.foram.postalmailreceiver.Model.UserModel
import com.foram.postalmailreceiver.databinding.ActivityImageCaptureBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@Suppress("DEPRECATION")
class ImageCaptureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageCaptureBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    lateinit var userModel: UserModel
    lateinit var receiverUserModel: UserModel

    var imageBitmap: Bitmap? = null

    lateinit var postalMailRef: DatabaseReference

    lateinit var imageUrl: String

    lateinit var storageRef: StorageReference

    var shouldStart = true

    private val uploadTaskOnSuccessListener = object : CustomOnSuccessListener {
        override fun onActionCompleted(result: Any) {
            storageRef.downloadUrl.addOnSuccessListener {
                imageUrl = result.toString()

                postalMailRef.setValue(
                    PostalMailModel(
                        postalMailRef.key.toString(),
                        imageUrl,
                        userModel.name,
                        userModel.email,
                        receiverUserModel.name,
                        binding.etReceiverEmail.text.toString(),
                        false,
                        getCurrentTimeStamp(),
                        false
                    )
                ).addOnSuccessListener {
                    if (shouldStart) {
                        Toast.makeText(
                            this@ImageCaptureActivity,
                            "Mail sent successfully.",
                            Toast.LENGTH_SHORT
                        ).show()

                        sendNotificationToReceiver()
                        finish()

                        shouldStart = false
                    }
                }
            }
        }
    }

    private var postMailValueEventListener = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(
                this@ImageCaptureActivity,
                "Failed to add postal mail. Please try again later!.",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onDataChange(snapshot: DataSnapshot) {
            storageRef =
                Firebase.storage.reference.child("images/${snapshot.key}.jpg")
            val baos = ByteArrayOutputStream()
            imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val uploadTask = storageRef.putBytes(data)

            uploadTask.addOnSuccessListener {
                uploadTaskOnSuccessListener.onActionCompleted(it)
            }
        }
    }

    private fun sendNotificationToReceiver() {
        val json = JSONObject()
        try {

            json.put("to", receiverUserModel.FCM)

            val notificationObj = JSONObject()
            notificationObj.put("title", "New Postal Mail")
            notificationObj.put("body", "From: ${userModel.name}")
            notificationObj.put("icon", imageUrl)
            json.put("notification", notificationObj)

            val url = "https://fcm.googleapis.com/fcm/send"
            val request: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST,
                url,
                json,
                Response.Listener { },
                Response.ErrorListener { }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val header: MutableMap<String, String> = HashMap()
                    header["content-type"] = "application/json"
                    header["authorization"] =
                        "key=AAAA0ZW7sKU:APA91bFvmU672U0s6AC1SAtiP7LoVPqm0elsXtK0Xm0WOHiMza4yx4wxokFguvU3PWBQ61gIv9gmw-HQo6J8D9HsXyzn2K7zbb35Yn0AlFKy-AfawymTFyvP-T6ezFhqfZRh5aXYmr6h"
                    return header
                }
            }

            val queue = Volley.newRequestQueue(this)
            queue.add(request).setShouldCache(false)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImageCaptureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference.child("postal_mail")
        postalMailRef = database

        val sp = getSharedPreferences("my_sp", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sp?.getString("user_model", null)
        val type: Type = object : TypeToken<UserModel>() {}.type

        userModel = gson.fromJson<Any>(json, type) as UserModel

        imageBitmap = intent.getParcelableExtra("capturedImage")
        binding.ivCaptureImage.setImageBitmap(imageBitmap)

        binding.btnSend.setOnClickListener {
            if (binding.etReceiverEmail.text.equals(null)) {
                binding.etReceiverEmail.error = "Please enter receiver's Email."
            } else {
                addPostalMailToFirebase()
            }
        }

        binding.ibBack.setOnClickListener {
            finish()
        }
    }

    fun getCurrentTimeStamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun addPostalMailToFirebase() {

        // Check if the receiver email exists
        val usersRef = Firebase.database.getReference("users")
        val query = usersRef.orderByChild("email").equalTo(binding.etReceiverEmail.text.toString())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    receiverUserModel = UserModel()
                    for (childSnapshot in snapshot.children) {
                        receiverUserModel = childSnapshot.getValue(UserModel::class.java)!!
                        break
                    }

                    postalMailRef = database.push()
                    postalMailRef.addValueEventListener(postMailValueEventListener)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ImageCaptureActivity,
                    "Receiver's Email doesn't exists. Please check the receiver's Emails address.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()

        if (postalMailRef != null){
            postalMailRef.removeEventListener(postMailValueEventListener)
        }
    }

    override fun onStop() {
        super.onStop()

//        postalMailRef.removeEventListener(postMailValueEventListener)
    }
}

interface CustomOnSuccessListener {
    fun onActionCompleted(result: Any)
}
