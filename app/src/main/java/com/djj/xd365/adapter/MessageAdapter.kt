package com.djj.xd365.adapter

import android.content.Context
import android.support.v4.content.ContextCompat.getColor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.djj.xd365.R
import com.djj.xd365.db.ComMessage

/**
 * Created by liufeng on 2018-07-01.
 * 用于 xd365
 */
class MessageAdapter(val context: Context, data: MutableList<ComMessage>?, val listener: onComMessageItemClickListener) : RecyclerView.Adapter<ComMessageViewHolder>() {

    val mData: MutableList<ComMessage> = data ?: mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ComMessageViewHolder {
        return ComMessageViewHolder(LayoutInflater.from(context).inflate(R.layout.item_course, parent, false), listener)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ComMessageViewHolder?, position: Int) {
        holder?.initView(mData[position])
    }

    fun onRefresh(data: List<ComMessage>) {
        mData.clear()
        onLoadMore(data)
    }

    fun onLoadMore(data: List<ComMessage>) {
        mData.addAll(data)
        notifyDataSetChanged()
    }

}

class ComMessageViewHolder(val view: View, listener: onComMessageItemClickListener) : RecyclerView.ViewHolder(view) {
    var name: TextView
    var data: ComMessage? = null

    init {
        this.name = view.findViewById(R.id.title) as TextView
        view.setOnClickListener {
            listener.onClick(data)
        }
    }

    fun initView(comMessage: ComMessage) {
        data = comMessage
        name.text = comMessage.tittle
        if (comMessage.readtime<=0){
            name.setTextColor(getColor(view.context,R.color.text_info))
        }else{
            name.setTextColor(getColor(view.context,R.color.text_normal))
        }
    }
}

interface onComMessageItemClickListener {
    fun onClick(data: ComMessage?)
}
