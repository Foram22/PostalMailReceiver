package com.foram.postalmailreceiver.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.foram.postalmailreceiver.Adapter.FavouriteMailAdapter
import com.foram.postalmailreceiver.Model.PostalMailModel
import com.foram.postalmailreceiver.databinding.FragmentFavouriteBinding

class FavouriteFragment : Fragment() {

    private lateinit var binding: FragmentFavouriteBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)

        binding.rvMails.layoutManager = LinearLayoutManager(context)
        val data = ArrayList<PostalMailModel>()

        val adapter = FavouriteMailAdapter(data, context)
        binding.rvMails.adapter = adapter

        return binding.root
    }
}