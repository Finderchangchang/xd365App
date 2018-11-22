package com.djj.xd365.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.djj.xd365.R
import com.djj.xd365.global.XD365Application
import com.djj.xd365.global.XD365http
import com.koushikdutta.async.future.FutureCallback
import kotlinx.android.synthetic.main.activity_car_add.*

class CarAddActivity : AppCompatActivity() {

    lateinit var xD365Application:XD365Application
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_add)
        xD365Application=application as XD365Application
        sign_up.setOnClickListener {
            binderCar()
        }
    }

    private fun binderCar() {
        var cancel = false
        var focusView: View? = null
        val codeStr = code.text.toString()
        if (codeStr.isEmpty()) {
            code.error = getString(R.string.error_field_required)
            focusView = code
            cancel = true
        }
        val frame_numStr = frame_num.text.toString()
        if (frame_numStr.isEmpty()) {
            frame_num.error = getString(R.string.error_field_required)
            focusView = frame_num
            cancel = true
        }
        val engin_numStr = engin_num.text.toString()
        if (engin_numStr.isEmpty()) {
            engin_num.error = getString(R.string.error_field_required)
            focusView = engin_num
            cancel = true
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
            XD365http.updateTouristCar(this@CarAddActivity,xD365Application.constant.tourist!!.id,codeStr,frame_numStr,engin_numStr, FutureCallback { e, result ->
                showProgress(false)
                if (e==null&&result!=null){
                    val resultStr=result.get("result").asString
                    if (resultStr!="OK"){
                        code.error = resultStr
                        code.requestFocus()
                    }else{
                        Toast.makeText(this@CarAddActivity,"注册成功.", Toast.LENGTH_SHORT).show()
                        val data = Intent()
                        data.putExtra("code",codeStr)
                        data.putExtra("frame",frame_numStr)
                        data.putExtra("engine",engin_numStr)
                        setResult(Activity.RESULT_OK,data)
                        finish()
                    }
                }
            })
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
            login_form.visibility = if (show) View.GONE else View.VISIBLE
            login_form.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 0 else 1).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_form.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_progress.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_form.visibility = if (show) View.GONE else View.VISIBLE
        }
    }
}
