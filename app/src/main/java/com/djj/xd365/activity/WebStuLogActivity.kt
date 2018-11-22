package com.djj.xd365.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.djj.xd365.R
import com.djj.xd365.global.XD365http
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import kotlinx.android.synthetic.main.activity_file_media.*

class WebStuLogActivity : AppCompatActivity() {

    val SERVER_URL = XD365http.Server_Url+ "Stulog/stulogDetails_mobile.action"

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
                Toast.makeText(this@WebStuLogActivity, "Oh no! $description", Toast.LENGTH_SHORT).show()
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                //handler.cancel(); 默认的处理方式，WebView变成空白页
                handler?.proceed()//接受证书
                //handleMessag565e(Message msg); 其他处理

            }

        }

        val id = intent.getStringExtra("id")
        val postData ="?id=$id"
        content.loadUrl(SERVER_URL+postData)
    }
}
