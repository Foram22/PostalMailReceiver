package com.foram.postalmailreceiver.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.foram.postalmailreceiver.Adapter.PostalMailAdapter
import com.foram.postalmailreceiver.Model.PostalMailModel
import com.foram.postalmailreceiver.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.rvMails.layoutManager = LinearLayoutManager(context)
        val data = ArrayList<PostalMailModel>()

        for (i in 1..10){
            data.add(PostalMailModel("","abc", "foram", "foram@gmail.com", "20 Sep",false))
        }

        val adapter = PostalMailAdapter(data, context)
        binding.rvMails.adapter = adapter

        return binding.root
    }
}