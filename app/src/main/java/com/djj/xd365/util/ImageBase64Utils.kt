package com.djj.xd365.util

import android.util.Base64
import java.io.*


/**
 * Created by liufeng on 2018-07-05.
 * 用于 xd365
 */
object ImageBase64Utils {
    fun bytesToBase64(bytes: ByteArray): String {
        return Base64.encodeToString(bytes,Base64.DEFAULT)// 返回Base64编码过的字节数组字符串
    }

    /**
     * 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
     *
     * @param path 图片路径
     * @return base64字符串
     */
    @Throws(IOException::class)
    fun imageToBase64(path: String): String {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        var data: ByteArray? = null
        // 读取图片字节数组
        var `in`: InputStream? = null
        try {
            `in` = FileInputStream(path)
            data = ByteArray(`in`!!.available())
            `in`!!.read(data)
        } finally {
            if (`in` != null) {
                try {
                    `in`!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return Base64.encodeToString(data,Base64.DEFAULT)// 返回Base64编码过的字节数组字符串
    }

    /**
     * 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
     *
     * @param path 图片路径
     * @return base64字符串
     */
    @Throws(IOException::class)
    fun imageToBase64ByteArray(path: String): ByteArray {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        var data: ByteArray? = null
        // 读取图片字节数组
        var `in`: InputStream? = null
        try {
            `in` = FileInputStream(path)
            data = ByteArray(`in`!!.available())
            `in`!!.read(data)
        } finally {
            if (`in` != null) {
                try {
                    `in`!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return Base64.encode(data,Base64.DEFAULT)// 返回Base64编码过的字节数组字符串
    }

    /**
     * 处理Base64解码并写图片到指定位置
     *
     * @param base64 图片Base64数据
     * @param path   图片保存路径
     * @return
     */
    @Throws(IOException::class)
    fun base64ToImageFile(base64: String, path: String): Boolean {// 对字节数组字符串进行Base64解码并生成图片
        // 生成jpeg图片
        try {
            val out = FileOutputStream(path)
            return base64ToImageOutput(base64, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return false
    }

    /**
     * 处理Base64解码并输出流
     *
     * @param base64
     * @param out
     * @return
     */
    @Throws(IOException::class)
    fun base64ToImageOutput(base64: String?, out: OutputStream): Boolean {
        if (base64 == null) { // 图像数据为空
            return false
        }
        try {
            // Base64解码
            val bytes = Base64.decode(base64,Base64.DEFAULT)
            for (i in bytes.indices) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] = (bytes[i]+256).toByte()
                }
            }
            // 生成jpeg图片
            out.write(bytes)
            out.flush()
            return true
        } finally {
            try {
                out.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
}