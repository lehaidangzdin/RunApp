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
import dagger.hilt.android.AndroidEntryPoint


class ReceiveFragment : Fragment() {


    private val lsReceive = ArrayList<Receive>()
    private var mBinding: FragmentReceiveBinding? = null
    private val binding get() = mBinding!!
    private var myAdapter = ReceiveAdapter(arrayListOf(), 1)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addLsReceive()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_receive, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRcv()
        showAlertDialog()
    }

    private fun setUpRcv() {
        binding.rcv.apply {
            adapter = myAdapter
            setHasFixedSize(true)
        }
        binding.rcv2.apply {
            adapter = myAdapter
            setHasFixedSize(true)
        }
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
                "12",
                "120"
            )
        )
        lsReceive.add(Receive(R.drawable.huy_chuong3, "Step to Mars ", "04/10/2022", "120", "120"))
        lsReceive.add(
            Receive(
                R.drawable.huy_chuong4,
                "August Challenger",
                "14/08/2022",
                "12",
                "120"
            )
        )
        lsReceive.add(
            Receive(
                R.drawable.huy_chuong2,
                "Spectacular Breakout",
                "14/10/2022",
                "20",
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
                "12",
                "120"
            )
        )

        myAdapter.addData(lsReceive)
    }
}