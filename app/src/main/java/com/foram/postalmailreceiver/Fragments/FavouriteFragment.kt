package com.foram.postalmailreceiver.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.foram.postalmailreceiver.Adapter.FavouriteMailAdapter
import com.foram.postalmailreceiver.Model.PostalMailModel
import com.foram.postalmailreceiver.Model.UserModel
import com.foram.postalmailreceiver.databinding.FragmentFavouriteBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class FavouriteFragment : Fragment() {

    lateinit var postalMailList: ArrayList<PostalMailModel>
    lateinit var adapter: FavouriteMailAdapter

    private lateinit var userModel: UserModel

    private lateinit var binding: FragmentFavouriteBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)

        binding.rvMails.layoutManager = LinearLayoutManager(context)
        postalMailList = ArrayList()

        val sp = context?.getSharedPreferences("my_sp", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sp?.getString("user_model", null)
        val type: Type = object : TypeToken<UserModel>() {}.type

        userModel = gson.fromJson<Any>(json, type) as UserModel

        adapter = FavouriteMailAdapter(postalMailList, context)
        binding.rvMails.adapter = adapter

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        getPostalMail()
    }

    private fun getPostalMail() {
        val postalMailRef = Firebase.database.reference.child("postal_mail")
        val query = postalMailRef.orderByChild("receiverEmail").equalTo(userModel.email)

        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {

                postalMailList.clear()
                for (childSnapshot in snapshot.children) {
                    val postalMail = childSnapshot.getValue(PostalMailModel::class.java)

                    if (postalMail != null) {
                        if (postalMail.favourite) {
                            postalMail.let { postalMailList.add(postalMail) }
                            adapter.notifyDataSetChanged()

                            if (postalMailList.size == 0) {
                                binding.tvNoFav.visibility = View.VISIBLE
                            }
                        }
                    }else{
                        binding.tvNoFav.visibility = View.VISIBLE
                    }
                }

                if (postalMailList.size == 0) {
                    binding.tvNoFav.visibility = View.VISIBLE
                }
            }
        })
    }
}