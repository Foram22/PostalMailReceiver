package com.foram.postalmailreceiver.Fragments

import android.Manifest.permission.CAMERA
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.foram.postalmailreceiver.Activity.ImageCaptureActivity
import com.foram.postalmailreceiver.Adapter.PostalMailAdapter
import com.foram.postalmailreceiver.Model.PostalMailModel
import com.foram.postalmailreceiver.Model.UserModel
import com.foram.postalmailreceiver.Model.UserType
import com.foram.postalmailreceiver.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

@Suppress("DEPRECATION")
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val CAMERA_PERMISSION_REQUEST_CODE = 1001

    lateinit var postalMailList: ArrayList<PostalMailModel>
    private lateinit var userModel: UserModel
    lateinit var adapter: PostalMailAdapter

    override fun onResume() {
        super.onResume()

        if (userModel.userType == UserType.MailCreator) {
            binding.llCreator.visibility = View.VISIBLE
            binding.llReceiver.visibility = View.VISIBLE
            binding.tvPostalMailsTitle.visibility = View.GONE
            binding.tvRecentMailsSendByYou.visibility = View.VISIBLE

            getPostalMail("senderEmail")
        } else {
            binding.llCreator.visibility = View.GONE
            binding.llReceiver.visibility = View.VISIBLE
            binding.tvPostalMailsTitle.visibility = View.VISIBLE
            binding.tvRecentMailsSendByYou.visibility = View.GONE

            getPostalMail("receiverEmail")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        linearLayoutManager.stackFromEnd = true
        binding.rvMails.layoutManager = linearLayoutManager
//        binding.rvMails.layoutManager = LinearLayoutManager(context)

        val sp = context?.getSharedPreferences("my_sp", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sp?.getString("user_model", null)
        val type: Type = object : TypeToken<UserModel>() {}.type

        userModel = gson.fromJson<Any>(json, type) as UserModel

        postalMailList = ArrayList()

        binding.tvNoPostalMails.visibility = View.GONE
        binding.rvMails.visibility = View.VISIBLE

        adapter = PostalMailAdapter(postalMailList, context, userModel)
        binding.rvMails.adapter = adapter

        binding.btnCaptureImage.setOnClickListener {
            requireActivity().run {

                checkForCameraAccess()
            }
        }

        return binding.root
    }

    private fun getPostalMail(s: String) {
        val postalMailRef = Firebase.database.reference.child("postal_mail")
        val query = postalMailRef.orderByChild(s).equalTo(userModel.email)

        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {

                postalMailList.clear()
                for (childSnapshot in snapshot.children) {
                    val postalMail = childSnapshot.getValue(PostalMailModel::class.java)
                    postalMail?.let { postalMailList.add(postalMail) }

                    adapter.notifyDataSetChanged()

                    if (postalMailList.size == 0) {
                        binding.tvNoPostalMails.visibility = View.VISIBLE
                        if (s == "senderEmail") {
                            binding.tvNoPostalMails.text =
                                "You haven't send any mails to any receiver."
                        } else {
                            binding.tvNoPostalMails.text = "There is no mails for you."
                        }
                    }
                }

                if (postalMailList.size == 0) {
                    binding.tvNoPostalMails.visibility = View.VISIBLE
                    if (s == "senderEmail") {
                        binding.tvNoPostalMails.text =
                            "You haven't send any mails to any receiver."
                    } else {
                        binding.tvNoPostalMails.text = "There is no mails for you."
                    }
                }
            }
        })
    }

    private fun checkForCameraAccess() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
            }
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 1)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openCamera()
                } else {
                    checkForCameraAccess()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            requireActivity().run {
                val intent = Intent(this, ImageCaptureActivity::class.java)
                intent.putExtra("capturedImage", imageBitmap)
                startActivity(intent)
            }
        }
    }
}