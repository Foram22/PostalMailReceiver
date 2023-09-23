package com.foram.postalmailreceiver.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.foram.postalmailreceiver.Fragments.HomeFragment
import com.foram.postalmailreceiver.Fragments.ProfileFragment
import com.foram.postalmailreceiver.R
import com.foram.postalmailreceiver.databinding.ActivityCreatorHomeBinding

class CreatorHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatorHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatorHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeFragment = HomeFragment()
        val profileFragment = ProfileFragment()

        setCurrentFragment(homeFragment)

        binding.bnv.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.receiver_home -> setCurrentFragment(homeFragment)
                R.id.receiver_profile -> setCurrentFragment(profileFragment)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(binding.frameLayout.id,fragment)
            commit()
        }
}