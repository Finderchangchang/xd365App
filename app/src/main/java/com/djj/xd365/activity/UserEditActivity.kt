package com.djj.xd365.activity

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import com.djj.xd365.R
import com.djj.xd365.global.XD365Application
import com.djj.xd365.global.XD365http
import com.koushikdutta.async.future.FutureCallback
import kotlinx.android.synthetic.main.activity_user_edit.*
import org.devio.takephoto.app.TakePhoto
import org.devio.takephoto.compress.CompressConfig
import org.devio.takephoto.model.TImage
import org.devio.takephoto.model.TResult
import org.devio.takephoto.model.TakePhotoOptions
import java.io.File

class UserEditActivity : TakePhotoAppCamptActivity() {

    lateinit var preferences: SharedPreferences
    lateinit var xD365Application:XD365Application
    var my_photoResult: TImage?=null
    var driver_cardResult: TImage?=null
    var qualification_cardResult: TImage?=null
    var id=-1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_edit)
        xD365Application = application as XD365Application
        preferences = getSharedPreferences(xD365Application.constant.APP_CONFIG, Context.MODE_PRIVATE)
        initUser()
        initView()
    }

    private fun initView() {
        my_photo.setOnClickListener {
            id=R.id.my_photo
            configCompress(takePhoto)
            configTakePhotoOption(takePhoto)
            takePhoto.onPickFromCapture(makeImageUri())
        }
        driver_card.setOnClickListener {
            id=R.id.driver_card
            configCompress(takePhoto)
            configTakePhotoOption(takePhoto)
            takePhoto.onPickFromCapture(makeImageUri())
        }
        qualification_card.setOnClickListener {
            id=R.id.qualification_card
            configCompress(takePhoto)
            configTakePhotoOption(takePhoto)
            takePhoto.onPickFromCapture(makeImageUri())
        }
        submit.setOnClickListener {
            val nameStr=name.text.toString()
            if (nameStr.isEmpty()){
                Toast.makeText(this@UserEditActivity,"请输入姓名",Toast.LENGTH_SHORT).show()
                name.requestFocus()
                return@setOnClickListener
            }
            val phoneStr=phone.text.toString()
            val path=my_photoResult?.compressPath
            val driver_path=driver_cardResult?.compressPath
            val qualification_path=qualification_cardResult?.compressPath
            XD365http.updateDriverInfo(this@UserEditActivity,xD365Application.constant.driver!!.id,nameStr,phoneStr,path,driver_path,qualification_path, FutureCallback { e, result->
                if (e==null&&result!=null){
                    Toast.makeText(this@UserEditActivity,"用户信息保存成功",Toast.LENGTH_SHORT).show()
                    xD365Application.constant.driver=xD365Application.constant.driver!!.copy(name = result.name,phone=result.phone,hardphoto= result.hardphoto,carphoto= result.carphoto,peoplezgz=result.peoplezgz)
                    finish()
                }else{
                    Toast.makeText(this@UserEditActivity,"用户信息保存失败",Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun initUser(){
        val driver=xD365Application.constant.driver
        if (driver!=null){
            name.setText(driver.name)
            phone.setText(driver.phone)
            XD365http.loadImage(my_photo,android.R.drawable.ic_menu_add,xD365Application.constant.driver?.hardphoto)
            XD365http.loadImage(driver_card,android.R.drawable.ic_menu_add,xD365Application.constant.driver?.carphoto)
            XD365http.loadImage(qualification_card,android.R.drawable.ic_menu_add,xD365Application.constant.driver?.peoplezgz)
        }
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

    override fun takeSuccess(result: TResult) {
        super.takeSuccess(result)
        showImg(result.images)
    }

    private fun showImg(images: ArrayList<TImage>) {
        if (images.isNotEmpty()) {
            when(id){
                R.id.my_photo->{
                    my_photo.setImageURI(null)
                    val imageResult = images[0]
                    my_photo.setImageURI(Uri.fromFile(File(imageResult.compressPath)))
                    my_photoResult=imageResult
                }
                R.id.driver_card->{
                    driver_card.setImageURI(null)
                    val imageResult = images[0]
                    driver_card.setImageURI(Uri.fromFile(File(imageResult.compressPath)))
                    driver_cardResult=imageResult
                }
                R.id.qualification_card->{
                    qualification_card.setImageURI(null)
                    val imageResult = images[0]
                    qualification_card.setImageURI(Uri.fromFile(File(imageResult.compressPath)))
                    qualification_cardResult=imageResult
                }
            }
        }

    }
}
