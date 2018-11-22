package com.djj.xd365.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.djj.xd365.R
import com.djj.xd365.global.XD365http
import com.koushikdutta.async.future.FutureCallback
import kotlinx.android.synthetic.main.activity_sing_up.*

class SingUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)
        sign_up.setOnClickListener {
            signupTourist()
        }
    }

    private fun signupTourist() {
        var cancel = false
        var focusView: View? = null
        val accountStr = account.text.toString()
        if (accountStr.isEmpty()) {
            account.error = getString(R.string.error_field_required)
            focusView = account
            cancel = true
        }
        val pass = password.text.toString()
        if (pass.isEmpty()) {
            password.error = getString(R.string.error_field_required)
            focusView = password
            cancel = true
        }
        val pass_sure = password_sure.text.toString()
        if (pass_sure != pass) {
            password_sure.error = getString(R.string.error_password_sure)
            focusView = password_sure
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
            XD365http.signUpTourist(this@SingUpActivity, accountStr, pass, FutureCallback { e, result ->
                showProgress(false)
                if (e == null && result != null) {
                    val resultStr = result.get("result").asString
                    if (resultStr != "OK") {
                        account.error = resultStr
                        account.requestFocus()
                    } else {
                        Toast.makeText(this@SingUpActivity, "注册成功.", Toast.LENGTH_SHORT).show()
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
