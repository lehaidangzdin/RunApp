package com.lhd.runapp.fragment

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.lhd.runapp.R
import com.lhd.runapp.adapter.ReceiveAdapter
import com.lhd.runapp.adapter.TabLayoutAdapter
import com.lhd.runapp.customviews.MySeekBar
import com.lhd.runapp.customviews.modelCustomView.ReceiveSeekbar
import com.lhd.runapp.databinding.FragmentHomeBinding
import com.lhd.runapp.interfacePresenter.HomeInterface
import com.lhd.runapp.models.Receive

class HomeFragment(private val goToReceive: HomeInterface) : Fragment(), SensorEventListener {

    private lateinit var mBinding: FragmentHomeBinding
    private var myAdapter = ReceiveAdapter(arrayListOf(), 0)
    private var lsReceive: ArrayList<Receive> = ArrayList()
    private var lsIconReceive = ArrayList<ReceiveSeekbar>()

    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps: Int = 0
    private var previousTotalSteps: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        loadData()
        resetSteps()

        setupMySeekBar()
        setupViewPager()
        setupTabLayout()
        addLsReceive()
        setUpRcv()

        // listener
        with(mBinding) {
            tvViewAll.setOnClickListener {
                goToReceive.replaceReceive(ReceiveFragment())
            }

            mySeekBar.setOnClickListener(object : MySeekBar.OnClickBitmapReceive {
                override fun clickItem(index: Int) {
                    /**
                     * 1, neu isDisable = false => CLICK
                     * 2, update trạng thái của lsIconReceive[index] isisDisable = true
                     * 3, truyền ls mơi update vào MySeekBar => update lại view
                     */
                    if (!lsIconReceive[index].isDisable) {
                        lsIconReceive[index].isDisable = !lsIconReceive[index].isDisable
                        Log.e("CLICKED  ", "clickItem: $index")

                        // Xử lí logic click button ....

                        updateSeekBar(lsIconReceive)
                    }
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        running = true
        val steps: Sensor? = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (steps == null) {
            Toast.makeText(requireContext(), "No sensor detected on this device", Toast.LENGTH_LONG)
                .show()
        } else {
            sensorManager?.registerListener(this, steps, SensorManager.SENSOR_DELAY_UI)
        }
    }

    private fun resetSteps() {
        with(mBinding) {
            numSteps.setOnClickListener {
                Toast.makeText(requireContext(), "Long tap to reset steps", Toast.LENGTH_LONG)
                    .show()
            }
            numSteps.setOnLongClickListener {
                previousTotalSteps = totalSteps
                numSteps.text = "0 / "
                saveData()
                true
            }
        }
    }

    private fun saveData() {
        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences("MySteps", Context.MODE_PRIVATE)
        val editTor: SharedPreferences.Editor = sharedPreferences.edit()
        editTor.putInt("key1", previousTotalSteps)
        editTor.apply()
    }

    private fun loadData() {
        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences("MySteps", Context.MODE_PRIVATE)
        val savedNumber: Int = sharedPreferences.getInt("key1", 0)
        Log.e("steps saved", "$savedNumber")
        previousTotalSteps = savedNumber
    }

    /**
     * Update lại seekbar
     */
    private fun updateSeekBar(lsIconReceive: ArrayList<ReceiveSeekbar>) {
        mBinding.mySeekBar.indicatorBitmapReceive = lsIconReceive
        mBinding.mySeekBar.invalidate()
    }

    /**
     * Add vị trí của indicator
     * Add text
     */

    private fun setupMySeekBar() {
        addIconReceive()
        mBinding.mySeekBar.indicatorPositions = listOf(0F, 0.1F, 0.3F, 0.85F)
        mBinding.mySeekBar.indicatorText = listOf("0", "500", "1000", "4000")
        mBinding.mySeekBar.indicatorBitmapReceive = lsIconReceive
    }


    private fun setUpRcv() {
        mBinding.rcv.apply {
            adapter = myAdapter
            setHasFixedSize(true)
        }
    }

    private fun addIconReceive() {
        lsIconReceive.add(ReceiveSeekbar(0, true))
        lsIconReceive.add(ReceiveSeekbar(R.drawable.reiceve1, false))
        lsIconReceive.add(ReceiveSeekbar(R.drawable.receive2, false))
        lsIconReceive.add(ReceiveSeekbar(R.drawable.reiceve3, false))
    }

    private fun addLsReceive() {
        lsReceive.add(
            Receive(
                R.drawable.huy_chuong2,
                "Spectacular Breakout",
                "14/10/2022",
                "120",
                "120"
            )
        )
        lsReceive.add(
            Receive(
                R.drawable.huy_huong1,
                "October Challenger",
                "09/10/2022",
                "120",
                "120"
            )
        )
        lsReceive.add(Receive(R.drawable.huy_chuong3, "Step to Mars ", "04/10/2022", "120", "120"))
        lsReceive.add(
            Receive(
                R.drawable.huy_chuong4,
                "August Challenger",
                "14/08/2022",
                "120",
                "120"
            )
        )
        lsReceive.add(
            Receive(
                R.drawable.huy_chuong2,
                "Spectacular Breakout",
                "14/10/2022",
                "120",
                "120"
            )
        )
        lsReceive.add(
            Receive(
                R.drawable.huy_huong1,
                "October Challenger",
                "09/10/2022",
                "120",
                "120"
            )
        )
        lsReceive.add(Receive(R.drawable.huy_chuong3, "Step to Mars ", "04/10/2022", "120", "120"))
        lsReceive.add(
            Receive(
                R.drawable.huy_chuong4,
                "August Challenger",
                "14/08/2022",
                "120",
                "120"
            )
        )

        myAdapter.addData(lsReceive)
    }

    private fun setupTabLayout() {
        TabLayoutMediator(
            mBinding.tabLayout, mBinding.viewPager
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = resources.getString(R.string.day)
                }
                1 -> {
                    tab.text = resources.getString(R.string.week)
                }
                2 -> {
                    tab.text = resources.getString(R.string.month)
                }
            }
        }.attach()
    }

    private fun setupViewPager() {
        val adapter = activity?.let { TabLayoutAdapter(it) }
        mBinding.viewPager.apply {
            setAdapter(adapter)
            isUserInputEnabled = false
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        totalSteps = event!!.values[0].toInt()
        val currentSteps: Int = totalSteps - previousTotalSteps
        mBinding.numSteps.text = "$currentSteps / "
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}