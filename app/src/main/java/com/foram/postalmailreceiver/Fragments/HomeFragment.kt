package com.foram.postalmailreceiver.Fragments

import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.opengl.Visibility
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val CAMERA_PERMISSION_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.rvMails.layoutManager = LinearLayoutManager(context)

        val sp = context?.getSharedPreferences("my_sp", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sp?.getString("user_model", null)
        val type: Type = object : TypeToken<UserModel>() {}.type

        val userModel = gson.fromJson<Any>(json, type) as UserModel

        if (userModel.userType == UserType.MailCreator) {
            binding.llCreator.visibility = View.VISIBLE
            binding.llReceiver.visibility = View.VISIBLE
            binding.tvPostalMailsTitle.visibility = View.GONE
            binding.tvRecentMailsSendByYou.visibility = View.VISIBLE
        } else {
            binding.llCreator.visibility = View.GONE
            binding.llReceiver.visibility = View.VISIBLE
            binding.tvPostalMailsTitle.visibility = View.VISIBLE
            binding.tvRecentMailsSendByYou.visibility = View.GONE
        }
        val data = ArrayList<PostalMailModel>()

        for (i in 1..10) {
            data.add(
                PostalMailModel(
                    "",
                    "abc",
                    "foram",
                    "foram@gmail.com",
                    "20 Sep",
                    "test@gmail.com",
                    false,
                    "2020",
                    false
                )
            )
        }

        val adapter = PostalMailAdapter(data, context)
        binding.rvMails.adapter = adapter

        binding.btnCaptureImage.setOnClickListener {
            requireActivity().run {

                checkForCameraAccess()
            }
        }

        return binding.root
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