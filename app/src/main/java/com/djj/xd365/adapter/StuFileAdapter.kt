package com.djj.xd365.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.djj.xd365.R
import com.djj.xd365.db.StuFile
import com.djj.xd365.db.Stucourselog

/**
 * Created by liufeng on 2018-07-01.
 * 用于 xd365
 */
class StuFileAdapter(val context: Context, data: MutableList<StuFile>?,val listener:onStuFileItemClickListener) : RecyclerView.Adapter<StufileViewHolder>() {

    val mData: MutableList<StuFile> = data ?: mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): StufileViewHolder {
        return StufileViewHolder(LayoutInflater.from(context).inflate(R.layout.item_stufile, parent, false),listener)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: StufileViewHolder?, position: Int) {
        holder?.initView(mData[position])
    }

    fun onRefresh(data: List<StuFile>) {
        mData.clear()
        onLoadMore(data)
    }

    fun onLoadMore(data: List<StuFile>) {
        mData.addAll(data)
        notifyDataSetChanged()
    }

}

class StufileViewHolder(view: View,listener:onStuFileItemClickListener) : RecyclerView.ViewHolder(view) {
    var title: TextView
    var type_image:ImageView
    var time:TextView
    var data: StuFile? = null
    init {
        this.title = view.findViewById(R.id.title) as TextView
        this.type_image = view.findViewById(R.id.type_image) as ImageView
        this.time = view.findViewById(R.id.time) as TextView
        view.setOnClickListener {
            listener.onClick(data)
        }
    }

    fun initView(stufile: StuFile) {
        data = stufile
        title.text = stufile.tittle
        time.text="${stufile.log_time}/${stufile.stu_time}"
        if (stufile.file_type=="视频"){
            type_image.setImageResource(R.drawable.video)
        }else if (stufile.file_type=="文档"){
            type_image.setImageResource(R.drawable.word)
        }
    }
}

interface onStuFileItemClickListener{
    fun onClick(data:StuFile?)
}
