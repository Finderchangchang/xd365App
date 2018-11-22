package com.djj.xd365.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.GridLayoutManager
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.djj.xd365.R
import com.djj.xd365.adapter.CheckImageAdapter
import com.djj.xd365.adapter.onTImageItemClickListener
import com.djj.xd365.db.CarCheck
import com.djj.xd365.global.Constant
import com.djj.xd365.global.XD365Application
import com.djj.xd365.global.XD365http
import com.koushikdutta.async.future.FutureCallback
import kotlinx.android.synthetic.main.activity_car_check_add.*
import org.devio.takephoto.app.TakePhoto
import org.devio.takephoto.app.TakePhotoActivity
import org.devio.takephoto.compress.CompressConfig
import org.devio.takephoto.model.TImage
import org.devio.takephoto.model.TResult
import org.devio.takephoto.model.TakePhotoOptions
import java.io.File
import com.baidu.location.BDLocation
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.search.core.RouteNode.location
import android.icu.util.ULocale.getCountry
import com.djj.xd365.adapter.CheckMenuImageAdapter
import com.djj.xd365.adapter.onCarCheckMenuItemClickListener
import com.djj.xd365.db.CarCheckMenu


class CarCheckAddActivity : TakePhotoActivity() {

    val MAX_PIC = 9
    var imageUri: Uri? = null
    var position:String=""
    var id =""
    var type=""
    lateinit var constant: Constant
    private lateinit var mLocClient: LocationClient
    private lateinit var checkMenuImageAdapter: CheckMenuImageAdapter
    private var takePhotoMenu:CarCheckMenu?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_check_add)
        constant = (application as XD365Application).constant
        if(intent.getStringExtra("id").isNullOrEmpty()||intent.getStringExtra("type").isNullOrEmpty()){
            finish()
        }else{
            id=intent.getStringExtra("id")
            type= intent.getStringExtra("type")
        }

        mLocClient = LocationClient(getApplicationContext())
        //声明LocationClient类
        mLocClient.registerLocationListener(MyLocationListener())
        val option = LocationClientOption()

        option.setIsNeedAddress(true)
//可选，是否需要地址信息，默认为不需要，即参数为false
//如果开发者需要获得当前点的地址信息，此处必须为true

        mLocClient.setLocOption(option)
//mLocationClient为第二步初始化过的LocationClient对象
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
//更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        mLocClient.start()
        pics.layoutManager = GridLayoutManager(this, 3)
        XD365http.getCarCheckMenu(this@CarCheckAddActivity, FutureCallback { e, result ->
            if(e==null&&result!=null){
                checkMenuImageAdapter = CheckMenuImageAdapter(this@CarCheckAddActivity, result, object : onCarCheckMenuItemClickListener {
                    override fun onClick(data: CarCheckMenu?) {
                        //全屏显示
                        if (data!=null){
                            if (data.url.isNullOrEmpty()){
                                takePhotoMenu=data
                                configCompress(takePhoto)
                                configTakePhotoOption(takePhoto)
                                takePhoto.onPickFromCapture(makeImageUri())
                            }
                        }
                    }

                    override fun onDelClick(data: CarCheckMenu?) {
                        checkMenuImageAdapter.delPhoto(data)
                    }
                })
                pics.adapter=checkMenuImageAdapter
            }
        })

        submit.setOnClickListener {
            val imageData=checkMenuImageAdapter.mData
            XD365http.addCarCheck(this@CarCheckAddActivity,id,type,imageData,position, FutureCallback{e,result->
                if(e==null&&result!=null) {
                    MaterialDialog.Builder(this@CarCheckAddActivity)
                            .title("提示")
                            .content("保存成功。")
                            .positiveText(R.string.sure)
                            .onPositive { _, _ ->
                                finish()
                            }
                            .show()
                }
            })
        }
    }

    private fun makeImageUri(): Uri {
        val file = File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg")
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        return Uri.fromFile(file)
    }

    private fun configCompress(takePhoto: TakePhoto) {
        val maxSize = 102400
        val width = 300
        val height = 400
        val config = CompressConfig.Builder().setMaxSize(maxSize).setMaxPixel(if (width >= height) width else height).enableReserveRaw(false).create()
        takePhoto.onEnableCompress(config, true)
    }

    private fun configTakePhotoOption(takePhoto: TakePhoto) {
        val builder = TakePhotoOptions.Builder().setWithOwnGallery(false).setCorrectImage(false).create()
        takePhoto.setTakePhotoOptions(builder)
    }

    override fun takeSuccess(result: TResult) {
        super.takeSuccess(result)
        showImg(result.images)
    }

    private fun showImg(images: ArrayList<TImage>) {
        if (images.isNotEmpty()&&takePhotoMenu!=null) {
            checkMenuImageAdapter.onLoadMore(images[0],takePhotoMenu!!)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        // 退出时销毁定位
        mLocClient.stop()
    }

    inner class MyLocationListener : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取位置描述信息相关的结果
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            position = location.addrStr    //获取详细地址信息
//            val country = location.country    //获取国家
//            val province = location.province    //获取省份
//            val city = location.city    //获取城市
//            val district = location.district    //获取区县
//            val street = location.street    //获取街道信息
        }
    }
}
