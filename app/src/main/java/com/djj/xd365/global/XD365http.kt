package com.djj.xd365.global

import android.content.Context
import android.widget.ImageView
import com.djj.xd365.R
import com.djj.xd365.db.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.koushikdutta.async.future.FutureCallback
import com.koushikdutta.ion.Ion
import com.koushikdutta.ion.builder.Builders
import com.koushikdutta.ion.builder.LoadBuilder
import org.devio.takephoto.model.TImage
import java.io.File
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Created by liufeng on 2018-06-29.
 * 用于 xd365
 */
object XD365http {
//    val Server_Url = "https://47.95.120.184/"http://xxd.01210aqqcom.yxnat.softdev.top/xd365/
//    val Server_Url = "http://123.123212qwcom.yxnat.softdev.top/xd365/"
    //http://xxd.01210aqqcom.yxnat.softdev.top/xd365/
    val Server_Url = "http://222.222.153.111:7384/xd365/"

    //val Server_Url = "https://192.168.3.10:8443/xd365/"
    val Server_Url2 = "http://edu.xd365.vip/car/controlvehicle/getCarPosition"
    val HTTPS_DEBUG = true
    fun login(context: Context, account: String, password: String, callback: FutureCallback<Driver>) {
        val url = Server_Url + "app/login.action"
//        val json = JsonObject()
//        json.addProperty("account", account)
//        json.addProperty("password", password)
        getIon(context).load(url)
                .setBodyParameter("account", account)
                .setBodyParameter("password", password)
                .`as`(object : TypeToken<Driver>() {}).setCallback(callback)
    }

    fun touristLogin(context: Context, account: String, password: String, callback: FutureCallback<Tourist>) {
        val url = Server_Url + "tourist/touristLogin.action"
//        val json = JsonObject()
//        json.addProperty("account", account)
//        json.addProperty("password", password)
        getIon(context).load(url)
                .setBodyParameter("account", account)
                .setBodyParameter("password", password)
                .`as`(object : TypeToken<Tourist>() {}).setCallback(callback)
    }

    fun updateStudyCount(context: Context, id: String, courseid: String, file_list: String, callback: FutureCallback<StulogCount>) {
        val url = Server_Url + "Driver/updateDrivecount.action"
        getIon(context).load(url)
                .setBodyParameter("id", id)
                .setBodyParameter("courseid", courseid)
                .setBodyParameter("file_list", file_list)
                .`as`(object : TypeToken<StulogCount>() {}).setCallback(callback)
    }

    fun updateStudyLogTime(context: Context, logid: String, file_list: String, callback: FutureCallback<JsonObject>) {
        val url = Server_Url + "Stucourse/updateStulogStarttime.action"
        getIon(context).load(url)
                .setBodyParameter("id", file_list)
                .setBodyParameter("id1", logid)
                .asJsonObject().setCallback(callback)
    }

    fun updateStuLogFileTime(context: Context, logFileid: String, callback: FutureCallback<JsonObject>) {
        val url = Server_Url + "Stulogfile/updateStulogfileBegintime.action"
        getIon(context).load(url)
                .setBodyParameter("id", logFileid)
                .asJsonObject().setCallback(callback)
    }

    fun getCourse(context: Context, id: String, start: Int, limit: Int, callback: FutureCallback<Common<Stucourse>>) {
        val url = Server_Url + "Stucourse/getbqxxStucourse.action"
        getIon(context).load(url)
                .setBodyParameter("id", id)
                .setBodyParameter("start", start.toString())
                .setBodyParameter("limit", limit.toString())
                .`as`(object : TypeToken<Common<Stucourse>>() {}).setCallback(callback)
    }

    fun getZzCourse(context: Context, id: String, start: Int, limit: Int, callback: FutureCallback<Common<Stucourse>>) {
        val url = Server_Url + "Stucourse/getzzxxStucourse.action"
        getIon(context).load(url)
                .setBodyParameter("id", id)
                .setBodyParameter("start", start.toString())
                .setBodyParameter("limit", limit.toString())
                .`as`(object : TypeToken<Common<Stucourse>>() {}).setCallback(callback)
    }

    fun getCourseFile(context: Context, logid: String, file_list: String, start: Int, limit: Int, callback: FutureCallback<Common<StuFile>>) {
        val url = Server_Url + "stufile/stufulebqxxlist.action"
        getIon(context).load(url)
                .setBodyParameter("id", file_list)
                .setBodyParameter("ids", logid)
                .setBodyParameter("start", start.toString())
                .setBodyParameter("limit", limit.toString())
                .`as`(object : TypeToken<Common<StuFile>>() {}).setCallback(callback)
    }

    fun getExam(context: Context, id: String, start: Int, limit: Int, callback: FutureCallback<Common<Exam>>) {
        val url = Server_Url + "Examlog/getExamlogList.action"
        getIon(context).load(url)
                .setBodyParameter("driverid", id)
                .setBodyParameter("start", start.toString())
                .setBodyParameter("limit", limit.toString())
                .`as`(object : TypeToken<Common<Exam>>() {}).setCallback(callback)
    }

    fun getStulog(context: Context, id: String, start: Int, limit: Int, callback: FutureCallback<Common<StudyLog>>) {
        val url = Server_Url + "app/getstulogbydriver.action"
        getIon(context).load(url)
                .setBodyParameter("driverid", id)
                .setBodyParameter("start", start.toString())
                .setBodyParameter("limit", limit.toString())
                .`as`(object : TypeToken<Common<StudyLog>>() {}).setCallback(callback)
    }

    fun getCarCode(context: Context, id: String, callback: FutureCallback<JsonObject>) {
        val url = Server_Url + "app/getcarcode.action"
        getIon(context).load(url)
                .setBodyParameter("carid", id)
                .asJsonObject().setCallback(callback)
    }

    fun getCarPosition(context: Context, carcode: String, callback: FutureCallback<String>) {
        val url = Server_Url2
        getIon(context).load(url)
                .setBodyParameter("carPlateNo", carcode)
                .asString().setCallback(callback)
    }

    fun getMessage(context: Context, driverid: String, start: Int, limit: Int, callback: FutureCallback<Common<ComMessage>>) {
        val url = Server_Url + "Drivermessage/DrivermessageListdri.action"
        getIon(context).load(url)
                .setBodyParameter("id", driverid)
                .setBodyParameter("start", start.toString())
                .setBodyParameter("limit", limit.toString())
                .`as`(object : TypeToken<Common<ComMessage>>() {}).setCallback(callback)
    }

    fun getCarCheck(context: Context, driverid: String, start: Int, limit: Int, callback: FutureCallback<Common<CarCheck>>) {
        val url = Server_Url + "CarCheck/getCarCheck.action"
        getIon(context).load(url)
                .setBodyParameter("driverid", driverid)
                .setBodyParameter("start", start.toString())
                .setBodyParameter("limit", limit.toString())
                .`as`(object : TypeToken<Common<CarCheck>>() {}).setCallback(callback)
    }



    fun getMessageCount(context: Context, dirverid: String, callback: FutureCallback<String>) {
        val url = Server_Url + "message/messagenoreadxy.action"
        getIon(context).load(url)
                .setBodyParameter("driverid", dirverid)
                .asString().setCallback(callback)
    }

    fun updateMessageStatus(context: Context, messageid: String, callback: FutureCallback<JsonObject>) {
        val url = Server_Url + "Drivermessage/updatedriMessage.action"
        getIon(context).load(url)
                .setBodyParameter("id", messageid)
                .asJsonObject().setCallback(callback)
    }

    fun uploadImage(context: Context, path: String, driverid: String, examid: String, callback: FutureCallback<JsonObject>) {
        val url = Server_Url + "Examlog/uploadImage.action"
        val load = getIon(context).load(url)
        load.setMultipartParameter("driverid", driverid)
        load.setMultipartParameter("examid", examid)
        load.setMultipartFile("imagedata", File(path))
        load.asJsonObject().setCallback(callback)
    }

    fun getIon(context: Context): LoadBuilder<Builders.Any.B> {
        return if (HTTPS_DEBUG) {
            try {
                // Create a trust manager that does not validate certificate chains
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }

                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                    }
                })
                // Install the all-trusting trust manager
                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, trustAllCerts, null)
                val i = Ion.getDefault(context)
                i.httpClient.sslSocketMiddleware.setTrustManagers(trustAllCerts)
                i.httpClient.sslSocketMiddleware.setHostnameVerifier { hostname, session -> return@setHostnameVerifier true }
                i.httpClient.sslSocketMiddleware.sslContext = sslContext


                return i.build(context)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        } else {
            Ion.with(context)
        }
    }

    fun getWxPayOrder_CarPosition(context: Context, callback: FutureCallback<JsonObject>) {
        val url = Server_Url + "wxpay/appPay.action"
        getIon(context).load(url).asJsonObject().setCallback(callback)
    }

    fun getAliPayOrder_CarPosition(context: Context, callback: FutureCallback<JsonObject>) {
        val url = Server_Url + "alipay/appPay.action"
        getIon(context).load(url).asJsonObject().setCallback(callback)
    }

    fun updateDriverPosition(context: Context, driverid: String, x: Double, y: Double, callback: FutureCallback<JsonObject>) {
        val url = Server_Url + "app/updateDriverPosition.action"
        getIon(context).load(url)
                .setBodyParameter("driverid", driverid)
                .setBodyParameter("x", x.toString())
                .setBodyParameter("y", y.toString())
                .asJsonObject().setCallback(callback)
    }

    fun updateTouristPosition(context: Context, touristid: String, x: Double, y: Double, callback: FutureCallback<JsonObject>) {
        val url = Server_Url + "tourist/updateTouristPosition.action"
        getIon(context).load(url)
                .setBodyParameter("touristid", touristid)
                .setBodyParameter("x", x.toString())
                .setBodyParameter("y", y.toString())
                .asJsonObject().setCallback(callback)
    }

    fun signUpTourist(context: Context, account: String, password: String, callback: FutureCallback<JsonObject>) {
        val url = Server_Url + "tourist/signUpTourist.action"
        getIon(context).load(url)
                .setBodyParameter("account", account)
                .setBodyParameter("password", password)
                .asJsonObject().setCallback(callback)
    }

    fun updateTouristName(context: Context, touristid: String, name: String, callback: FutureCallback<JsonObject>) {
        val url = Server_Url + "tourist/updateTouristName.action"
        getIon(context).load(url)
                .setBodyParameter("touristid", touristid)
                .setBodyParameter("name", name)
                .asJsonObject().setCallback(callback)
    }

    fun updateTouristCar(context: Context, touristid: String, code: String, frame_num: String, engine_num: String, callback: FutureCallback<JsonObject>) {
        val url = Server_Url + "tourist/updateTouristCar.action"
        getIon(context).load(url)
                .setBodyParameter("touristid", touristid)
                .setBodyParameter("code", code)
                .setBodyParameter("frame", frame_num)
                .setBodyParameter("engine", engine_num)
                .asJsonObject().setCallback(callback)
    }

    fun loadDriverImage(user_image: ImageView?, image: String?) {
        if (image != null && !image.isEmpty()) {
            Ion.with(user_image)
                    .placeholder(R.drawable.user_image)
                    .error(R.drawable.user_image)
                    .load(Server_Url + image.replace("\\", "/"))
        }
    }

    fun loadImage(user_image: ImageView?, errorDrawable: Int, image: String?) {
        if (image != null && !image.isEmpty()) {
            Ion.with(user_image)
                    .placeholder(errorDrawable)
                    .error(errorDrawable)
                    .load(Server_Url + image.replace("\\", "/"))
        }
    }

    fun updateDriverInfo(context: Context, driverid: String, name: String, phone: String, path: String?, driver_path: String?, qualification_path: String?, callback: FutureCallback<Driver>) {
        val url = Server_Url + "Driver/updateDriverInfo.action"
        val load = getIon(context).load(url)
                .setMultipartParameter("driverid", driverid)
                .setMultipartParameter("name", name)
                .setMultipartParameter("phone", phone)
        if (!path.isNullOrEmpty()) {
            load.setMultipartFile("imagedata", File(path))
        }
        if (!driver_path.isNullOrEmpty()) {
            load.setMultipartFile("driverimagedata", File(driver_path))
        }
        if (!qualification_path.isNullOrEmpty()) {
            load.setMultipartFile("qualificationimagedata", File(qualification_path))
        }
        load.`as`(object : TypeToken<Driver>() {}).setCallback(callback)
    }

    fun updateDriverImage(context: Context, driverid: String, path: String, callback: FutureCallback<JsonObject>) {
        val url = Server_Url + "Driver/updateDriverImage.action"
        getIon(context).load(url)
                .setMultipartParameter("driverid", driverid)
                .setMultipartFile("imagedata", File(path))
                .asJsonObject().setCallback(callback)
    }

    fun updateDriverOnline(context: Context, driverid: String, callback: FutureCallback<JsonObject>) {
        val url = Server_Url + "Driver/updateDriverLasttime.action"
        getIon(context).load(url)
                .setMultipartParameter("id", driverid)
                .asJsonObject().setCallback(callback)
    }

    fun getCarCheckMenu(context: Context,callback: FutureCallback<MutableList<CarCheckMenu>>){
        val url = Server_Url + "CarCheck/getCarCheckMenu.action"
        getIon(context).load(url)
                .`as`(object : TypeToken<MutableList<CarCheckMenu>>() {}).setCallback(callback)
    }
    fun uploadCarCheck(context: Context,carCheck: CarCheck,callback: FutureCallback<JsonObject>){
        val url = Server_Url + "CarCheck/saveCarCheck.action"
        getIon(context).load(url).setBodyParameter("carcheck",Gson().toJson(carCheck))
                .asJsonObject().setCallback(callback)
    }

    fun addCarCheck(context: Context, id: String,type:String, pics: List<CarCheckMenu>, position: String, callback: FutureCallback<JsonObject>) {
        val url = Server_Url + "CarCheck/saveCarCheckImage.action"
        val load = getIon(context).load(url)
        load.setMultipartParameter("id", id)
        load.setMultipartParameter("type", type)
        load.setMultipartParameter("position", position)
        for (pic in pics){
            if (pic.url.isNotEmpty()){
                load.setMultipartFile(pic.code, File(pic.url))
            }
        }
        load.asJsonObject().setCallback(callback)
    }
}