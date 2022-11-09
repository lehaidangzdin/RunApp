package com.lhd.runapp.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.material.tabs.TabLayout
import com.lhd.runapp.FitRequestCode
import com.lhd.runapp.R
import com.lhd.runapp.adapter.ReceiveAdapter
import com.lhd.runapp.customviews.MySeekBar
import com.lhd.runapp.customviews.modelCustomView.ReceiveSeekbar
import com.lhd.runapp.databinding.FragmentHomeBinding
import com.lhd.runapp.fragment.fragChart.DayFragment
import com.lhd.runapp.fragment.fragChart.MonthFragment
import com.lhd.runapp.fragment.fragChart.WeekFragment
import com.lhd.runapp.interfacePresenter.HomeInterface
import com.lhd.runapp.models.Receive
import kotlin.collections.ArrayList


const val TAG = "abc"

class HomeFragment(private val goToReceive: HomeInterface) : Fragment() {

    private lateinit var mBinding: FragmentHomeBinding
    private var myAdapter = ReceiveAdapter(arrayListOf(), 0)

    //    private var lsReceive: ArrayList<Receive> = ArrayList()
    private var lsIconReceive = ArrayList<ReceiveSeekbar>()

    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
        .build()

    //
    private var fragment: Fragment? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        replaceFragment(DayFragment())
        setupMySeekBar()
        checkPermission()
        setupTabLayout()
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


    private fun getGoogleAccount() =
        GoogleSignIn.getAccountForExtension(requireContext(), fitnessOptions)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkPermission() {

        if (!GoogleSignIn.hasPermissions(getGoogleAccount(), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this,
                FitRequestCode.GG_FIT_REQUEST_CODE.ordinal,
                getGoogleAccount(),
                fitnessOptions
            )
        } else {
            getStepsByCurrentDay()
        }
    }

    private fun getStepsByCurrentDay() {
        Fitness.getHistoryClient(requireActivity(), getGoogleAccount())
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener { dataSet ->
                val total = when {
                    dataSet.isEmpty -> 0
                    else -> dataSet.dataPoints.first().getValue(Field.FIELD_STEPS).asInt()
                }
                Log.i(TAG, "Total steps: $total")
                displayTotalSteps(total)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "There was a problem getting the step count.", e)
            }
    }

    private fun displayTotalSteps(total: Int) {
        mBinding.numSteps.text = "$total"
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
        mBinding.mySeekBar.indicatorPositions = listOf(0F, 0.1F, 0.3F, 0.8F)
        mBinding.mySeekBar.indicatorText = listOf("0", "500", "1000", "4000")
        mBinding.mySeekBar.indicatorBitmapReceive = lsIconReceive
    }

    private fun setUpRcv() {
        myAdapter.addData(addLsReceive())
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

    private fun addLsReceive(): ArrayList<Receive> {

        val lsAchieved = ArrayList<Receive>()

        lsAchieved.add(
            Receive(
                R.drawable.huy_chuong2,
                "Spectacular Breakout",
                "14/10/2022",
                "120",
                "120"
            )
        )
        lsAchieved.add(
            Receive(
                R.drawable.huy_huong1,
                "October Challenger",
                "09/10/2022",
                "120",
                "120"
            )
        )
        lsAchieved.add(Receive(R.drawable.huy_chuong3, "Step to Mars ", "04/10/2022", "120", "120"))
        lsAchieved.add(
            Receive(
                R.drawable.huy_chuong4,
                "August Challenger",
                "14/08/2022",
                "120",
                "120"
            )
        )
        lsAchieved.add(
            Receive(
                R.drawable.huy_chuong2,
                "Spectacular Breakout",
                "14/10/2022",
                "120",
                "120"
            )
        )
        lsAchieved.add(
            Receive(
                R.drawable.huy_huong1,
                "October Challenger",
                "09/10/2022",
                "120",
                "120"
            )
        )
        lsAchieved.add(Receive(R.drawable.huy_chuong3, "Step to Mars ", "04/10/2022", "120", "120"))
        lsAchieved.add(
            Receive(
                R.drawable.huy_chuong4,
                "August Challenger",
                "14/08/2022",
                "120",
                "120"
            )
        )
        return lsAchieved
    }

    private fun setupTabLayout() {
        with(mBinding) {
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> fragment = DayFragment()
                        1 -> fragment = WeekFragment()
                        2 -> fragment = MonthFragment()
                    }
                    replaceFragment(fragment)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

            })
        }
    }

    private fun replaceFragment(fragment: Fragment?) {
        val fm = requireActivity().supportFragmentManager
        val ft = fm.beginTransaction()
        if (fragment != null) {
            ft.replace(R.id.frameFrg, fragment)
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()
    }

}