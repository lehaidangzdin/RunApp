package com.lhd.runapp.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.lhd.runapp.R
import com.lhd.runapp.adapter.ReceiveAdapter
import com.lhd.runapp.databinding.DialogBinding
import com.lhd.runapp.databinding.FragmentReceiveBinding
import com.lhd.runapp.models.Receive


class ReceiveFragment : Fragment() {


    private var mBinding: FragmentReceiveBinding? = null
    private val binding get() = mBinding!!
    private var myAdapterMonthChallenger = ReceiveAdapter(arrayListOf(), 1)
    private var myAdapterAccumulatingSteps = ReceiveAdapter(arrayListOf(), 1)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addLsMonthChallenger()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_receive, container, false
        )
        setUpRcv()
        showAlertDialog()
        return binding.root
    }

    private fun setUpRcv() {
        binding.rcv.apply {
            adapter = myAdapterMonthChallenger
            setHasFixedSize(true)
        }
        myAdapterMonthChallenger.addData(addLsMonthChallenger())
        //
        binding.rcv2.apply {
            adapter = myAdapterAccumulatingSteps
            setHasFixedSize(true)
        }
        myAdapterAccumulatingSteps.addData(addLsAccumulatingSteps())
    }

    private fun showAlertDialog() {
        val dialog = activity?.let { Dialog(it) }
        dialog?.window?.apply {
            setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        val binding =
            DataBindingUtil.inflate<DialogBinding>(
                LayoutInflater.from(activity?.applicationContext),
                R.layout.dialog,
                null,
                false
            )
        dialog?.setContentView(binding.root)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.show()
        binding.btnShare.setOnClickListener { _ ->
            dialog?.dismiss()
        }
        binding.btnClose.setOnClickListener { _ ->
            dialog?.dismiss()
        }


    }

    private fun addLsMonthChallenger(): ArrayList<Receive> {
        val lsReceive = ArrayList<Receive>()
        lsReceive.add(
            Receive(
                R.drawable.huy_huong1,
                "Thu thach thang 1",
                "14/1/2022",
                "120",
                "120"
            )
        )
        lsReceive.add(
            Receive(
                R.drawable.huy_huong1,
                "Thu thach thang 2",
                "14/1/2022",
                "10",
                "120"
            )
        )

        lsReceive.add(
            Receive(
                R.drawable.huy_huong1,
                "Thu thach thang 3",
                "14/1/2022",
                "120",
                "120"
            )
        )
        lsReceive.add(
            Receive(
                R.drawable.huy_huong1,
                "Thu thach thang 4",
                "14/1/2022",
                "120",
                "120"
            )
        )

        lsReceive.add(
            Receive(
                R.drawable.huy_huong1,
                "Thu thach thang 5",
                "14/1/2022",
                "120",
                "120"
            )
        )
        lsReceive.add(
            Receive(
                R.drawable.huy_huong1,
                "Thu thach thang 6",
                "14/1/2022",
                "120",
                "120"
            )
        )

        lsReceive.add(
            Receive(
                R.drawable.huy_huong1,
                "Thu thach thang 7",
                "14/1/2022",
                "120",
                "120"
            )
        )
        lsReceive.add(
            Receive(
                R.drawable.huy_huong1,
                "Thu thach thang 8",
                "14/1/2022",
                "120",
                "120"
            )
        )

        lsReceive.add(
            Receive(
                R.drawable.huy_huong1,
                "Thu thach thang 9",
                "14/1/2022",
                "120",
                "120"
            )
        )
        lsReceive.add(
            Receive(
                R.drawable.huy_huong1,
                "Thu thach thang 10",
                "14/1/2022",
                "120",
                "120"
            )
        )

        lsReceive.add(
            Receive(
                R.drawable.huy_huong1,
                "Thu thach thang 11",
                "14/1/2022",
                "120",
                "120"
            )
        )
        lsReceive.add(
            Receive(
                R.drawable.huy_huong1,
                "Thu thach thang 12",
                "14/1/2022",
                "120",
                "120"
            )
        )

        return lsReceive
    }

    private fun addLsAccumulatingSteps(): ArrayList<Receive> {
        val lsAccumulatingSteps = ArrayList<Receive>()
        lsAccumulatingSteps.add(
            Receive(
                R.drawable.huy_huong1,
                "Khoi dau suon se",
                "14/1/2022",
                "20",
                "120"
            )
        )
        lsAccumulatingSteps.add(
            Receive(
                R.drawable.huy_huong1,
                "Kien tri luyen tap",
                "14/1/2022",
                "10",
                "120"
            )
        )

        lsAccumulatingSteps.add(
            Receive(
                R.drawable.huy_huong1,
                "Tich luy ben bi",
                "14/1/2022",
                "120",
                "120"
            )
        )
        lsAccumulatingSteps.add(
            Receive(
                R.drawable.huy_huong1,
                "But pha ngoan muc",
                "14/1/2022",
                "20",
                "120"
            )
        )

        lsAccumulatingSteps.add(
            Receive(
                R.drawable.huy_huong1,
                "Khong he lui buoc",
                "14/1/2022",
                "120",
                "120"
            )
        )
        lsAccumulatingSteps.add(
            Receive(
                R.drawable.huy_huong1,
                "Dua con than gio",
                "14/1/2022",
                "120",
                "120"
            )
        )

        lsAccumulatingSteps.add(
            Receive(
                R.drawable.huy_huong1,
                "Doi chan khong moi",
                "14/1/2022",
                "20",
                "120"
            )
        )
        lsAccumulatingSteps.add(
            Receive(
                R.drawable.huy_huong1,
                "Buoc toi mat trang",
                "14/1/2022",
                "120",
                "120"
            )
        )

        lsAccumulatingSteps.add(
            Receive(
                R.drawable.huy_huong1,
                "Buoc toi sao hoa",
                "14/1/2022",
                "10",
                "120"
            )
        )
        return lsAccumulatingSteps
    }
}