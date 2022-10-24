package com.lhd.runapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lhd.runapp.fragment.fragChart.DayFragment
import com.lhd.runapp.fragment.fragChart.MonthFragment
import com.lhd.runapp.fragment.fragChart.WeekFragment

class TabLayoutAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DayFragment()
            1 -> WeekFragment()
            2 -> MonthFragment()
            else -> DayFragment()
        }
    }
}