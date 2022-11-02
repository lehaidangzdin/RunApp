package com.lhd.runapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

class HomeFragment(private val goToReceive: HomeInterface) : Fragment() {

    private lateinit var mBinding: FragmentHomeBinding
    private var myAdapter = ReceiveAdapter(arrayListOf(), 0)
    private var lsReceive: ArrayList<Receive> = ArrayList()
    private var lsIconReceive = ArrayList<ReceiveSeekbar>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //
        setupMySeekBar()
        setupViewPager()
        setupTabLayout()
        addLsReceive()
        setUpRcv()

        // listener
        mBinding.tvViewAll.setOnClickListener {
            goToReceive.replaceReceive(ReceiveFragment())
        }
        mBinding.mySeekBar.setOnClickListener(object : MySeekBar.OnClickBitmapReceive {
            override fun clickItem(positionReceive: Int) {
                if (!lsIconReceive[positionReceive].disable) {
                    lsIconReceive[positionReceive].disable = !lsIconReceive[positionReceive].disable
                }
                mBinding.mySeekBar.indicatorBitmapReceive = lsIconReceive
                mBinding.mySeekBar.invalidate()

                Toast.makeText(activity, "$positionReceive", Toast.LENGTH_SHORT).show()
            }
        })
    }

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
}