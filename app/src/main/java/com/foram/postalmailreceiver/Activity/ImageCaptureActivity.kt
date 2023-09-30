package com.foram.postalmailreceiver.Activity

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class ImageCaptureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageCaptureBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImageCaptureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference.child("postal_mail")

        val sp = getSharedPreferences("my_sp", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sp?.getString("user_model", null)
        val type: Type = object : TypeToken<UserModel>() {}.type

        val userModel = gson.fromJson<Any>(json, type) as UserModel

        val imageBitmap = intent.getParcelableExtra<Bitmap>("capturedImage")
        binding.ivCaptureImage.setImageBitmap(imageBitmap)

        binding.btnSend.setOnClickListener {
            if (binding.etReceiverEmail.text.equals(null)) {
                binding.etReceiverEmail.error = "Please enter receiver's Email."
            } else {
                addPostalMailToFirebase(imageBitmap, userModel)
            }
        }
    }

    fun getCurrentTimeStamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun addPostalMailToFirebase(imageBitmap: Bitmap?, userModel: UserModel) {

        // Check if the receiver email exists
        val usersRef = Firebase.database.getReference("users")
        val query = usersRef.orderByChild("email").equalTo(binding.etReceiverEmail.text.toString())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    var receiverUserModel = UserModel()
                    for (childSnapshot in snapshot.children) {
                        receiverUserModel = childSnapshot.getValue(UserModel::class.java)!!
                        break
                    }

                    val postalMailRef = database.push()
                    postalMailRef.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                this@ImageCaptureActivity,
                                "Failed to add postal mail. Please try again later!.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val storageRef =
                                Firebase.storage.reference.child("images/${snapshot.key}.jpg")
                            val baos = ByteArrayOutputStream()
                            imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                            val data = baos.toByteArray()

                            val uploadTask = storageRef.putBytes(data)
                            var imageUrl = ""

                            uploadTask.continueWithTask { task ->
                                if (!task.isSuccessful) {
                                    task.exception?.let {
                                        throw it
                                    }
                                }
                                storageRef.downloadUrl
                            }.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    imageUrl = task.result.toString()
                                }
                            }

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
                            )
                            Toast.makeText(
                                this@ImageCaptureActivity,
                                "Mail sent successfully.",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    })
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
}