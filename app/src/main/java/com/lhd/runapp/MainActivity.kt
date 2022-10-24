package com.lhd.runapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.lhd.runapp.databinding.ActivityMainBinding
import com.lhd.runapp.fragment.EventFragment
import com.lhd.runapp.fragment.HomeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        reFragment(HomeFragment())

        mBinding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> reFragment(HomeFragment())
                R.id.event -> reFragment(EventFragment())
                else -> {
                }
            }
            true
        }
    }

    private fun reFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransient = fragmentManager.beginTransaction()

        fragmentTransient.replace(R.id.frame, fragment)
        fragmentTransient.commit()
    }

}