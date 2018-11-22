package com.djj.xd365.db

import com.baidu.mapapi.map.Overlay
import com.google.gson.JsonObject
import java.util.*

/**
 * Created by liufeng on 2018-06-30.
 * 用于 xd365
 */

data class XDMenu(val bqStudy:Boolean,val zzStudy:Boolean,val exam:Boolean,val studyLog:Boolean,val carPosition:Boolean,val violation:Boolean,val roadInfo:Boolean,val comMessage:Boolean,val carCheck:Boolean)
data class Driver(val account:String,val password:String, val id:String,val name:String?,val carid:String,val company:String,val study_count:Int?,val hardphoto:String?,val phone:String?,val carphoto:String?,val peoplezgz:String?)
data class Tourist(val account:String,val password:String, val id:String,val name:String,val code:String?,val frame_num:String?,val engin_num:String?)
/**
 * id1 学习记录id
 * name 学习内容名称
 * file_list 学习文档id 列表
 * start_time1 学习记录开始学习时间
 */
data class Stucourselog(val name:String, val id1:String,val file_list:String,val start_time1:Long)
data class Stucourse(val id:String,val name:String,val file_list:String)
data class StulogCount(val stulogid:String,val count:Int)

/**
 * 学习记录详情
 * id 学习文档id
 * tittle 学习文档标题
 * url学习文档路径
 * 学习记录详情表
 * id1 学习记录详情id
 *
 */
data class StuFile(val tittle:String,val log_time:Int,val stu_time:Int,val file_type:String,val id1: String,val begin_time:Long,val id:String,val url:String)

/**
 * id 考试内容id
 * owen 创建者id
 * car_type 是否通过
 */
data class Exam(val name:String,val car_type:String,val owen:String,val id:String)
data class Common<T>(val rowCounts:Int,val rows:List<T>)
/**
 * id 学习记录id
 * courseid 学习内容名称
 * driverid 驾驶员name
 * carid 车牌号
 * start_time 学习记录开始时间
 * end_time 学习记录结束时间
 * charge 企业名称
 * sfwatched 是否抽查('已抽查','未抽查')
 * sfchecked 是否审核('已审核','未审核')
 */
data class StudyLog(val id: String,val courseid:String,val driverid:String,val carid:String,val start_time:Long,val end_time:Long,val charge:String,val sfwatched:String,val sfchecked:String)

data class CarPositionResult(val result:JsonObject,val status:String,val carcode:String)

data class ComMessage(val tittle:String,val content:String,val create_time:Long,val id:String,val pmid:String,val readtime:Long)

data class MapCar(var carcode:String,var markOverlay:Overlay?,var textOverlay:Overlay?,var charge:Boolean)

data class CarCheck(var id:String,var name:String,var driverid: String,var carid: String,var creatime:Long,var stype:String,var jcdate:Long,var wather:String,var hezai:String,var weightnow:String,var qiyundate:Long,var qiyundi:String,var mudidi:String,var zjtkd:String,var dddate:Long,var licheng:String,var qita:String,var xcq:String,var xcz:String,var xch:String)
data class CarCheckMenu(var code:String,var name:String,var url:String)
