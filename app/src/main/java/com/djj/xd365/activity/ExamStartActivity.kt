package com.djj.xd365.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import com.djj.xd365.R
import com.djj.xd365.global.XD365Application
import com.djj.xd365.global.XD365http
import com.koushikdutta.async.future.FutureCallback
import kotlinx.android.synthetic.main.activity_exam_start.*
import org.devio.takephoto.app.TakePhoto
import org.devio.takephoto.app.TakePhotoActivity
import org.devio.takephoto.compress.CompressConfig
import org.devio.takephoto.model.TImage
import org.devio.takephoto.model.TResult
import org.devio.takephoto.model.TakePhotoOptions
import java.io.File


class ExamStartActivity : TakePhotoActivity() {


    var imageUri: Uri = makeImageUri()
    var imageResult: TImage? = null
    var name = ""
    var examid = ""
    var driverid=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam_start)
        val xD365Application=application as XD365Application
        driverid=xD365Application.constant.driver!!.id
        name = intent.getStringExtra("name")
        examid = intent.getStringExtra("examid")
        exam_name.text = name
        image.setOnClickListener {
//            val dialog = PhotoSelFragment()
//            dialog.listener = object : PhotoSelFragment.OnPhotoTaskSel {
//                override fun onSelPhoto() {
//                    configCompress(takePhoto)
//                    configTakePhotoOption(takePhoto)
//                    takePhoto.onPickFromGallery()
//                }
//
//                override fun onCamera() {
//                    configCompress(takePhoto)
//            configTakePhotoOption(takePhoto)
//            takePhoto.onPickFromCapture(imageUri)
//                }
//            }
//            dialog.show(fragmentManager, "loginDialog")
            configCompress(takePhoto)
            configTakePhotoOption(takePhoto)
            takePhoto.onPickFromCapture(imageUri)
        }
        start_exam.setOnClickListener {
            if (imageResult != null) {
                XD365http.uploadImage(this@ExamStartActivity, imageResult!!.compressPath, driverid, examid, FutureCallback { e, result ->
                    if (e == null && result != null) {
                        if (result.get("success").asBoolean) {
                            val examlogid=result.get("examlogid").asString
                            val intent = Intent(this@ExamStartActivity, WebExamActivity::class.java)
                            intent.putExtra("examlogid", examlogid)
                            intent.putExtra("examid", examid)
                            startActivity(intent)
                            finish()
                        }
                    }
                })
            } else {
                Toast.makeText(this, "请选择照片", Toast.LENGTH_SHORT).show()
            }
        }
        cancel.setOnClickListener { finish() }
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
        val width = 300
        val height = 400
        val config = CompressConfig.Builder().setMaxSize(maxSize).setMaxPixel(if (width >= height) width else height).enableReserveRaw(false).create()
        takePhoto.onEnableCompress(config, true)
    }

    override fun takeSuccess(result: TResult) {
        super.takeSuccess(result)
        showImg(result.images)
    }

    private fun showImg(images: ArrayList<TImage>) {
        if (images.isNotEmpty()) {
            image.setImageURI(null)
            imageResult = images[0]
            image.setImageURI(Uri.fromFile(File(imageResult!!.compressPath)))
        }

    }


}
