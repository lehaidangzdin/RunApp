package com.lhd.runapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.lhd.runapp.R
import com.lhd.runapp.adapter.ReceiveAdapter
import com.lhd.runapp.adapter.TabLayoutAdapter
import com.lhd.runapp.customviews.SpacesItemDecoration
import com.lhd.runapp.databinding.FragmentHomeBinding
import com.lhd.runapp.models.Receive

class HomeFragment : Fragment() {


    private lateinit var mBinding: FragmentHomeBinding
    private var myAdapter = ReceiveAdapter(arrayListOf())
    private var lsReceive: ArrayList<Receive> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.mySeekBar.indicatorPositions = listOf(0F, 0.1F, 0.3F, 0.85F)
        mBinding.mySeekBar.indicatorText = listOf("0", "500", "1000", "4000")
        setupViewPager()
        setupTabLayout()
        setUpReceive()
        setUpRcv()
    }

    private fun setUpRcv() {
        mBinding.rcv.apply {
            adapter = myAdapter
            addItemDecoration(SpacesItemDecoration(10))
            setHasFixedSize(true)
        }
    }

    private fun setUpReceive() {

        lsReceive.add(Receive(R.drawable.huy_chuong2, "Spectacular Breakout", "14/10/2022"))
        lsReceive.add(Receive(R.drawable.huy_huong1, "October Challenger", "09/10/2022"))
        lsReceive.add(Receive(R.drawable.huy_chuong3, "Step to Mars ", "04/10/2022"))
        lsReceive.add(Receive(R.drawable.huy_chuong4, "August Challenger", "14/08/2022"))
        lsReceive.add(Receive(R.drawable.huy_chuong2, "Spectacular Breakout", "14/10/2022"))
        lsReceive.add(Receive(R.drawable.huy_huong1, "October Challenger", "09/10/2022"))
        lsReceive.add(Receive(R.drawable.huy_chuong3, "Step to Mars ", "04/10/2022"))
        lsReceive.add(Receive(R.drawable.huy_chuong4, "August Challenger", "14/08/2022"))

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
        mBinding.viewPager.adapter = adapter
    }


}