package com.djj.xd365.activity

import android.app.Activity
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.alipay.sdk.app.EnvUtils
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.utils.CoordinateConverter
import com.djj.xd365.R
import com.djj.xd365.db.MapCar
import com.djj.xd365.global.XD365Application
import com.djj.xd365.global.XD365http
import com.djj.xd365.util.PayWayDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jpay.JPay
import com.koushikdutta.async.future.FutureCallback
import kotlinx.android.synthetic.main.activity_car_position.*
import java.util.*


class TouristCarPositionActivity : AppCompatActivity(), SensorEventListener {
    lateinit var mBaiduMap: BaiduMap
    private lateinit var xD365Application: XD365Application

    // 定位相关
    private lateinit var mLocClient: LocationClient
    private var myListener = MyLocationListenner()
    private var accelerometerValues = FloatArray(3)
    private var magneticFieldValues = FloatArray(3)
    private lateinit var mSensorManager: SensorManager
    private var lastX: Float = 0.toFloat()
    private var mCurrentDirection: Float = 0.toFloat()
    private var mCurrentLat = 0.0
    private var mCurrentLon = 0.0
    private var mCurrentAccracy: Float = 0.toFloat()


    var isFirstLoc = true // 是否首次定位
    private var locData: MyLocationData? = null

    private var timer: Timer? = null
    lateinit var carIcon: BitmapDescriptor
    private var carlist = mutableListOf<MapCar>()
    private var paycar: MapCar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_position)
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX)
        xD365Application = application as XD365Application
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager //获取传感器管理服务
        mBaiduMap = mmap.map
        carIcon = BitmapDescriptorFactory.fromResource(R.drawable.car)
        search_car.setOnClickListener {
            startActivityForResult(Intent(this@TouristCarPositionActivity, CarCodeActivity::class.java), 1)
        }
        val carcode=xD365Application.constant.tourist!!.code
        if (!carcode.isNullOrEmpty()){
            val mapCar = MapCar(carcode!!, null, null, false)
            carlist.add(mapCar)
            startCarPosition()
        }
        showMyPosition()

    }

    private fun showMyPosition() {
        // 开启定位图层
        mBaiduMap.isMyLocationEnabled = true
        // 定位初始化
        mLocClient = LocationClient(this)
        mLocClient.registerLocationListener(myListener)
        val option = LocationClientOption()
        option.isOpenGps = true // 打开gps
        option.setCoorType("bd09ll") // 设置坐标类型
        option.setScanSpan(1000)
        mLocClient.locOption = option
        mLocClient.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mmap.onDestroy()
        // 退出时销毁定位
        mLocClient.stop()
        // 关闭定位图层
        mBaiduMap.isMyLocationEnabled = false
        carIcon.recycle()
    }

    override fun onStop() {
        super.onStop()
        timer?.cancel()
        //取消注册传感器监听
        mSensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()

        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mmap.onResume()
        //为系统的方向传感器注册监听器
        val sensora = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val sensorb = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        mSensorManager.registerListener(this, sensora, SensorManager.SENSOR_DELAY_UI)
        mSensorManager.registerListener(this, sensorb, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mmap.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val code = data.getStringExtra("carcode")
                val mapCar = MapCar(code, null, null, false)
                carlist.add(mapCar)
                startCarPosition()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            val codeArray = Array(carlist.size) { i -> carlist[i].carcode }
            val chargeArray = BooleanArray(carlist.size) { i -> carlist[i].charge }
            outState.putStringArray("carlist", codeArray)
            outState.putBooleanArray("chargelist", chargeArray)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            val codelist = savedInstanceState.getStringArray("carlist")
            val changelist = savedInstanceState.getBooleanArray("chargelist")
            for (index in codelist.indices) {
                carlist.clear()
                carlist.add(MapCar(codelist[index], null, null, changelist[index]))
            }

        }
    }

    /**
     * 定位SDK监听函数
     */
    inner class MyLocationListenner : BDAbstractLocationListener() {

        override fun onReceiveLocation(location: BDLocation?) {
            // map view 销毁后不在处理新接收的位置
            if (location == null) {
                return
            }
            mCurrentLat = location.latitude
            mCurrentLon = location.longitude
            mCurrentAccracy = location.radius
            locData = MyLocationData.Builder()
                    .accuracy(location.radius)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.latitude)
                    .longitude(location.longitude).build()
            mBaiduMap.setMyLocationData(locData)
            if (isFirstLoc) {
                isFirstLoc = false
                val ll = LatLng(location.latitude,
                        location.longitude)
                val builder = MapStatus.Builder()
                builder.target(ll).zoom(18.0f)
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))
            }
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (event != null) {
            if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD)
                magneticFieldValues = event.values
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER)
                accelerometerValues = event.values
            calculateOrientation()
        }
    }

    private fun calculateOrientation() {
        val values = FloatArray(3)
        val Rarray = FloatArray(9)
        SensorManager.getRotationMatrix(Rarray, null, accelerometerValues, magneticFieldValues)
        SensorManager.getOrientation(Rarray, values)

        // 要经过一次数据格式的转换，转换为度
        values[0] = Math.toDegrees(values[0].toDouble()).toFloat()
        //values[1] = (float) Math.toDegrees(values[1]);
        //values[2] = (float) Math.toDegrees(values[2]);

        val x = if (values[0] >= 0) {
            values[0]
        } else {
            values[0] + 360
        }
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = x
            locData = MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build()
            mBaiduMap.setMyLocationData(locData)
        }
        lastX = x
    }


    /**
     * 开始获取车辆位置,判断是否收费,并启动定时器
     */
    private fun startCarPosition() {
        isCharge()
    }

    /**
     * 判断是否需要收费
     */
    private fun isCharge() {
        var resultCar: MapCar? = null
        for (car in carlist) {
            if (!car.charge) {
                resultCar = car
                break
            }
        }
        if (resultCar != null) {
            paycar = resultCar
            initDialog()
//            XD365http.getCarPosition(this@TouristCarPositionActivity, resultCar.carcode, FutureCallback { e, result ->
//                if (e == null && result != null) {
//                    try {
//                        if (result.contains("未知异常，请联系管理员")) {
//
//                        } else {
//                            val real_result = Gson().fromJson<JsonObject>(result.substring(1..result.length - 2).replace("\\", ""), JsonObject::class.java)
//                            val lng = real_result.get("x").asDouble
//                            val lat = real_result.get("y").asDouble
//                            val latLng = CoordinateConverter().from(CoordinateConverter.CoordType.GPS).coord(LatLng(lat, lng)).convert()
//                        }
//
//                        MaterialDialog.Builder(this@TouristCarPositionActivity)
//                                .title("提示")
//                                .content("查询其他车辆位置为收费功能,金额为1元/次.确定支付吗?")
//                                .negativeText(R.string.cancel)
//                                .positiveText(R.string.sure)
//                                .onPositive { _, _ ->
//                                    //                                    payMoney()
////                                    resultCar.charge = true
////                                    isCharge()
//                                    paycar = resultCar
//                                    initDialog()
//                                }
//                                .onNegative { _, _ ->
//                                    carlist.remove(resultCar)
//                                    isCharge()
//                                }
//                                .show()
//                    } catch (e: Exception) {
//                        carlist.remove(resultCar)
//                        Toast.makeText(this@TouristCarPositionActivity, "查询车辆位置失败.", Toast.LENGTH_SHORT).show()
//                        isCharge()
//                    }
//
//                } else {
//                    carlist.remove(resultCar)
//                    Toast.makeText(this@TouristCarPositionActivity, "查询车辆位置失败.", Toast.LENGTH_SHORT).show()
//                    isCharge()
//                }
//            })
        } else {
            startTimer()
        }
    }

    /**
     * 初始化支付方式Dialog
     */
    fun initDialog() {
        // 隐藏输入法
        //SoftInputUtils.hideSoftInput(mActivity);
        val dialog = PayWayDialog(this, R.style.recharge_pay_dialog, object : PayWayDialog.OnPayWayDialogPositive {
            override fun onPositive(mode: JPay.PayMode) {
                payMoney(mode)
            }

        })
        dialog.show()
        dialog.setRechargeNum(1.0)
    }


    private fun payMoney(mode: JPay.PayMode) {
        val listener = object : JPay.JPayListener {
            override fun onUUPay(p0: String?, p1: String?, p2: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onPaySuccess() {
                Toast.makeText(this@TouristCarPositionActivity, "支付成功", Toast.LENGTH_SHORT).show()
                if (paycar != null) {
                    paycar!!.charge = true
                    isCharge()
                }
            }

            override fun onPayError(error_code: Int, message: String) {
                Toast.makeText(this@TouristCarPositionActivity, "支付失败>" + error_code + " " + message, Toast.LENGTH_SHORT).show();
                if (paycar != null) {
                    carlist.remove(paycar!!)
                    isCharge()
                }
            }

            override fun onPayCancel() {
                Toast.makeText(this@TouristCarPositionActivity, "取消了支付", Toast.LENGTH_SHORT).show()
                if (paycar != null) {
                    carlist.remove(paycar!!)
                    isCharge()
                }
            }
        }
        when (mode) {
            JPay.PayMode.ALIPAY -> {
                //alipay_sdk=alipay-sdk-java-3.3.4.ALL&app_id=2016091600526658&biz_content=%7B%22body%22%3A%22%E4%BF%A1%E8%BE%BE%E5%AE%9D%E9%A9%BE++-++%E8%BD%A6%E8%BE%86%E4%BD%8D%E7%BD%AE%E6%9F%A5%E8%AF%A2%22%2C%22out_trade_no%22%3A%22072514114615324%22%2C%22passback_params%22%3A%22callback+params%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%22%E4%BF%A1%E8%BE%BE%E5%AE%9D%E9%A9%BE%22%2C%22timeout_express%22%3A%2230m%22%2C%22total_amount%22%3A%220.01%22%7D&charset=UTF-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2F192.168.0.80%3A8443%2Fxd365%2Falipay%2Fnotify_url.action&sign=A2OqTBlYL95kapiYAmH7s%2FKqy340qksGsGA6WYzVkP8MLmXhY%2FocI8t8XCDKqxpti4%2FWO1O3K5XBVkRnrqMYRPc%2FFSpLhGIMzwHiRSIGCBVmUr2i5GN4UbnDdDjT8HorC7PzCH%2Br%2BLadG9UJU55IONvnKeGg%2FvOt%2Btnl01thq6ouImkuPYUvZYbwspBNCeIneQe0jT3BckDy%2BI%2B2AvdTc4Ap0bW2SmxbF7cyTMaCoS5Dbv5exG3LrWeoz7xZKVBvPwKM08u0RFEzFXIz5DdUfgZgXAzXlxdSAAGSbJMmtdyxNQltnSaZjsZwVk1VmtftXcJS0MTLFbXfqe%2BjvDF8ig%3D%3D&sign_type=RSA2&timestamp=2018-07-25+14%3A12%3A03&version=1.0
                //alipay_sdk=alipay-sdk-php-20161101&app_id=2016072800109035&biz_content=%7B%22out_trade_no%22%3A%22201712290251256987%22%2C%22total_amount%22%3A0.01%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%22app%E6%B5%8B%E8%AF%95%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay&notify_url=https%3A%2F%2Fwww.alipay.com&sign_type=RSA2&timestamp=2017-12-29+02%3A51%3A25&version=1.0&sign=HaD12RXDt8rubSpyHaN4n3KeJh3oh1KwQyQSzVg3XJGrtyvA%2BcAZVtJhyJsYwxMj17%2FNwBmF9QGOYiOQt%2FY%2FWvnt%2FwTCJ7wYByLvcxkQjaQR3dThEN3LXLzp%2FZESRPgVCIywkn%2Bk0Os6or2xG8uWljPRZekDxHGaz0ADqug9hJqxnovV1s8R%2BVf5T16DiJf8YSC%2BnXd%2FJxkWB%2Bbm8oJT1UVs5QMdw5e3LoqiQQJNYIHZbwdbkkFc41v1URwaLsdtgjZ6dH11DOE0fJsFMfAo3FbYoOuyG%2F4ZM1zqVpwWkPP0D4SC7NjIIlyv5LpsObV70nceyASd7w7G9mgjo6J3pQ%3D%3D
                XD365http.getAliPayOrder_CarPosition(this@TouristCarPositionActivity, FutureCallback { e, result ->
                    if (e == null && result != null) {
                        val code = result.get("code").asInt
                        if (code == 0) {
                            val orderInfo = result.get("data").asString
                            JPay.getIntance(this@TouristCarPositionActivity).toPay(JPay.PayMode.ALIPAY, orderInfo, listener)
                        } else {
                            Toast.makeText(this@TouristCarPositionActivity, "生成订单错误", Toast.LENGTH_SHORT).show()
                            if (paycar != null) {
                                carlist.remove(paycar!!)
                                isCharge()
                            }
                        }
                    }
                })
            }
            JPay.PayMode.WXPAY -> {
                XD365http.getWxPayOrder_CarPosition(this@TouristCarPositionActivity, FutureCallback { e, result ->
                    if (e == null && result != null) {
                        val code = result.get("code").asInt
                        if (code == 0) {
                            val payParameters = result.get("data").asString
                            //val payParameters = Gson().toJson(result)
                            JPay.getIntance(this@TouristCarPositionActivity).toPay(JPay.PayMode.WXPAY, payParameters, listener)
                        } else {
                            Toast.makeText(this@TouristCarPositionActivity, "生成订单错误", Toast.LENGTH_SHORT).show()
                            if (paycar != null) {
                                carlist.remove(paycar!!)
                                isCharge()
                            }
                        }

                    }
                })
            }
            else -> {
            }
        }


    }

    /**
     * 开始计时
     */
    private fun startTimer() {
        if (timer != null) {
            timer?.cancel()
        }
        timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                getCarPostion()
            }
        }
        timer?.schedule(timerTask, 0, 10 * 1000L)
    }

    /**
     * 批量创建位置查询请求,并更新地图
     */
    private fun getCarPostion() {
        for (mapcar in carlist) {
            CarPositionTask(mapcar).run()
        }
    }

    inner class CarPositionTask(private val mapCar: MapCar) {

        fun run() {
            XD365http.getCarPosition(this@TouristCarPositionActivity, mapCar.carcode, FutureCallback { e, result ->
                if (e == null && result != null) {
                    try {
                        var latLng: LatLng
                        if (result.contains("未知异常，请联系管理员")) {
                            val lng = 116.39622
                            val lat = 39.926224
                            latLng = CoordinateConverter().from(CoordinateConverter.CoordType.GPS).coord(LatLng(lat, lng)).convert()
                        } else {
                            val real_result = Gson().fromJson<JsonObject>(result.substring(1..result.length - 2).replace("\\", ""), JsonObject::class.java)
                            val lng = real_result.get("x").asDouble
                            val lat = real_result.get("y").asDouble
                            latLng = CoordinateConverter().from(CoordinateConverter.CoordType.GPS).coord(LatLng(lat, lng)).convert()
                        }

                        runOnUiThread {
                            showCarPosition(mapCar, latLng)
                        }
                    } catch (e: Exception) {

                    }

                }
            })
        }

        private fun showCarPosition(mapCar: MapCar, position: LatLng) {
            if (mapCar.markOverlay != null) {
                mapCar.markOverlay!!.remove()
            }
            if (mapCar.textOverlay != null) {
                mapCar.textOverlay!!.remove()
            }
            val markerOptions = MarkerOptions()
            markerOptions.icon(carIcon)
            markerOptions.title(mapCar.carcode)
            markerOptions.position(position)
            mapCar.markOverlay = mBaiduMap.addOverlay(markerOptions)
            val textOptions = TextOptions()
            textOptions.text(mapCar.carcode)
            textOptions.position(position)
            textOptions.bgColor(0xFF3F51B5.toInt())
            textOptions.fontColor(0xFFFFFFFF.toInt())
            textOptions.fontSize(50)
            mapCar.textOverlay = mBaiduMap.addOverlay(textOptions)
        }
    }
}
