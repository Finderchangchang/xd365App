package com.djj.xd365.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.djj.xd365.R
import com.djj.xd365.db.CarCheck
import com.djj.xd365.global.XD365Application
import com.djj.xd365.global.XD365http
import com.djj.xd365.util.DialogUtil
import com.koushikdutta.async.future.FutureCallback
import kotlinx.android.synthetic.main.activity_car_check_add_main.*
import java.text.SimpleDateFormat
import java.util.*

class CarCheckAddMainActivity : AppCompatActivity() {

    lateinit var myApplication: XD365Application
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myApplication=application as XD365Application
        setContentView(R.layout.activity_car_check_add_main)
        start_time.inputType = InputType.TYPE_NULL
        end_time.inputType = InputType.TYPE_NULL
        start_time.onFocusChangeListener = View.OnFocusChangeListener {view, hasFocus ->
            if (hasFocus) {
                DialogUtil.setDateDialog(this@CarCheckAddMainActivity,start_time)
            }
        }
        start_time.setOnClickListener { DialogUtil.setDateDialog(this@CarCheckAddMainActivity,start_time) }
        end_time.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                DialogUtil.setDateDialog(this@CarCheckAddMainActivity,end_time)
            }
        }
        end_time.setOnClickListener { DialogUtil.setDateDialog(this@CarCheckAddMainActivity,end_time) }
        submit.setOnClickListener {
            val stypeV=if (ptype.isChecked){"0"}else{"1"}
            val weatherV=weather.text.toString()
            val weightV=weight.text.toString()
            val sdf=SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
            val start_timeV=start_time.text.toString()
            val start_timeL=if (start_timeV.isNotEmpty()){
                sdf.parse(start_timeV).time
            }else{0}


            val end_timeV=end_time.text.toString()
            val end_timeL=if (end_timeV.isNotEmpty()){
                sdf.parse(end_timeV).time
            }else{0}
            val start_positionV=start_position.text.toString()
            val end_positonV=end_positon.text.toString()
            val middle_positionV=middle_position.text.toString()
            val distanceV=distance.text.toString()
            val otherV=other.text.toString()
            val creatime=System.currentTimeMillis()
            val car_check=CarCheck(
                    "",
                    "",
                    myApplication.constant.driver!!.id,
                    myApplication.constant.driver!!.carid,
                    creatime,
                    stypeV,creatime,weatherV,"",weightV,start_timeL,start_positionV,end_positonV,middle_positionV,end_timeL,distanceV,otherV,"0","0","0"
                    )
            XD365http.uploadCarCheck(this,car_check, FutureCallback { e, result ->
                if (e==null&&result!=null){
                    val re=result["success"].asBoolean
                    if (re){
                        Toast.makeText(this@CarCheckAddMainActivity,"保存成功",Toast.LENGTH_SHORT).show()
                        finish()
                    }else{
                        Toast.makeText(this@CarCheckAddMainActivity,"保存失败",Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this@CarCheckAddMainActivity,"保存失败",Toast.LENGTH_SHORT).show()
                }

            })
        }
    }
}
