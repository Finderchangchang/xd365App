package com.djj.xd365.util

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import com.djj.xd365.global.XD365http
import com.koushikdutta.async.future.FutureCallback

class OnlineTask(val context: Context,val driverid: String,val period:Long) {
    val handlerThread=HandlerThread("OnlineTask")
    var task:Runnable?=null
    var handler:Handler?=null
    fun start(){
        handlerThread.start()
        handler=Handler(handlerThread.looper)
        task= Runnable {
            XD365http.updateDriverOnline(context,driverid, FutureCallback { e, result ->
                if (task!=null) {
                    handler?.postDelayed(task, period)
                }
            })
        }
        handler?.postDelayed(task, 1000L)
    }
    fun stop(){
        handler?.removeCallbacks(task)
    }
}