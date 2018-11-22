package com.djj.xd365.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.djj.xd365.R
import com.djj.xd365.adapter.BqStudyAdapter
import com.djj.xd365.adapter.onItemClickListener
import com.djj.xd365.db.Stucourse
import com.djj.xd365.global.Constant
import com.djj.xd365.global.XD365Application
import com.djj.xd365.global.XD365http
import com.koushikdutta.async.future.FutureCallback
import kotlinx.android.synthetic.main.activity_bq_study.*


class BqStudyActivity : AppCompatActivity() {

    lateinit var constant: Constant
    private lateinit var bqStudyAdapter: BqStudyAdapter
    private val PAGE_SIZE = 20
    private var page = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bq_study)
        constant = (application as XD365Application).constant
        studylist.setOnRefreshListener { layout ->
            page = 0
            XD365http.getCourse(this@BqStudyActivity, constant.driver!!.id, page * PAGE_SIZE, PAGE_SIZE, FutureCallback { e, result ->
                if (e == null && result != null) {
                    bqStudyAdapter.onRefresh(result.rows)
                    layout.finishRefresh(true)
                } else {
                    layout.finishRefresh(false)
                }
            })
        }
        studylist.setOnLoadMoreListener { layout ->
            page++
            XD365http.getCourse(this@BqStudyActivity, constant.driver!!.id, page * PAGE_SIZE, PAGE_SIZE, FutureCallback { e, result ->
                if (e == null && result != null) {
                    bqStudyAdapter.onLoadMore(result.rows)
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
        bqStudyAdapter = BqStudyAdapter(this, null, object : onItemClickListener {
            override fun onClick(data: Stucourse?) {
                if (data != null) {
                    XD365http.updateStudyCount(this@BqStudyActivity, constant.driver!!.id, data.id,data.file_list, FutureCallback { e, result ->
                        if (e == null && result != null) {
                            val intent = Intent(this@BqStudyActivity, CourseActivity::class.java)
                            intent.putExtra("logid", result.stulogid)
                            intent.putExtra("file_list", data.file_list)
                            startActivity(intent)
                        }
                    })


                }

            }
        })
        studylistview.adapter = bqStudyAdapter
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getData() {
        XD365http.getCourse(this@BqStudyActivity, constant.driver!!.id, page * PAGE_SIZE, PAGE_SIZE, FutureCallback { e, result ->
            if (e == null && result != null) {
                bqStudyAdapter.onRefresh(result.rows)
            }
        })
    }

}
