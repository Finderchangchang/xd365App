package com.djj.xd365.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.djj.xd365.R
import com.djj.xd365.db.Exam

/**
 * Created by liufeng on 2018-07-01.
 * 用于 xd365
 */
class ExamAdapter(val context: Context, data: MutableList<Exam>?, val listener: OnExamItemClickListener) : RecyclerView.Adapter<ExamViewHolder>() {

    val mData: MutableList<Exam> = data ?: mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ExamViewHolder {
        return ExamViewHolder(LayoutInflater.from(context).inflate(R.layout.item_exam, parent, false), listener)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ExamViewHolder?, position: Int) {
        holder?.initView(mData[position])
    }

    fun onRefresh(data: List<Exam>) {
        mData.clear()
        onLoadMore(data)
    }

    fun onLoadMore(data: List<Exam>) {
        mData.addAll(data)
        notifyDataSetChanged()
    }

}

class ExamViewHolder(view: View, listener: OnExamItemClickListener) : RecyclerView.ViewHolder(view) {
    var title: TextView
    var data: Exam? = null

    init {
        this.title = view.findViewById(R.id.title) as TextView
        view.setOnClickListener {
            listener.onClick(data)
        }
    }

    fun initView(exam: Exam) {
        data = exam
        title.text = exam.name
    }
}

interface OnExamItemClickListener {
    fun onClick(data: Exam?)
}
