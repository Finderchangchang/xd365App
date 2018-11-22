package com.djj.xd365.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.djj.xd365.R
import com.djj.xd365.db.Stucourse

/**
 * Created by liufeng on 2018-07-01.
 * 用于 xd365
 */
class BqStudyAdapter(val context: Context, data: MutableList<Stucourse>?,val listener:onItemClickListener) : RecyclerView.Adapter<StucourseViewHolder>() {

    val mData: MutableList<Stucourse> = data ?: mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): StucourseViewHolder {
        return StucourseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_course, parent, false),listener)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: StucourseViewHolder?, position: Int) {
        holder?.initView(mData[position])
    }

    fun onRefresh(data: List<Stucourse>) {
        mData.clear()
        onLoadMore(data)
    }

    fun onLoadMore(data: List<Stucourse>) {
        mData.addAll(data)
        notifyDataSetChanged()
    }

}

class StucourseViewHolder(view: View,listener:onItemClickListener) : RecyclerView.ViewHolder(view) {
    var name: TextView
    var data: Stucourse? = null
    init {
        this.name = view.findViewById(R.id.title) as TextView
        view.setOnClickListener {
            listener.onClick(data)
        }
    }

    fun initView(stucourselog: Stucourse) {
        data = stucourselog
        name.text = stucourselog.name
    }
}

interface onItemClickListener{
    fun onClick(data:Stucourse?)
}
