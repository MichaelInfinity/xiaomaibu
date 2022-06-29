package com.example.xiaomaibu

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.obs.services.ObsClient
import java.io.InputStream
import java.sql.Connection
import java.sql.SQLException

class ObsBug {
    private val endPoint = "https://obs.cn-north-4.myhuaweicloud.com"
    private val ak = "ZQYKF3RKKXAHZA1JQQGC"
    private val sk = "3YEiIZ9bpFta2JNvxfWs8DdigC6Q4Zupo1xFL6XE"
    fun connect_obsClient(): ObsClient {
        return ObsClient(ak, sk, endPoint)
    }

    public fun imageRead(obsConn:ObsClient, imagePath:String): Bitmap {
        var imageBytes = ByteArray(0)
        var content: InputStream?=null
        content = obsConn.getObject("bug1", imagePath).getObjectContent()
        imageBytes=content!!.readBytes()
        val backgroundMap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        return backgroundMap
    }
}