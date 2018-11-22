package com.djj.xd365.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import com.djj.xd365.R
import com.djj.xd365.adapter.StuFileAdapter
import com.djj.xd365.adapter.onStuFileItemClickListener
import com.djj.xd365.db.Common
import com.djj.xd365.db.StuFile
import com.djj.xd365.global.Constant
import com.djj.xd365.global.XD365Application
import com.djj.xd365.global.XD365http
import com.koushikdutta.async.future.FutureCallback
import kotlinx.android.synthetic.main.activity_course.*
/**
 * 学习文档
 * */
class CourseActivity : AppCompatActivity() {

    lateinit var constant: Constant
    lateinit var stuFileAdapter: StuFileAdapter
    val PAGE_SIZE = 20
    var page = 0
    var logid = ""
    var file_list = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        constant = (application as XD365Application).constant
        logid = intent.getStringExtra("logid")
        file_list = intent.getStringExtra("file_list")
        filelist.setOnRefreshListener { layout ->
            page = 0
            XD365http.getCourseFile(this@CourseActivity, logid, file_list, page * PAGE_SIZE, PAGE_SIZE, FutureCallback<Common<StuFile>> { e, result ->
                if (e == null && result != null) {
                    stuFileAdapter.onRefresh(result.rows)
                    layout.finishRefresh(true)
                } else {
                    layout.finishRefresh(false)
                }
            })
            layout.finishRefresh(2000, true)
        }
        filelist.setOnLoadMoreListener { layout ->
            page++
            XD365http.getCourseFile(this@CourseActivity, logid, file_list, page * PAGE_SIZE, PAGE_SIZE, FutureCallback<Common<StuFile>> { e, result ->
                if (e == null && result != null) {
                    stuFileAdapter.onLoadMore(result.rows)
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
        filelistview.layoutManager = GridLayoutManager(this, 2)
        stuFileAdapter = StuFileAdapter(this, null, object : onStuFileItemClickListener {
            override fun onClick(data: StuFile?) {
                if (data != null) {
                    if (data.log_time >= data.stu_time) {
                        MaterialDialog.Builder(this@CourseActivity)
                                .title("提示")
                                .content("已完成本文档学习。")
                                .negativeText(R.string.sure)
                                .show()
                    } else {
                        if (data.begin_time <= 0) {
                            XD365http.updateStuLogFileTime(this@CourseActivity, data.id1, FutureCallback { e, result ->
                                if (e == null && result != null) {
                                    if (result.get("success").asBoolean) {
//                                        val intent = Intent(this@CourseActivity, FileMediaActivity::class.java)
                                        val intent = Intent(this@CourseActivity, ClassDetailActivity::class.java)
                                        intent.putExtra("filepath", data.url)//文件路径
                                        intent.putExtra("title", data.tittle)//标题
                                        intent.putExtra("needtime", data.stu_time)//
                                        intent.putExtra("donetime", data.log_time)
                                        intent.putExtra("ids", logid)
                                        intent.putExtra("fileid", data.id)
                                        startActivity(intent)
                                    } else {
                                        MaterialDialog.Builder(this@CourseActivity)
                                                .title("提示")
                                                .content("数据加载失败。")
                                                .negativeText(R.string.sure)
                                                .show()
                                    }
                                }
                            })
                        } else {
                            val intent = Intent(this@CourseActivity, ClassDetailActivity::class.java)

//                            val intent = Intent(this@CourseActivity, FileMediaActivity::class.java)
                            intent.putExtra("filepath", data.url)
                            intent.putExtra("title", data.tittle)
                            intent.putExtra("needtime", data.stu_time)
                            intent.putExtra("donetime", data.log_time)
                            intent.putExtra("ids", logid)
                            intent.putExtra("fileid", data.id)
                            startActivity(intent)
                        }
                    }
                }
            }
        })
        filelistview.adapter = stuFileAdapter
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            outState.putString("logid", logid)
            outState.putString("file_list", file_list)
        }

    }



    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            logid = savedInstanceState.getString("logid")
            file_list = savedInstanceState.getString("file_list")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!=null){
            when(item.itemId){
                android.R.id.home->{
                    close()
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    override fun onBackPressed() {
        close()

    }

    fun close(){
        MaterialDialog.Builder(this@CourseActivity)
                .title("提示")
                .content("确定要结束本次学习吗?")
                .negativeText(R.string.cancel)
                .positiveText(R.string.sure)
                .onPositive { _, _ ->
                    super.onBackPressed()
                }
                .onNegative { _, _ -> }
                .show()
    }

    private fun getData() {
        XD365http.getCourseFile(this@CourseActivity, logid, file_list, page * PAGE_SIZE, PAGE_SIZE, FutureCallback<Common<StuFile>> { e, result ->
            if (e == null && result != null) {
                stuFileAdapter.onRefresh(result.rows)
            }
        })

//        stuFileAdapter.onRefresh(listOf(StuFile("2018危险品运输车辆十条新政", 0, 12, "文档"),
//                StuFile("公路水路行业安全生产典型事故警示录", 0, 12, "视频"),
//                StuFile("春季行车安全注意事项", 0, 12, "文档")))
    }
}
