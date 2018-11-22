package com.djj.xd365.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.djj.xd365.R
import com.djj.xd365.global.XD365Application
import com.djj.xd365.global.XD365http
import com.koushikdutta.async.future.FutureCallback
import kotlinx.android.synthetic.main.activity_user.*
import org.devio.takephoto.app.TakePhoto
import org.devio.takephoto.compress.CompressConfig
import org.devio.takephoto.model.TImage
import org.devio.takephoto.model.TResult
import org.devio.takephoto.model.TakePhotoOptions
import java.io.File

class UserActivity : AppCompatActivity() {

    lateinit var preferences: SharedPreferences
    lateinit var xD365Application:XD365Application
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        xD365Application=application as XD365Application
        preferences = getSharedPreferences(xD365Application.constant.APP_CONFIG, Context.MODE_PRIVATE)
        sign_out.setOnClickListener {
            MaterialDialog.Builder(this@UserActivity)
                    .title("提示")
                    .content("确定要注销当前用户吗?")
                    .negativeText(R.string.cancel)
                    .positiveText(R.string.sure)
                    .onPositive { _, _ ->
                        val editer = preferences.edit()
                        editer.remove(xD365Application.constant.KEY_PASSWORD)
                        editer.apply()
                        val intent = Intent(this@UserActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    .onNegative { _, _ ->

                    }
                    .show()

        }
        user.setOnClickListener {
           startActivity(Intent(this@UserActivity,UserEditActivity::class.java))
        }

    }

    override fun onResume() {
        super.onResume()
        xD365Application = application as XD365Application
        name.text = xD365Application.constant.driver?.name
        account.text = xD365Application.constant.driver?.account
        XD365http.loadDriverImage(user_image,xD365Application.constant.driver?.hardphoto)
    }

    private fun makeImageUri(): Uri {
        val file = File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg")
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        return Uri.fromFile(file)
    }

    private fun configTakePhotoOption(takePhoto: TakePhoto) {
        val builder = TakePhotoOptions.Builder().setWithOwnGallery(false).setCorrectImage(false).create()
        takePhoto.setTakePhotoOptions(builder)
    }

    private fun configCompress(takePhoto: TakePhoto) {
        val maxSize = 102400
        val width = 400
        val height = 400
        val config = CompressConfig.Builder().setMaxSize(maxSize).setMaxPixel(if (width >= height) width else height).enableReserveRaw(false).create()
        takePhoto.onEnableCompress(config, true)
    }
}
