package com.lhd.runapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.lhd.runapp.databinding.ActivityMainBinding
import com.lhd.runapp.fragment.EventFragment
import com.lhd.runapp.fragment.HomeFragment
import com.lhd.runapp.fragment.ReceiveFragment
import com.lhd.runapp.interfacePresenter.HomeInterface

class MainActivity : AppCompatActivity(), HomeInterface {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isPermissionRecognitionGranted = false

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                isPermissionRecognitionGranted =
                    it[Manifest.permission.ACTIVITY_RECOGNITION] ?: isPermissionRecognitionGranted
            }
        requestPermission()

        reFragment(HomeFragment(this))

        mBinding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> reFragment(HomeFragment(this))
                R.id.event -> reFragment(EventFragment())
                else -> {
                }
            }
            true
        }
        //
        mBinding.header.btnBackPress.setOnClickListener {
            // if current fragment == ReceiveFragment => remove
            val f = supportFragmentManager.findFragmentById(R.id.frame)
            if (f is ReceiveFragment) {
                reFragment(HomeFragment(this))
            }
        }
    }

    override fun replaceReceive(fragment: Fragment) {
        super.replaceReceive(fragment)
        reFragment(ReceiveFragment())
    }

    private fun reFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransient = fragmentManager.beginTransaction()

        fragmentTransient.replace(R.id.frame, fragment).commit()
//        fragmentTransient.commit()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestPermission() {
        isPermissionRecognitionGranted = ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
        val permissionRequest: MutableList<String> = ArrayList()
        if (!isPermissionRecognitionGranted) {
            permissionRequest.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }
        if (permissionRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }

}