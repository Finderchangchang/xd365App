package com.djj.xd365.activity

import android.graphics.Canvas
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import com.bumptech.glide.load.model.ResourceLoader

import com.djj.xd365.R
import com.djj.xd365.global.XD365http.Server_Url
import com.lidong.pdf.listener.OnDrawListener
import com.lidong.pdf.listener.OnLoadCompleteListener
import com.lidong.pdf.listener.OnPageChangeListener
import kotlinx.android.synthetic.main.activity_class_detail.*
import java.io.File
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.WindowManager
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer


class ClassDetailActivity : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener, OnDrawListener {
    override fun onLayerDrawn(canvas: Canvas?, pageWidth: Float, pageHeight: Float, displayedPage: Int) {

    }

    override fun loadComplete(nbPages: Int) {

    }

    override fun onPageChanged(page: Int, pageCount: Int) {

    }

    var orientationUtils: OrientationUtils? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_detail)
        val filepath = intent.getStringExtra("filepath")
        val title = intent.getStringExtra("title")
        val needtime = intent.getIntExtra("needtime", 0)
        val donetime = intent.getIntExtra("donetime", 0)
        val ids = intent.getStringExtra("ids")
        val fileid = intent.getStringExtra("fileid")
        var true_url = Server_Url + filepath.substring(1, filepath.length)
        var word_num = SimpleDateFormat("yyyyMMdd").format(Date())
        if (true_url.contains(".mp4")) {
            true_url = true_url.replace("\\", "/")
            detail_player.setUp(true_url, false, null)
            detail_player.backButton.visibility = View.GONE

            orientationUtils = OrientationUtils(this, detail_player)
            //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
            detail_player.fullscreenButton.setOnClickListener {
                orientationUtils?.resolveByClick()
                detail_player!!.startWindowFullscreen(this@ClassDetailActivity, true, true)
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
            detail_player.setSeekOnStart(1000 * 10)
            detail_player.startPlayLogic()
        } else {
            displayFromFile1(true_url, "$word_num.$fileid.pdf")
        }
    }

    override fun onPause() {
        super.onPause()
        detail_player.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        detail_player.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
        if (orientationUtils != null)
            orientationUtils?.releaseListener()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //如果旋转了就全屏

        if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
            if (!detail_player!!.isIfCurrentIsFullscreen) {
                //highApiEffects(false)
                //全屏
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

                detail_player!!.startWindowFullscreen(this@ClassDetailActivity, true, true)
                //detailPlayer!!.setText("0000000")
            }
        } else {
            if (orientationUtils != null) {
                orientationUtils!!.isEnable = true
            }
            window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

    override fun onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils!!.backToProtVideo()
            orientationUtils!!.resolveByClick()
        }
        //super.onBackPressed()
    }

    /**
     * 获取打开网络的pdf文件
     * @param fileUrl
     * @param fileName
     */
    private fun displayFromFile1(fileUrl: String, fileName: String) {
        pdfView.fileFromLocalStorage(this, this, this, fileUrl, fileName)   //设置pdf文件地址

    }

}
