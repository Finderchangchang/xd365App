package com.djj.xd365.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.djj.xd365.R
import com.djj.xd365.adapter.OnStudyLogItemClickListener
import com.djj.xd365.adapter.StudyLogAdapter
import com.djj.xd365.db.Common
import com.djj.xd365.db.StudyLog
import com.djj.xd365.global.Constant
import com.djj.xd365.global.XD365Application
import com.djj.xd365.global.XD365http
import com.koushikdutta.async.future.FutureCallback
import kotlinx.android.synthetic.main.activity_study_log.*

class StudyLogActivity : AppCompatActivity() {

    lateinit var constant: Constant
    lateinit var logAdapter: StudyLogAdapter
    val PAGE_SIZE = 20
    var page = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_log)
        constant = (application as XD365Application).constant
        loglist.setOnRefreshListener { layout ->
            page = 0
            XD365http.getStulog(this@StudyLogActivity, constant.driver!!.id, page * PAGE_SIZE, PAGE_SIZE, FutureCallback<Common<StudyLog>> { e, result ->
                if (e == null && result != null) {
                    logAdapter.onRefresh(result.rows)
                    layout.finishRefresh(true)
                } else {
                    layout.finishRefresh(false)
                }
            })
            layout.finishRefresh(2000, true)
        }
        loglist.setOnLoadMoreListener { layout ->
            page++
            XD365http.getStulog(this@StudyLogActivity, constant.driver!!.id, page * PAGE_SIZE, PAGE_SIZE, FutureCallback<Common<StudyLog>> { e, result ->
                if (e == null && result != null) {
                    logAdapter.onLoadMore(result.rows)
                    layout.finishLoadMore(true)
                } else {
                    layout.finishLoadMore(false)
                }
            })
            layout.finishLoadMore(2000)
        }
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));//这里用线性宫格显示 类似于grid view
//        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));//这里用线性宫格显示 类似于瀑布流
        loglistview.layoutManager = LinearLayoutManager(this)
        logAdapter = StudyLogAdapter(this, null, object : OnStudyLogItemClickListener {
            override fun onClick(data: StudyLog?) {
                if(data!=null){
                    val intent=Intent(this@StudyLogActivity,WebStuLogActivity::class.java)
                    intent.putExtra("id",data.id)
                    startActivity(intent)
                }
            }
        })
        loglistview.adapter = logAdapter
        getData()
    }

    private fun getData() {
        XD365http.getStulog(this@StudyLogActivity, constant.driver!!.id, page * PAGE_SIZE, PAGE_SIZE, FutureCallback<Common<StudyLog>> { e, result ->
            if (e == null && result != null) {
                logAdapter.onRefresh(result.rows)
            }
        })
//        logAdapter.onRefresh(listOf(StudyLog("2018年一月份安全知识学习"),
//                StudyLog("2018年二月份安全知识学习"),
//                StudyLog("2018年三月份安全知识学习")))
    }
}
