package com.djj.xd365.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.djj.xd365.R
import com.djj.xd365.global.XD365Application
import com.djj.xd365.global.XD365http
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import kotlinx.android.synthetic.main.activity_file_media.*

class ViolationActivity : AppCompatActivity() {

    val SERVER_URL = XD365http.Server_Url + "Car/startpeccancy.action"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_media)
        content.getSettings().setJavaScriptEnabled(true)

        content.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(view: WebView, progress: Int) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                if (progress in 1..99) {
                    load_progress.visibility = View.VISIBLE
                    load_progress.progress = progress
                } else {
                    load_progress.visibility = View.GONE
                }
            }
        }
        content.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                Toast.makeText(this@ViolationActivity, "Oh no! $description", Toast.LENGTH_SHORT).show()
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                //handler.cancel(); 默认的处理方式，WebView变成空白页
                handler?.proceed()//接受证书
                //handleMessage(Message msg); 其他处理

            }

        }
        val xD365Application = application as XD365Application

        val id = if (xD365Application.constant.driver != null) {
            xD365Application.constant.driver!!.carid
        } else {
            xD365Application.constant.tourist!!.id
        }
//        val  bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.size)
        val postData = "?id=$id"
        content.loadUrl(SERVER_URL + postData)
        //content.loadUrl("https://192.168.0.80:8443/xd365/filemedia/readFile.action?filepath=\\upload\\20180705\\a319e16a-6697-4694-82c8-43b96497972c_.mp4&title=mp4%E8%A7%86%E9%A2%91%E5%AD%A6%E4%B9%A0")
    }
}
