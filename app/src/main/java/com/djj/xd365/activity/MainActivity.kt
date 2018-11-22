package com.djj.xd365.activity

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
 import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.view.Menu
import android.view.MenuItem
import cn.jpush.android.api.BasicPushNotificationBuilder
import cn.jpush.android.api.JPushInterface
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.model.LatLng
import com.djj.xd365.R
import com.djj.xd365.adapter.MainMenuAdapter
import com.djj.xd365.db.XDMenu
import com.djj.xd365.global.XD365Application
import com.djj.xd365.global.XD365http
import com.djj.xd365.jpush.DifferentNotifications
import com.djj.xd365.jpush.TagAliasOperatorHelper
import com.djj.xd365.util.OnlineTask
import com.koushikdutta.async.future.FutureCallback
import com.tencent.bugly.beta.Beta
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var xD365Application:XD365Application
    private val MQTT_IM_NOTIFICATION_ID = 1007

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
        xD365Application=application as XD365Application
        //Beta.init(applicationContext,false)
        grid_menu.adapter = MainMenuAdapter(this@MainActivity, XDMenu(true,true,true,true,true,false,true,true,true))
        grid_menu.setOnItemClickListener { parent, view, position, id ->
            val map = grid_menu.adapter.getItem(position) as Map<String, Int>
            when (map["name"]) {
                R.string.bqstudy -> startActivity(Intent(this@MainActivity, BqStudyActivity::class.java))
                R.string.zzstudy -> startActivity(Intent(this@MainActivity, ZzStudyActivity::class.java))
                R.string.exam -> startActivity(Intent(this@MainActivity, ExamActivity::class.java))
                R.string.studylog -> startActivity(Intent(this@MainActivity, StudyLogActivity::class.java))
                R.string.carPosition -> startActivity(Intent(this@MainActivity, CarPositionActivity::class.java))
                R.string.violation -> startActivity(Intent(this@MainActivity, ViolationActivity::class.java))
                R.string.roadInfo -> startActivity(Intent(this@MainActivity, RoadInfoActivity::class.java))
                R.string.comMessage->startActivity(Intent(this@MainActivity, ComMessageActivity::class.java))
                R.string.carcheck->startActivity(Intent(this@MainActivity,CarCheckActivity::class.java))
            }
        }
        val sequence=1001
        val tagAliasBean=TagAliasOperatorHelper.TagAliasBean()
        tagAliasBean.action=TagAliasOperatorHelper.ACTION_SET
        tagAliasBean.tags= setOf(xD365Application.constant.driver!!.company)
        tagAliasBean.isAliasAction=false
        TagAliasOperatorHelper.getInstance().handleAction(applicationContext, sequence, tagAliasBean)

        val builder = BasicPushNotificationBuilder(this@MainActivity)
        builder.statusBarDrawable = R.mipmap.xinda
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL  //设置为点击后自动消失
        builder.notificationDefaults = Notification.DEFAULT_SOUND  //设置为铃声（ Notification.DEFAULT_SOUND）或者震动（ Notification.DEFAULT_VIBRATE）

        JPushInterface.setPushNotificationBuilder(1, builder)
        getMyPosition()
        OnlineTask(this,xD365Application.constant.driver!!.id,60*1000).start()
    }

    private fun getMyPosition() {
        // 定位初始化
        mLocClient = LocationClient(this)
        mLocClient.registerLocationListener(myListener)
        val option = LocationClientOption()
        option.isOpenGps = true // 打开gps
        option.setCoorType("bd09ll") // 设置坐标类型
        option.setScanSpan(60*1000)
        mLocClient.locOption = option
        mLocClient.start()
    }

    override fun onResume() {
        super.onResume()
        XD365http.getMessageCount(this,xD365Application.constant.driver!!.id, FutureCallback { e, result ->
            if (e==null&&result!=null){
                val count= result.toInt()
                updateMessageCount(count)
                val notification = makeNewNotification(this)
                DifferentNotifications.showBubble(this, notification, MQTT_IM_NOTIFICATION_ID, count)

            }
        })
    }

    private fun updateMessageCount(count:Int) {
        (grid_menu.adapter as MainMenuAdapter).updateCornerCount( R.string.comMessage,count)
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
                intent.setClass(this@MainActivity, UserActivity::class.java)
                intent.putExtra("init", false)
                startActivity(intent)
                return true
            }
        }
        return false
    }

    fun makeNewNotification(context: Context): Notification {
        val content = "您有新的消息."
        val builder = NotificationCompat.Builder(context)
        builder.setSmallIcon(R.mipmap.xinda)
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.xinda))
        builder.setTicker("您有新的消息.")
        builder.setWhen(System.currentTimeMillis())
        val intent = Intent(context, ComMessageActivity::class.java)

        intent.action = context.getApplicationContext().getPackageName()
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pi)
        builder.setContentTitle(context.getResources().getText(R.string.app_name))
        builder.setContentText(content)

        val n = builder.build()
        var defaults = Notification.DEFAULT_LIGHTS

        defaults = defaults or Notification.DEFAULT_SOUND

        defaults = defaults or Notification.DEFAULT_VIBRATE


        n.defaults = defaults
        n.flags = Notification.FLAG_SHOW_LIGHTS or Notification.FLAG_AUTO_CANCEL
        return n
    }

    /**
     * 定位SDK监听函数
     */
    inner class MyLocationListenner : BDAbstractLocationListener() {

        override fun onReceiveLocation(location: BDLocation?) {
            // map view 销毁后不在处理新接收的位置
            if (location == null) {
                return
            }else{
                XD365http.updateDriverPosition(this@MainActivity,xD365Application.constant.driver!!.id,location.longitude,location.latitude, FutureCallback { e, result ->
                    if (result!=null){
                        val resultStr=result.get("result").asString
                        if (resultStr=="OK"){}
                    }
                })
            }

        }

    }
}
