package com.djj.xd365.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.djj.xd365.R
import com.djj.xd365.db.CarCheck
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by liufeng on 2018-07-01.
 * 用于 xd365
 */
class CarCheckAdapter(val context: Context, data: MutableList<CarCheck>?, val listener: onCarCheckItemClickListener) : RecyclerView.Adapter<CarCheckViewHolder>() {

    val mData: MutableList<CarCheck> = data ?: mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CarCheckViewHolder {
        return CarCheckViewHolder(LayoutInflater.from(context).inflate(R.layout.item_carcheck, parent, false), listener)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: CarCheckViewHolder?, position: Int) {
        holder?.initView(mData[position])
    }

    fun onRefresh(data: List<CarCheck>) {
        mData.clear()
        onLoadMore(data)
    }

    fun onLoadMore(data: List<CarCheck>) {
        mData.addAll(data)
        notifyDataSetChanged()
    }

}

class CarCheckViewHolder(view: View, listener: onCarCheckItemClickListener) : RecyclerView.ViewHolder(view) {
    var name: TextView
    var xcq:Button
    var xcz:Button
    var xch:Button
    var data: CarCheck? = null
    var sdf=SimpleDateFormat("yyyy年MM月dd日")

    init {
        this.name = view.findViewById(R.id.title) as TextView
        this.xcq = view.findViewById(R.id.xcq) as Button
        this.xcz = view.findViewById(R.id.xcz) as Button
        this.xch = view.findViewById(R.id.xch) as Button
        xcq.setOnClickListener {
            listener.onXcqClick(data)
        }
        xcz.setOnClickListener {
            listener.onXczClick(data)
        }
        xch.setOnClickListener {
            listener.onXchClick(data)
        }
    }

    fun initView(stucourselog: CarCheck) {
        data = stucourselog
        val date=Date()
        date.time=stucourselog.creatime
        name.text = sdf.format(date)
        if (stucourselog.xcq=="0"){
            xcq.visibility=View.VISIBLE
        }else{
            xcq.visibility=View.GONE
        }
        if (stucourselog.xcz=="0"){
            xcz.visibility=View.VISIBLE
        }else{
            xcz.visibility=View.GONE
        }
        if (stucourselog.xch=="0"){
            xch.visibility=View.VISIBLE
        }else{
            xch.visibility=View.GONE
        }

    }
}

interface onCarCheckItemClickListener {
    fun onXcqClick(data: CarCheck?)
    fun onXczClick(data: CarCheck?)
    fun onXchClick(data: CarCheck?)
}
