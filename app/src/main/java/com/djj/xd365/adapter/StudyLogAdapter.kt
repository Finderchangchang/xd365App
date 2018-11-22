package com.djj.xd365.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.djj.xd365.R
import com.djj.xd365.db.Exam
import com.djj.xd365.db.StudyLog

/**
 * Created by liufeng on 2018-07-01.
 * 用于 xd365
 */
class StudyLogAdapter(val context: Context, data: MutableList<StudyLog>?, val listener: OnStudyLogItemClickListener) : RecyclerView.Adapter<StudyLogViewHolder>() {

    val mData: MutableList<StudyLog> = data ?: mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): StudyLogViewHolder {
        return StudyLogViewHolder(LayoutInflater.from(context).inflate(R.layout.item_studylog, parent, false), listener)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: StudyLogViewHolder?, position: Int) {
        holder?.initView(mData[position])
    }

    fun onRefresh(data: List<StudyLog>) {
        mData.clear()
        onLoadMore(data)
    }

    fun onLoadMore(data: List<StudyLog>) {
        mData.addAll(data)
        notifyDataSetChanged()
    }

}

class StudyLogViewHolder(view: View, listener: OnStudyLogItemClickListener) : RecyclerView.ViewHolder(view) {
    var title: TextView
    var data: StudyLog? = null

    init {
        this.title = view.findViewById(R.id.title) as TextView
        view.setOnClickListener {
            listener.onClick(data)
        }
    }

    fun initView(studylog: StudyLog) {
        data = studylog
        title.text = studylog.courseid
    }
}

interface OnStudyLogItemClickListener {
    fun onClick(data: StudyLog?)
}
