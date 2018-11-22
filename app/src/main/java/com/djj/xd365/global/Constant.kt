package com.djj.xd365.global

import com.djj.xd365.db.Driver
import com.djj.xd365.db.Tourist

/**
 * Created by liufeng on 2018-06-30.
 * 用于 xd365
 */
class Constant {
    var driver: Driver?=null
    var tourist:Tourist?=null
    val APP_CONFIG:String="XD365_CONFIG"

    val KEY_ACCOUNT:String="ACCOUNT"
    val KEY_PASSWORD:String="PASSWORD"
}