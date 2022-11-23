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
import androidx.lifecycle.ViewModelProvider
import com.lhd.runapp.R
import com.lhd.runapp.adapter.ReceiveAdapter
import com.lhd.runapp.databinding.DialogBinding
import com.lhd.runapp.databinding.FragmentReceiveBinding
import com.lhd.runapp.models.Challenger
import com.lhd.runapp.utils.Utils
import com.lhd.runapp.viewmodel.HomeViewModel


class ReceiveFragment : Fragment() {
    private var mBinding: FragmentReceiveBinding? = null
    private val binding get() = mBinding!!
    private var myAdapterMonthChallenger = ReceiveAdapter(arrayListOf(), 1)
    private var myAdapterAccumulatingSteps = ReceiveAdapter(arrayListOf(), 1)
    private val lsChallenger = ArrayList<Challenger>()

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentReceiveBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        mBinding!!.model = viewModel

        viewModel.getStepsByMonth()
        setUpRcv()
        observableComponent()
        showAlertDialog()
        return binding.root
    }

    private fun observableComponent() {
        viewModel.lsMonthChallenger.observe(viewLifecycleOwner) {
            lsChallenger.addAll(it)
            myAdapterMonthChallenger.addData(it)
        }
    }

    private fun setUpRcv() {
        binding.rcv.apply {
            adapter = myAdapterMonthChallenger
            setHasFixedSize(true)
        }
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

    private fun addLsAccumulatingSteps(): ArrayList<Challenger> {
        val lsAccumulatingSteps = ArrayList<Challenger>()
        lsAccumulatingSteps.add(
            Challenger(
                R.drawable.huy_huong1,
                Utils.lsAccumulateChallenger[0],
                0,
                "20",
                "120"
            )
        )
        lsAccumulatingSteps.add(
            Challenger(
                R.drawable.huy_huong1,
                Utils.lsAccumulateChallenger[1],
                0,
                "10",
                "120"
            )
        )

        lsAccumulatingSteps.add(
            Challenger(
                R.drawable.huy_huong1,
                Utils.lsAccumulateChallenger[2],
                1669827600776,
                "120",
                "120"
            )
        )
        lsAccumulatingSteps.add(
            Challenger(
                R.drawable.huy_huong1,
                Utils.lsAccumulateChallenger[3],
                0,
                "20",
                "120"
            )
        )

        lsAccumulatingSteps.add(
            Challenger(
                R.drawable.huy_huong1,
                Utils.lsAccumulateChallenger[4],
                1669827600776,
                "120",
                "120"
            )
        )
        lsAccumulatingSteps.add(
            Challenger(
                R.drawable.huy_huong1,
                Utils.lsAccumulateChallenger[5],
                1669827600776,
                "120",
                "120"
            )
        )

        lsAccumulatingSteps.add(
            Challenger(
                R.drawable.huy_huong1,
                Utils.lsAccumulateChallenger[6],
                0,
                "20",
                "120"
            )
        )
        lsAccumulatingSteps.add(
            Challenger(
                R.drawable.huy_huong1,
                Utils.lsAccumulateChallenger[7],
                1669827600776,
                "120",
                "120"
            )
        )

        lsAccumulatingSteps.add(
            Challenger(
                R.drawable.huy_huong1,
                Utils.lsAccumulateChallenger[8],
                0,
                "10",
                "120"
            )
        )
        return lsAccumulatingSteps
    }
}