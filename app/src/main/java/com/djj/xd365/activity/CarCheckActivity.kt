package com.djj.xd365.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.djj.xd365.R
import com.djj.xd365.adapter.CarCheckAdapter
import com.djj.xd365.adapter.onCarCheckItemClickListener
import com.djj.xd365.db.CarCheck
import com.djj.xd365.global.Constant
import com.djj.xd365.global.XD365Application
import com.djj.xd365.global.XD365http
import com.koushikdutta.async.future.FutureCallback
import kotlinx.android.synthetic.main.activity_car_check.*

class CarCheckActivity : AppCompatActivity() {

    lateinit var constant: Constant
    private lateinit var carCheckAdapter: CarCheckAdapter
    private val PAGE_SIZE = 20
    private var page = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_check)
        constant = (application as XD365Application).constant
        checklist.setOnRefreshListener { layout ->
            page = 0
            XD365http.getCarCheck(this@CarCheckActivity, constant.driver!!.id, page * PAGE_SIZE, PAGE_SIZE, FutureCallback { e, result ->
                if (e == null && result != null) {
                    carCheckAdapter.onRefresh(result.rows)
                    layout.finishRefresh(true)
                } else {
                    layout.finishRefresh(false)
                }
            })
        }
        checklist.setOnLoadMoreListener { layout ->
            page++
            XD365http.getCarCheck(this@CarCheckActivity, constant.driver!!.id, page * PAGE_SIZE, PAGE_SIZE, FutureCallback { e, result ->
                if (e == null && result != null) {
                    carCheckAdapter.onLoadMore(result.rows)
                    layout.finishLoadMore(true)
                } else {
                    layout.finishLoadMore(false)
                }
            })
        }
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));//这里用线性宫格显示 类似于grid view
//        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));//这里用线性宫格显示 类似于瀑布流
        checklistview.layoutManager = LinearLayoutManager(this)
        carCheckAdapter = CarCheckAdapter(this, null, object : onCarCheckItemClickListener {
            override fun onXcqClick(data: CarCheck?) {
                if (data!=null){
                    val intent=Intent(this@CarCheckActivity,CarCheckAddActivity::class.java)
                    intent.putExtra("id",data.id)
                    intent.putExtra("type","xcq")
                    startActivity(intent)
                }
            }

            override fun onXczClick(data: CarCheck?) {
                if (data!=null){
                    val intent=Intent(this@CarCheckActivity,CarCheckAddActivity::class.java)
                    intent.putExtra("id",data.id)
                    intent.putExtra("type","xcz")
                    startActivity(intent)
                }
            }

            override fun onXchClick(data: CarCheck?) {
                if (data!=null){
                    val intent=Intent(this@CarCheckActivity,CarCheckAddActivity::class.java)
                    intent.putExtra("id",data.id)
                    intent.putExtra("type","xch")
                    startActivity(intent)
                }
            }

        })
        checklistview.adapter = carCheckAdapter
        addCheck.setOnClickListener {
            startActivity(Intent(this@CarCheckActivity,CarCheckAddMainActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getData() {
//        val result= listOf<CarCheck>(CarCheck("1","1","","",""),CarCheck("2","2","","",""), CarCheck("3","3","","",""))
//        carCheckAdapter.onRefresh(result)
        XD365http.getCarCheck(this@CarCheckActivity, constant.driver!!.id, page * PAGE_SIZE, PAGE_SIZE, FutureCallback { e, result ->
            if (e == null && result != null) {
                carCheckAdapter.onRefresh(result.rows)
            }
        })
    }
}
