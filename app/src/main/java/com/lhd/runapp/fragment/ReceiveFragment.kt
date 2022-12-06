package com.lhd.runapp.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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
import com.lhd.runapp.viewmodel.HomeViewModel


class ReceiveFragment : Fragment() {
    private var mBinding: FragmentReceiveBinding? = null
    private val binding get() = mBinding!!
    private var myAdapterMonthChallenger = ReceiveAdapter(arrayListOf(), 1)
    private var myAdapterAccumulatingSteps = ReceiveAdapter(arrayListOf(), 1)
    private val lsMonthChallenger = ArrayList<Challenger>()
    private val lsAccumulateCha = ArrayList<Challenger>()

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentReceiveBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        mBinding!!.model = viewModel
        setUpRcv()
        observableComponent()
        showAlertDialog()
        return binding.root
    }

    private fun observableComponent() {
        viewModel.lsMonthChallenger.observe(viewLifecycleOwner) {
            lsMonthChallenger.addAll(it)
            myAdapterMonthChallenger.addData(it)
        }

        viewModel.accumulateChallenger.observe(viewLifecycleOwner) {
            lsAccumulateCha.addAll(it)
            myAdapterAccumulatingSteps.addData(it)
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
//        myAdapterAccumulatingSteps.addData(addLsAccumulatingSteps())
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
        binding.btnShare.setOnClickListener {
            dialog?.dismiss()
        }
        binding.btnClose.setOnClickListener {
            dialog?.dismiss()
        }
    }
}