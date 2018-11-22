package com.djj.xd365.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.djj.xd365.R
import com.djj.xd365.global.XD365http
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import kotlinx.android.synthetic.main.activity_file_media.*
import org.apache.http.util.EncodingUtils


class FileMediaActivity : AppCompatActivity() {

    val SERVER_URL = XD365http.Server_Url + "filemedia/studyFile.action"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_file_media)

//        //声明WebSettings子类
//        val webSettings = content.settings
//
////如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
//        webSettings.setJavaScriptEnabled(true)
//
////支持插件
//       // webSettings.setPluginsEnabled(true)
//
////设置自适应屏幕，两者合用
//        webSettings.setUseWideViewPort(true) //将图片调整到适合webview的大小
//        webSettings.setLoadWithOverviewMode(true) // 缩放至屏幕的大小
//
////缩放操作
//        webSettings.setSupportZoom(false) //支持缩放，默认为true。是下面那个的前提。
//   //     webSettings.setBuiltInZoomControls(true) //设置内置的缩放控件。若为false，则该WebView不可缩放
//   //     webSettings.setDisplayZoomControls(false) //隐藏原生的缩放控件
//
////其他细节操作
//        //webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK) //关闭webview中缓存
//       // webSettings.setAllowFileAccess(true) //设置可以访问文件
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true) //支持通过JS打开新窗口
//        webSettings.setLoadsImagesAutomatically(true) //支持自动加载图片
//        webSettings.setDefaultTextEncodingName("utf-8")//设置编码格式
//        content.loadUrl("https://www.baidu.com")
//        content.webViewClient = object : WebViewClient() {
//            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//                view.loadUrl(url)
//                return true
//            }
//        }


        content.getSettings().setJavaScriptEnabled(true)
        // 通过addJavascriptInterface()将Java对象映射到JS对象
        //参数1：Javascript对象名
        //参数2：Java对象名
        content.addJavascriptInterface(JsFunction(), "mobile")//AndroidtoJS类对象映射到js的test对象

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
                Toast.makeText(this@FileMediaActivity, "Oh no! $description", Toast.LENGTH_SHORT).show()
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                //handler.cancel(); 默认的处理方式，WebView变成空白页
                handler?.proceed()//接受证书
                //handleMessage(Message msg); 其他处理
            }

//            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {

//
//            }

        }

        val filepath = intent.getStringExtra("filepath")
        val title = intent.getStringExtra("title")
        val needtime = intent.getIntExtra("needtime", 0)
        val donetime = intent.getIntExtra("donetime", 0)
        val ids = intent.getStringExtra("ids")
        val fileid = intent.getStringExtra("fileid")

        val postData = "filepath=$filepath&title=$title&needtime=$needtime&donetime=$donetime&ids=$ids&fileid=$fileid"
        //content.loadUrl(SERVER_URL+postData)
        content.postUrl(SERVER_URL, EncodingUtils.getBytes(postData, "BASE64"))
    }

    override fun onResume() {
        super.onResume()
        if (content != null) {
            content.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (content != null) {
            content.onPause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (content != null) {
            content.destroy()
        }
    }

    inner class JsFunction {
        // 定义JS需要调用的方法
        // 被JS调用的方法必须加入@JavascriptInterface注解
        @JavascriptInterface
        fun close() {
            this@FileMediaActivity.finish()
        }
    }
}
