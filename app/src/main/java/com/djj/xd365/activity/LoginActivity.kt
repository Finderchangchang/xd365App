package com.djj.xd365.activity

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.djj.xd365.R
import com.djj.xd365.db.Driver
import com.djj.xd365.db.Tourist
import com.djj.xd365.global.XD365Application
import com.djj.xd365.global.XD365http
import com.koushikdutta.async.future.FutureCallback
import kotlinx.android.synthetic.main.activity_login.*
import org.devio.takephoto.app.TakePhoto
import org.devio.takephoto.compress.CompressConfig
import org.devio.takephoto.model.TImage
import org.devio.takephoto.model.TResult
import org.devio.takephoto.model.TakePhotoOptions
import java.io.File
import java.lang.Exception


/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : TakePhotoAppCamptActivity() {
    lateinit var xD365Application: XD365Application
    lateinit var preferences: SharedPreferences
    var imageUri: Uri = makeImageUri()
    var driverid=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        xD365Application = application as XD365Application
        preferences = getSharedPreferences(xD365Application.constant.APP_CONFIG, Context.MODE_PRIVATE)
        account.text = SpannableStringBuilder(preferences.getString(xD365Application.constant.KEY_ACCOUNT, ""))
        password.text = SpannableStringBuilder(preferences.getString(xD365Application.constant.KEY_PASSWORD, ""))
        if (!account.text.isNullOrEmpty()) {
            password.requestFocus()
        }
        if (!account.text.isNullOrEmpty() && !password.text.isNullOrEmpty()) {
            attemptLogin()
        }
        // Set up the login form.
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        sign_in.setOnClickListener { attemptLogin() }
        sign_up.setOnClickListener { startActivity(Intent(this@LoginActivity, SingUpActivity::class.java)) }
        requestPower()
    }

    fun requestPower() {
        //判断是否已经赋予权限
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                            Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {//这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限
//            } else {
//                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
//                ActivityCompat.requestPermissions(this,
//                        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION), 1)
//            }
            //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION), 1)

        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {

        // Reset errors.
        account.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val accountStr = account.text.toString()
        val passwordStr = password.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(passwordStr)) {
            password.error = getString(R.string.error_field_required)
            focusView = password
            cancel = true
        }

        // Check for a valid account address.
        if (TextUtils.isEmpty(accountStr)) {
            account.error = getString(R.string.error_field_required)
            focusView = account
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
            XD365http.login(this, accountStr, passwordStr, object : FutureCallback<Driver> {
                override fun onCompleted(e: Exception?, result: Driver?) {
                    if (e != null) {
                        showProgress(false)
                        password.error = getString(R.string.error_incorrect_password)
                        password.requestFocus()
                        return
                    }
                    if (result != null) {
                        showProgress(false)
                        if (result.id == "-1") {
                            account.error = getString(R.string.error_account_checked)
                            account.requestFocus()
                        } else if(result.hardphoto.isNullOrEmpty()){
                            driverid=result.id
                            MaterialDialog.Builder(this@LoginActivity)
                            .title("提示")
                            .content("您还未设置头像，确认设置头像吗?")
                            .positiveText(R.string.sure)
                            .negativeText(R.string.cancel)
                            .onPositive { _, _ ->
                                configCompress(takePhoto)
                                configTakePhotoOption(takePhoto)
                                takePhoto.onPickFromCapture(imageUri)
                            }
                            .onNegative { _, _ ->

                            }
                            .show()
                        }else{
                            xD365Application.constant.driver = result
                            val editor = preferences.edit()
                            editor.putString(xD365Application.constant.KEY_ACCOUNT, result.account)
                            editor.putString(xD365Application.constant.KEY_PASSWORD, result.password)
                            editor.apply()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    } else {
                        touristLogin(accountStr, passwordStr)
                    }
                }


            })
        }
    }

    private fun touristLogin(accountStr: String, passwordStr: String) {
        XD365http.touristLogin(this, accountStr, passwordStr, object : FutureCallback<Tourist> {
            override fun onCompleted(e: Exception?, result: Tourist?) {
                showProgress(false)
                if (e == null && result != null) {
                    xD365Application.constant.tourist = result
                    val editor = preferences.edit()
                    editor.putString(xD365Application.constant.KEY_ACCOUNT, result.account)
                    editor.putString(xD365Application.constant.KEY_PASSWORD, result.password)
                    editor.apply()
                    startActivity(Intent(this@LoginActivity, TouristMainActivity::class.java))
                    finish()
                } else {
                    password.error = getString(R.string.error_incorrect_password)
                    password.requestFocus()
                    return
                }
            }


        })
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
    private fun makeImageUri(): Uri {
        val file = File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg")
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        return Uri.fromFile(file)
    }
    override fun takeSuccess(result: TResult) {
        super.takeSuccess(result)
        showImg(result.images)
    }

    private fun showImg(images: ArrayList<TImage>) {
        if (images.isNotEmpty()) {
            val imageResult = images[0]
            XD365http.updateDriverImage(this@LoginActivity,driverid,imageResult.compressPath, FutureCallback { e, result ->
                if (e==null&&result!=null){
                    Toast.makeText(this, "头像设置成功，请重新登录", Toast.LENGTH_SHORT).show()
                }
            })
        }

    }
}
