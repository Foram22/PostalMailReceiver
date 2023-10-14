package com.foram.postalmailreceiver.Fragments

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foram.postalmailreceiver.Activity.UpdatePasswordActivity
import com.foram.postalmailreceiver.Activity.MainActivity
import com.foram.postalmailreceiver.Model.UserModel
import com.foram.postalmailreceiver.databinding.FragmentProfileBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    lateinit var sp: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.btnLogout.setOnClickListener {
            sp.edit()?.remove("user_model")?.apply()
            sp.edit()?.clear()?.apply()
            requireActivity().run {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        binding.btnChange.setOnClickListener {
            requireActivity().run {
                startActivity(Intent(this, UpdatePasswordActivity::class.java))
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        sp = context?.getSharedPreferences("my_sp", MODE_PRIVATE)!!
        val gson = Gson()
        val json = sp.getString("user_model", null)
        val type: Type = object : TypeToken<UserModel>() {}.type

        val userModel = gson.fromJson<Any>(json, type) as UserModel

        binding.tvName.text = userModel.name
        binding.tvEmail.text = userModel.email
        binding.tvPhone.text = userModel.phone
        binding.tvPassword.text = userModel.password
        binding.tvUserType.text = userModel.userType.toString()
    }
}