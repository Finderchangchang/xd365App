package com.djj.xd365.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.djj.xd365.R
import com.djj.xd365.adapter.MainMenuAdapter
import com.djj.xd365.db.XDMenu
import com.djj.xd365.global.XD365Application
import com.djj.xd365.global.XD365http
import com.koushikdutta.async.future.FutureCallback
import com.tencent.bugly.beta.Beta
import kotlinx.android.synthetic.main.activity_main.*

class TouristMainActivity : AppCompatActivity() {


    lateinit var xD365Application: XD365Application

    // 定位相关
    private lateinit var mLocClient: LocationClient
    private var myListener = MyLocationListenner()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        actionBar.setBackgroundDrawable( ColorDrawable(Color.parseColor("#33000000")))
////google的actionbar是分为上下两栏显示的，上面的代码只能设置顶部actionbar的背景色，
////为了让下面的背景色一致，还需要添加一行代码：
//        actionBar.setSplitBackgroundDrawable( ColorDrawable(Color.parseColor("#33000000")))
        xD365Application = application as XD365Application
        //Beta.init(applicationContext, false)
        grid_menu.adapter = MainMenuAdapter(this@TouristMainActivity, XDMenu(false, false, false, false, true, false, true, false,false))
        grid_menu.setOnItemClickListener { _, _, position, _ ->
            val map = grid_menu.adapter.getItem(position) as Map<*, *>
            when (map["name"]) {
                R.string.bqstudy -> startActivity(Intent(this@TouristMainActivity, BqStudyActivity::class.java))
                R.string.zzstudy -> startActivity(Intent(this@TouristMainActivity, ZzStudyActivity::class.java))
                R.string.exam -> startActivity(Intent(this@TouristMainActivity, ExamActivity::class.java))
                R.string.studylog -> startActivity(Intent(this@TouristMainActivity, StudyLogActivity::class.java))
                R.string.carPosition -> startActivity(Intent(this@TouristMainActivity, TouristCarPositionActivity::class.java))
                R.string.violation -> startActivity(Intent(this@TouristMainActivity, TouristViolationActivity::class.java))
                R.string.roadInfo -> startActivity(Intent(this@TouristMainActivity, RoadInfoActivity::class.java))
                R.string.comMessage -> startActivity(Intent(this@TouristMainActivity, ComMessageActivity::class.java))
            }
        }
        getMyPosition()
    }

    private fun getMyPosition() {
        // 定位初始化
        mLocClient = LocationClient(this)
        mLocClient.registerLocationListener(myListener)
        val option = LocationClientOption()
        option.isOpenGps = true // 打开gps
        option.setCoorType("bd09ll") // 设置坐标类型
        option.setScanSpan(60 * 1000)
        mLocClient.locOption = option
        mLocClient.start()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_user, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            if (item.itemId == R.id.config) {
                val intent = Intent()
                intent.setClass(this@TouristMainActivity, TouristUserActivity::class.java)
                intent.putExtra("init", false)
                startActivity(intent)
                return true
            }
        }
        return false
    }


    /**
     * 定位SDK监听函数
     */
    inner class MyLocationListenner : BDAbstractLocationListener() {

        override fun onReceiveLocation(location: BDLocation?) {
            // map view 销毁后不在处理新接收的位置
            if (location == null) {
                return
            } else {
                XD365http.updateTouristPosition(this@TouristMainActivity, xD365Application.constant.tourist!!.id, location.longitude, location.latitude, FutureCallback { _, result ->
                    if (result!=null){
                        val resultStr=result.get("result").asString
                        if (resultStr=="OK"){}
                    }
                })
            }

        }

    }
}
