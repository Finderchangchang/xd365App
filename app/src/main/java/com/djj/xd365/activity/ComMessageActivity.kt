package com.djj.xd365.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.djj.xd365.R
import com.djj.xd365.adapter.MessageAdapter
import com.djj.xd365.adapter.onComMessageItemClickListener
import com.djj.xd365.db.ComMessage
import com.djj.xd365.db.Common
import com.djj.xd365.global.Constant
import com.djj.xd365.global.XD365Application
import com.djj.xd365.global.XD365http
import com.koushikdutta.async.future.FutureCallback
import kotlinx.android.synthetic.main.activity_com_message.*

class ComMessageActivity : AppCompatActivity() {

    lateinit var constant: Constant
    lateinit var messageAdapter: MessageAdapter
    val PAGE_SIZE = 20
    var page = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_com_message)
        val xD365Application = (application as XD365Application)
        if (xD365Application.constant.driver == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        constant = xD365Application.constant
        messagelist.setOnRefreshListener { layout ->
            page = 0
            XD365http.getMessage(this@ComMessageActivity, constant.driver!!.id, page * PAGE_SIZE, PAGE_SIZE, FutureCallback<Common<ComMessage>> { e, result ->
                if (e == null && result != null) {
                    messageAdapter.onRefresh(result.rows)
                    layout.finishRefresh(true)
                } else {
                    layout.finishRefresh(false)
                }
            })
        }
        messagelist.setOnLoadMoreListener { layout ->
            page++
            XD365http.getMessage(this@ComMessageActivity, constant.driver!!.id, page * PAGE_SIZE, PAGE_SIZE, FutureCallback<Common<ComMessage>> { e, result ->
                if (e == null && result != null) {
                    messageAdapter.onLoadMore(result.rows)
                    layout.finishLoadMore(true)
                } else {
                    layout.finishLoadMore(false)
                }
            })
        }
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));//这里用线性宫格显示 类似于grid view
//        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));//这里用线性宫格显示 类似于瀑布流
        messagelistview.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(this, null, object : onComMessageItemClickListener {
            override fun onClick(data: ComMessage?) {
                if (data != null) {
                    MaterialDialog.Builder(this@ComMessageActivity)
                            .title(data.tittle)
                            .content(data.content)
                            .positiveText(R.string.sure)
                            .onPositive { dialog, _ ->
                                XD365http.updateMessageStatus(this@ComMessageActivity, data.pmid, FutureCallback { e, result ->
                                    if (e == null && result != null) {
                                        getData()
                                    }
                                })
                            }
                            .show()
                }

            }
        })
        messagelistview.adapter = messageAdapter
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getData() {
        XD365http.getMessage(this@ComMessageActivity, constant.driver!!.id, page * PAGE_SIZE, PAGE_SIZE, FutureCallback<Common<ComMessage>> { e, result ->
            if (e == null && result != null) {
                messageAdapter.onRefresh(result.rows)
            }
        })
    }


}
