package com.lhd.runapp.fragment

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.material.tabs.TabLayout
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
import com.lhd.runapp.presenter.HomePresenter
import com.lhd.runapp.presenter.IHomePresenter
import com.lhd.runapp.utils.FitRequestCode
import com.lhd.runapp.utils.HomeFactory
import com.lhd.runapp.utils.Utils.fitnessOptions
import com.lhd.runapp.utils.Utils.getAccount
import kotlin.collections.ArrayList


const val TAG = "abc"

class HomeFragment(private val goToReceive: HomeInterface) : Fragment(), IHomePresenter {

    private lateinit var mBinding: FragmentHomeBinding
    private var myAdapter = ReceiveAdapter(arrayListOf(), 0)
    private var lsIconReceive = ArrayList<ReceiveSeekbar>()

    private lateinit var viewModel: HomePresenter

    //
    private var fragment: Fragment? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        // viewModel
        val factory = HomeFactory(requireActivity().application, this)
        viewModel = ViewModelProvider(this, factory)[HomePresenter::class.java]
        mBinding.homePresenter = viewModel
        viewModel.checkPermission(requireActivity())
        //
        observerComponent()
        setupMySeekBar()
        setUpRcv()
        Log.e(
            TAG,
            "onCreateView: ${GoogleSignIn.hasPermissions(getAccount(requireContext()), fitnessOptions)}",
        )
        return mBinding.root
    }

    private fun observerComponent() {
        viewModel.process.observe(viewLifecycleOwner) {
            mBinding.mySeekBar.progress = it / 5000f
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    override fun updateUi() {
        setupTabLayout()
        replaceFragment(DayFragment())
    }

}