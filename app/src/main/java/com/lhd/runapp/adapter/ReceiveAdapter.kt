package com.lhd.runapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexboxLayoutManager
import com.lhd.runapp.databinding.ItemReceiveBinding
import com.lhd.runapp.models.Challenger

class ReceiveAdapter(
    private val lsChallenger: ArrayList<Challenger>,
    private val type: Int
) : RecyclerView.Adapter<ReceiveAdapter.MyViewHolder>() {

    private lateinit var listener: OnClickItem
    private val limit = 4

    @SuppressLint("NotifyDataSetChanged")
    fun addData(list: List<Challenger>) {
        lsChallenger.clear()
        lsChallenger.addAll(list)
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: OnClickItem) {
        this.listener = listener
    }

    class MyViewHolder(val mBinding: ItemReceiveBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemReceiveBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentMovie = lsChallenger[position]
        holder.mBinding.item = currentMovie
        val lp = holder.mBinding.itemReceive.layoutParams
        if (lp is FlexboxLayoutManager.LayoutParams) {
            lp.flexGrow = 1.0f
            lp.alignSelf = AlignItems.FLEX_START
        }
    }

    override fun getItemCount(): Int {

        if (type == 0 && lsChallenger.size > limit) return limit

        return lsChallenger.size
    }

    interface OnClickItem {
        fun clickItem(results: Challenger);
    }
}