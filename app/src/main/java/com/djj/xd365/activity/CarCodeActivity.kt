package com.djj.xd365.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.djj.xd365.R
import com.parkingwang.keyboard.KeyboardInputController
import com.parkingwang.keyboard.OnInputChangedListener
import com.parkingwang.keyboard.PopupKeyboard
import com.parkingwang.keyboard.engine.KeyboardEntry
import com.parkingwang.keyboard.view.OnKeyboardChangedListener
import kotlinx.android.synthetic.main.activity_car_code.*


class CarCodeActivity : AppCompatActivity() {

    private var mPopupKeyboard: PopupKeyboard? = null

    private var mHideOKKey = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_code)

        // 创建弹出键盘
        mPopupKeyboard = PopupKeyboard(this)
        // 弹出键盘内部包含一个KeyboardView，在此绑定输入两者关联。
        mPopupKeyboard!!.attach(input_view, this)

        // 隐藏确定按钮
        mPopupKeyboard!!.keyboardEngine.setHideOKKey(mHideOKKey)

        // KeyboardInputController提供一个默认实现的新能源车牌锁定按钮
        mPopupKeyboard!!.controller
                .setDebugEnabled(false)
                .bindLockTypeProxy(object : KeyboardInputController.ButtonProxyImpl(lock_type) {
                    override fun onNumberTypeChanged(isNewEnergyType: Boolean) {
                        super.onNumberTypeChanged(isNewEnergyType)
                        if (isNewEnergyType) {
                            lock_type.setTextColor(resources.getColor(android.R.color.holo_green_light))
                        } else {
                            lock_type.setTextColor(resources.getColor(android.R.color.black))
                        }
                    }
                })

                .addOnInputChangedListener(object : OnInputChangedListener {
                    override fun onChanged(number: String?, isCompleted: Boolean) {
                        //Log.d("CarCode",number+",isCompleted:"+isCompleted)
                    }

                    override fun onCompleted(number: String?, isAutoCompleted: Boolean) {
                        //Log.d("CarCode",number+",isAutoCompleted:"+isAutoCompleted)
                    }
                })
        mPopupKeyboard!!.keyboardView.addKeyboardChangedListener(object : OnKeyboardChangedListener {
            override fun onConfirmKey() {
                val data = Intent()
                data.putExtra("carcode", input_view.number)
                setResult(Activity.RESULT_OK, data)
                finish()
            }

            override fun onKeyboardChanged(keyboard: KeyboardEntry?) {
                //Log.d("CarCode","onKeyboardChanged")
            }

            override fun onDeleteKey() {
                //Log.d("CarCode","onDeleteKey")
            }

            override fun onTextKey(text: String?) {
                //Log.d("CarCode","onTextKey,"+text)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // 默认选中第一个车牌号码输入框
        input_view!!.performFirstFieldView()
    }

    fun onClick(view: View) {
        val id = view.id
        // 切换键盘类型
        when (id) {
//            R.id.test_number -> {
//                val idx = (mTestIndex % mTestNumber.size).toInt()
//                mTestIndex++
//                // 上面测试例子中，第12个，指定为新能源车牌，部分车牌
//                if (idx == 11) {
//                    mPopupKeyboard!!.controller.updateNumberLockType(mTestNumber.get(idx), true)
//                } else {
//                    mPopupKeyboard!!.controller.updateNumber(mTestNumber.get(idx))
//                }
//            }
            R.id.clear_number -> mPopupKeyboard!!.controller.updateNumber("")
//            R.id.popup_keyboard -> if (mPopupKeyboard!!.isShown) {
//                mPopupKeyboard!!.dismiss(this@CarCodeActivity)
//            } else {
//                mPopupKeyboard!!.show(this@CarCodeActivity)
//            }

//            R.id.hide_ok_key -> {
//                mHideOKKey = !mHideOKKey
//                mPopupKeyboard!!.keyboardEngine.setHideOKKey(mHideOKKey)
//                Toast.makeText(baseContext,
//                        "演示“确定”键盘状态，将在下一个操作中生效: " + if (mHideOKKey) "隐藏" else "显示", Toast.LENGTH_SHORT)
//                        .show()
//            }

//            R.id.commit_province -> {
//                val name = province_value!!.text.toString()
//                mPopupKeyboard!!.keyboardEngine.setLocalProvinceName(name)
//                Toast.makeText(baseContext,
//                        "演示“周边省份”重新排序，将在下一个操作中生效：$name", Toast.LENGTH_SHORT).show()
//            }
        }
    }
}
