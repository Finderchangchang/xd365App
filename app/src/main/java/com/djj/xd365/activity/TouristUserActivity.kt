package com.djj.xd365.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.djj.xd365.R
import com.djj.xd365.global.XD365Application
import com.djj.xd365.global.XD365http
import com.koushikdutta.async.future.FutureCallback
import kotlinx.android.synthetic.main.activity_tourist_user.*

class TouristUserActivity : AppCompatActivity() {

    lateinit var preferences: SharedPreferences
    lateinit var xD365Application:XD365Application
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tourist_user)
        xD365Application=application as XD365Application
        preferences = getSharedPreferences(xD365Application.constant.APP_CONFIG, Context.MODE_PRIVATE)
        name.text = xD365Application.constant.tourist?.name
        account.text = xD365Application.constant.tourist?.account
        sign_out.setOnClickListener {
            MaterialDialog.Builder(this@TouristUserActivity)
                    .title("提示")
                    .content("确定要注销当前用户吗?")
                    .negativeText(R.string.cancel)
                    .positiveText(R.string.sure)
                    .onPositive { _, _ ->
                        val editer=preferences.edit()
                        editer.remove(xD365Application.constant.KEY_PASSWORD)
                        editer.apply()
                        val intent=Intent(this@TouristUserActivity,LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    .onNegative { _, _ ->

                    }
                    .show()

        }
        user.setOnClickListener {
            MaterialDialog.Builder(this)
                    .content("用户名")
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input(null,null) { _, _ ->}
                    .positiveText(R.string.sure)
                    .onPositive { dialog, which ->
                        val newname=dialog.inputEditText?.text.toString()
                        if (newname.isEmpty()){

                        }else{
                            XD365http.updateTouristName(this@TouristUserActivity,xD365Application.constant.tourist!!.id,newname, FutureCallback { e, result ->
                                if (e==null&&result!=null){
                                    val resultStr = result.get("result").asString
                                    if (resultStr == "OK") {
                                        Toast.makeText(this@TouristUserActivity,"更新成功。",Toast.LENGTH_SHORT).show()
                                        name.text=newname
                                        val tourist=xD365Application.constant.tourist
                                        if(tourist!=null){
                                            val newtourist=tourist.copy(name = newname)
                                            xD365Application.constant.tourist=newtourist
                                        }
                                    }
                                }
                            })
                        }

                    }
                    .show()
        }
        car.setOnClickListener {
            val intent=Intent(this@TouristUserActivity,CarAddActivity::class.java)
            startActivityForResult(intent,1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode==1&&resultCode== Activity.RESULT_OK){
            if (data!=null){
                val codeStr=data.getStringExtra("code")
                val frame_numStr=data.getStringExtra("frame")
                val engin_numStr=data.getStringExtra("engine")
                car_code.text=codeStr
                val tourist=xD365Application.constant.tourist
                if(tourist!=null){
                    val newtourist=tourist.copy(code = codeStr,frame_num = frame_numStr,engin_num = engin_numStr)
                    xD365Application.constant.tourist=newtourist
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
