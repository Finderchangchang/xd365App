package com.djj.xd365.activity

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.baidu.location.BDLocation
import com.baidu.location.BDLocationListener
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.MapStatus
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MyLocationData
import com.baidu.mapapi.model.LatLng
import com.djj.xd365.R
import com.djj.xd365.global.XD365Application
import kotlinx.android.synthetic.main.activity_road_info.*

class RoadInfoActivity : AppCompatActivity(), SensorEventListener {
    lateinit var mBaiduMap: BaiduMap
    lateinit var xD365Application: XD365Application

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_road_info)
        xD365Application = application as XD365Application
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager //获取传感器管理服务
        mBaiduMap = mmap.map
        mBaiduMap.isTrafficEnabled=true
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
    }

    override fun onStop() {
        super.onStop()
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


    /**
     * 定位SDK监听函数
     */
    inner class MyLocationListenner : BDLocationListener {

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
        val R = FloatArray(9)
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues)
        SensorManager.getOrientation(R, values)

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
}
