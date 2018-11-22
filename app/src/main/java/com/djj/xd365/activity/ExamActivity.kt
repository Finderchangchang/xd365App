package com.djj.xd365.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.djj.xd365.R
import com.djj.xd365.adapter.ExamAdapter
import com.djj.xd365.adapter.OnExamItemClickListener
import com.djj.xd365.db.Common
import com.djj.xd365.db.Exam
import com.djj.xd365.global.Constant
import com.djj.xd365.global.XD365Application
import com.djj.xd365.global.XD365http
import com.koushikdutta.async.future.FutureCallback
import kotlinx.android.synthetic.main.activity_exam.*


class ExamActivity : AppCompatActivity() {

    lateinit var constant: Constant
    lateinit var examAdapter: ExamAdapter
    val PAGE_SIZE = 20
    var page = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam)
        constant = (application as XD365Application).constant
        examlist.setOnRefreshListener { layout ->
            page = 0
            XD365http.getExam(this@ExamActivity, constant.driver!!.id, page * PAGE_SIZE, PAGE_SIZE, FutureCallback<Common<Exam>> { e, result ->
                if (e == null && result != null) {
                    examAdapter.onRefresh(result.rows)
                    layout.finishRefresh(true)
                } else {
                    layout.finishRefresh(false)
                }
            })
            layout.finishRefresh(2000, true)
        }
        examlist.setOnLoadMoreListener { layout ->
            page++
            XD365http.getExam(this@ExamActivity, constant.driver!!.id, page * PAGE_SIZE, PAGE_SIZE, FutureCallback<Common<Exam>> { e, result ->
                if (e == null && result != null) {
                    examAdapter.onLoadMore(result.rows)
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
        examlistview.layoutManager = LinearLayoutManager(this)
        examAdapter = ExamAdapter(this, null, object : OnExamItemClickListener {
            override fun onClick(data: Exam?) {
                if (data!=null){
                    if(data.car_type=="未通过"){
                        val intent=Intent(this@ExamActivity, ExamStartActivity::class.java)
                        intent.putExtra("examid",data.id)
                        intent.putExtra("name",data.name)
                        startActivity(intent)
                    }else{
                        MaterialDialog.Builder(this@ExamActivity)
                                .title("提示")
                                .content("此考试已经通过，无需再进行考试！")
                                .negativeText(R.string.sure)
                                .show()
                    }
                }

            }
        })
        examlistview.adapter = examAdapter
        getData()
    }

    private fun getData() {
        XD365http.getExam(this@ExamActivity, constant.driver!!.id, page * PAGE_SIZE, PAGE_SIZE, FutureCallback<Common<Exam>> { e, result ->
            if (e == null && result != null) {
                examAdapter.onRefresh(result.rows)
            }
        })
//        examAdapter.onRefresh(listOf(Exam("2018年一月份安全考试"),
//                Exam("2018年二月份安全考试"),
//                Exam("2018年三月份安全考试")))
    }
}
