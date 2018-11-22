package com.djj.xd365.global

import android.app.Application
import android.content.Context
import android.util.Log
import cn.jpush.android.api.JPushInterface
import com.baidu.mapapi.SDKInitializer
import com.tencent.bugly.beta.Beta
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.smtt.sdk.QbSdk
import java.nio.charset.Charset

/**
 * Created by liufeng on 2018-06-30.
 * 用于 xd365
 */
class XD365Application : Application() {

    lateinit var constant: Constant
    lateinit var weChatApi:IWXAPI
    companion object {
        var context: Context? = null
        var api: IWXAPI? = null
        var wx_id: String = "wxe2f463e8626df1ff"//微信的id
    }
    override fun onCreate() {
        super.onCreate()
        constant= Constant()
        SDKInitializer.initialize(this)

        //极光推送
        JPushInterface.setDebugMode(true)
        JPushInterface.init(this)

        //Bugly
        val strategy=CrashReport.UserStrategy(this)
        strategy.setCrashHandleCallback(object: CrashReport.CrashHandleCallback(){
            override fun onCrashHandleStart(crashType: Int, errorType: String?, errorMessage: String?, errorStack: String?): MutableMap<String, String> {
                val  map = LinkedHashMap<String, String>()
                val x5CrashInfo = com.tencent.smtt.sdk.WebView.getCrashExtraMessage(this@XD365Application)
                map["x5crashInfo"] = x5CrashInfo
                return map
            }

            override fun onCrashHandleStart2GetExtraDatas(crashType: Int, errorType: String?, errorMessage: String?, errorStack: String?): ByteArray? {
                try {
                        return "Extra data.".toByteArray(Charset.forName("UTF-8"))
                } catch (e:Exception) {
                        return null
                }
            }
        })
        CrashReport.initCrashReport(applicationContext, "da19d751b6", false,strategy)
        Beta.autoInit = false

        //tbs浏览服务
        QbSdk.initX5Environment(this,object:QbSdk.PreInitCallback{
            override fun onCoreInitFinished() {
                Log.d("tbs","onCoreInitFinished")
            }

            override fun onViewInitFinished(p0: Boolean) {
                Log.d("tbs", "onViewInitFinished$p0")
            }
        })

        //微信sdk
        weChatApi=WXAPIFactory.createWXAPI(this,XD365Application.wx_id,true)
        weChatApi.registerApp(XD365Application.wx_id)
    }
}