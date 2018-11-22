package com.djj.xd365.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import com.djj.xd365.R
import kotlinx.android.synthetic.main.activity_launcher.*
import com.baidu.mapapi.SDKInitializer
import android.widget.TextView
import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log
import com.djj.xd365.activity.LauncherActivity.SDKReceiver
import android.content.IntentFilter
import android.graphics.Typeface


class LauncherActivity : AppCompatActivity() {

    private var launcherView: LinearLayout? = null
    private var mFadeIn: Animation? = null
    private var mFadeInScale: Animation? = null

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    inner class SDKReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val s = intent.action
            var text=""
            if (s == SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR) {
                text = ("key 验证出错! 错误码 :" + intent.getIntExtra(SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_CODE, 0)
                        + " ; 请在 AndroidManifest.xml 文件中检查 key 设置")
            } else if (s == SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK) {
                text = "key 验证成功! 功能可以正常使用"
            } else if (s == SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR) {
                text = "网络出错"
            }
            Log.d("baidusdk",text)
        }
    }

    private var mReceiver: SDKReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_launcher)
        launcherView = this.findViewById(R.id.launcherView) as LinearLayout
        // Font path
        val fontPath1 = "fonts/jianxiqian.ttf"
        val fontPath2 = "fonts/jianchilun.ttf"
        val tf = Typeface.createFromAsset(assets, fontPath2)
        textView1.typeface = tf
        val tf2 = Typeface.createFromAsset(assets, fontPath1)
        version.typeface = tf2
        val packageInfo = applicationContext.packageManager.getPackageInfo(packageName, 0)
        version.text = "V" + packageInfo.versionName

        // 注册 SDK 广播监听者
        val iFilter = IntentFilter()
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)
        mReceiver = SDKReceiver()
        registerReceiver(mReceiver, iFilter)


        init()
        setListener()

    }

    private fun setListener() {

        mFadeIn!!.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationRepeat(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                launcherView!!.startAnimation(mFadeInScale)
            }
        })

        mFadeInScale!!.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationRepeat(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                val intent = Intent()
                intent.setClass(this@LauncherActivity, LoginActivity::class.java)
                intent.putExtra("init", true)
                startActivity(intent)
                finish()
            }
        })

    }

    private fun init() {
        initAnim()
        launcherView!!.startAnimation(mFadeIn)
    }

    private fun initAnim() {
        mFadeIn = AnimationUtils.loadAnimation(this@LauncherActivity,
                R.anim.welcome_fade_in)
        mFadeIn!!.duration = 2000
        mFadeIn!!.fillAfter = true

        mFadeInScale = AnimationUtils.loadAnimation(this@LauncherActivity,
                R.anim.welcome_fade_in_scale)
        mFadeInScale!!.duration = 1000
        mFadeInScale!!.fillAfter = true
    }

    override fun onDestroy() {
        super.onDestroy()
        // 取消监听 SDK 广播
        unregisterReceiver(mReceiver)
    }
}
