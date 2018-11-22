package com.djj.xd365.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.djj.xd365.R
import com.djj.xd365.adapter.BqStudyAdapter
import com.djj.xd365.adapter.onItemClickListener
import com.djj.xd365.db.Common
import com.djj.xd365.db.Stucourse
import com.djj.xd365.global.Constant
import com.djj.xd365.global.XD365Application
import com.djj.xd365.global.XD365http
import com.google.gson.JsonObject
import com.koushikdutta.async.future.FutureCallback
import kotlinx.android.synthetic.main.activity_bq_study.*


class ZzStudyActivity : AppCompatActivity() {


    lateinit var constant: Constant
    lateinit var zzStudyAdapter: BqStudyAdapter
    val PAGE_SIZE = 20
    var page = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bq_study)
        constant = (application as XD365Application).constant
        studylist.setOnRefreshListener { layout ->
            page = 0
            XD365http.getZzCourse(this@ZzStudyActivity, constant.driver!!.id, page * PAGE_SIZE, PAGE_SIZE, FutureCallback<Common<Stucourse>> { e, result ->
                if (e == null && result != null) {
                    zzStudyAdapter.onRefresh(result.rows)
                    layout.finishRefresh(true)
                } else {
                    layout.finishRefresh(false)
                }
            })
        }
        studylist.setOnLoadMoreListener { layout ->
            page++
            XD365http.getZzCourse(this@ZzStudyActivity, constant.driver!!.id, page * PAGE_SIZE, PAGE_SIZE, FutureCallback<Common<Stucourse>> { e, result ->
                if (e == null && result != null) {
                    zzStudyAdapter.onLoadMore(result.rows)
                    layout.finishLoadMore(true)
                } else {
                    layout.finishLoadMore(false)
                }
            })
        }
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));//这里用线性宫格显示 类似于grid view
//        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));//这里用线性宫格显示 类似于瀑布流
        studylistview.layoutManager = LinearLayoutManager(this)
        zzStudyAdapter = BqStudyAdapter(this, null, object : onItemClickListener {
            override fun onClick(data: Stucourse?) {
                if (data != null) {
                    XD365http.updateStudyCount(this@ZzStudyActivity, constant.driver!!.id, data.id,data.file_list, FutureCallback { e, result ->
                        if (e == null && result != null) {
                            val intent = Intent(this@ZzStudyActivity, CourseActivity::class.java)
                            intent.putExtra("logid", result.stulogid)
                            intent.putExtra("file_list", data.file_list)
                            startActivity(intent)
                        }
                    })

                }
            }
        })
        studylistview.adapter = zzStudyAdapter
        studylistview
        getData()
    }

    private fun getData() {
        XD365http.getZzCourse(this@ZzStudyActivity, constant.driver!!.id, page * PAGE_SIZE, PAGE_SIZE, FutureCallback<Common<Stucourse>> { e, result ->
            if (e == null && result != null) {
                zzStudyAdapter.onRefresh(result.rows)
            }
        })
    }
}
