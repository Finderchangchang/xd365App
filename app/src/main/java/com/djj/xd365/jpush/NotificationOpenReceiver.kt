package com.djj.xd365.jpush

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat
import cn.jpush.android.api.JPushInterface
import com.djj.xd365.R
import com.djj.xd365.activity.ComMessageActivity


/**
 * 自定义JPush message 接收器,包括操作tag/alias的结果返回(仅仅包含tag/alias新接口部分)
 */
class NotificationOpenReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            JPushInterface.ACTION_NOTIFICATION_OPENED ->
                context.startActivity(Intent(context, ComMessageActivity::class.java))
//            JPushInterface.ACTION_NOTIFICATION_RECEIVED -> {
//
//            }

        }

    }


}
