package com.example.xiaomaibu

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlin.Throws
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.lang.Exception
import com.example.xiaomaibu.Data
import com.obs.services.ObsClient
import com.obs.services.model.DownloadFileRequest
import com.obs.services.model.GetObjectRequest
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.sql.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

class mysql_minecraft {
    fun sql_connect(): Connection? {
        var conn: Connection? = null
        try {
            Class.forName("com.mysql.jdbc.Driver")
            conn = DriverManager.getConnection(url, sql_username, sql_passwd)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            Log.d(TAG, "数据库连接失败")
        } catch (e: SQLException) {
            e.printStackTrace()
            Log.d(TAG, "数据库连接失败")
        }
        return conn
    }

    @Throws(SQLException::class)
    fun signup(conn: Connection?, username: String, passwd: String, mailbox: String): Int {
        var ifconn = 0
        val pass_hash=encode(passwd)
        val sql = "insert into user_table(username, password, mailbox) VALUES ('$username', '$pass_hash','$mailbox');"
        val st = conn!!.createStatement() as Statement
        val ifconn1 = st.execute(sql)
        st.close()
        ifconn = if (ifconn1) 0 else 1
        return ifconn
    }

    @Throws(SQLException::class)
    fun newCommUpload(conn: Connection?, comm_name: String, comm_intro: String, Image: Bitmap, background: Bitmap, obsconn: ObsClient): Int {
        var ifconn = 0
        val comm_intro_little=if(comm_intro.length>16) comm_intro.subSequence(0,16).toString() else comm_intro
        val imageString = "/commImage/$comm_name.png"
        val backgroundString = "/commBackground/$comm_name.png"
        val baos1 = ByteArrayOutputStream()
        Image.compress(Bitmap.CompressFormat.PNG, 100, baos1)
        var imageBytes1 = baos1.toByteArray()
        obsconn.putObject("bug1",imageString,ByteArrayInputStream(imageBytes1))
        val baos2 = ByteArrayOutputStream()
        background.compress(Bitmap.CompressFormat.PNG, 100, baos2)
        var imageBytes2 = baos2.toByteArray()
        obsconn.putObject("bug1",backgroundString,ByteArrayInputStream(imageBytes2))
        val sql = "insert into community_table(comm_name, comm_intro, comm_intro_little, comm_image, comm_background, leader) values ('$comm_name','$comm_intro','$comm_intro_little','$imageString','$backgroundString','${Data.getusername()}');"
        val st = conn!!.createStatement() as Statement
        val ifconn1 = st.execute(sql)
        val sql1 = "create table ${Data.getusername()}(apply_name varchar(16) unique not null, career varchar(16));"
        st.execute(sql1)
        val sql_string="update user_table set community = '$comm_name', career = 'leader' where username = '${Data.getusername()}'"
        st.execute(sql_string)
        st.close()
        Data.setStatic_career("leader")
        ifconn = if (ifconn1) 0 else 1
        return ifconn
    }

    @Throws(SQLException::class)
    fun comm_apply(conn: Connection?, career:String): Int {
        var ifconn = 0
        val sql1 = "select leader from community_table where comm_name = '${Data.getChoose_community_name()}';"
        val st = conn!!.createStatement() as Statement
        val rs = st.executeQuery(sql1)
        rs.next()
        val leadername=rs.getString("leader")
        val sql = "insert into $leadername(apply_name, career) values ('${Data.getusername()}','$career');"
        val ifconn1 = st.execute(sql)
        st.close()
        ifconn = if (ifconn1) 0 else 1
        return ifconn
    }

    @Throws(SQLException::class)
    fun signin(conn: Connection?, username: String, passwd: String): Int {
        var flagnum:Int?=null
        var career:String="laji"
        val st = conn!!.createStatement() as Statement
        val sql = "select * from user_table where mailbox = '$username';"
        val sql1="select leader from community_table where comm_name = (select community from user_table where mailbox = '$username')"
        val rs1 = st.executeQuery(sql1)
        rs1.next()
        try {
            career=rs1.getString("leader")
        }catch(e:Exception){}
        rs1.close()
        val rs = st.executeQuery(sql)
        rs.next()
        val check_passwd = rs.getString("password")
        if (check_passwd != encode(passwd)) flagnum=0 else flagnum=1
        if (rs.getString("username") == career) career="leader"
        Data.setall(rs.getString("username"),rs.getString("mailbox"),passwd,career,rs.getString("community"))
        return flagnum
    }

    @Throws(SQLException::class)
    fun image_upload(conn: Connection?, username: String, bitmap: Bitmap, obsconn:ObsClient): Int{
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        var imageBytes = baos.toByteArray()
        var pictureString = "/UserImageBug/$username.png"
        val sql_string="update user_table set picture = '$pictureString' where username = '$username'"
        obsconn.putObject("bug1",pictureString,ByteArrayInputStream(imageBytes))
        val st = conn!!.createStatement() as Statement
        val ifconn1 = st.execute(sql_string)
        st.close()
        var ifconn = if (ifconn1) 0 else 1
        return ifconn
    }

    @Throws(SQLException::class)
    fun Ok_or_No(conn: Connection?, username: String, career: String, flag:Int): Int{
        val st = conn!!.createStatement() as Statement
        var ifconn1:Boolean?=null
        if(flag==1) {
            val sql_string =
                "update user_table set community = (select comm_name from community_table where leader = '${Data.getusername()}'), career='$career' where username = '$username'"
            st.execute(sql_string)
        }
        val sql_string="delete from ${Data.getusername()} where apply_name = '$username'"
        ifconn1=st.execute(sql_string)
        st.close()
        var ifconn = if (ifconn1!!) 0 else 1
        return ifconn
    }

    @Throws(SQLException::class)
    fun image_download(conn: Connection?, username: String, obsconn: ObsClient): Bitmap{
        var decodeImage:Bitmap?=null
        var imageBytes = ByteArray(0)
        val sql_string="select picture from user_table where username = '$username'"
        val st = conn!!.createStatement() as Statement
        val rs = st.executeQuery(sql_string)
        rs.next()
        var picturestring=rs.getString("picture")
        if(picturestring!=null) {
            var obsObject = obsconn.getObject("bug1", picturestring)
            val content = obsObject.objectContent
            imageBytes = content.readBytes()
        }
        decodeImage=BitmapFactory.decodeByteArray(imageBytes,0,imageBytes!!.size)
        st.close()
        return decodeImage
    }

    fun commImage_download(conn: Connection?, username: String, obsconn: ObsClient): Bitmap{
        var decodeImage:Bitmap?=null
        var imageBytes = ByteArray(0)
        val sql_string="select comm_image from community_table where comm_name = (select community from user_table where username = '$username')"
        val st = conn!!.createStatement() as Statement
        val rs = st.executeQuery(sql_string)
        rs.next()
        var picturestring=rs.getString("comm_image")
        if(picturestring!=null) {
            var obsObject = obsconn.getObject("bug1", picturestring)
            val content = obsObject.objectContent
            imageBytes = content.readBytes()
        }
        decodeImage=BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.size)
        st.close()
        return decodeImage
    }

    @Throws(SQLException::class)
    fun community_init(conn: Connection?, obsconn: ObsClient): List<Map<String,Any?>>{
        var Commudata = mutableListOf<MutableMap<String,Any?>>()
        val sql_string="select * from community_table"
        val st = conn!!.createStatement() as Statement
        val rs = st.executeQuery(sql_string)
        var imageBitmap:Bitmap?=null
        var backgBitmap:Bitmap?=null
        while(rs.next()){
            var oneData = mutableMapOf<String,Any?>("comm_name" to "123")
            oneData.put("comm_name",rs.getString("comm_name"))
            oneData.put("comm_intro_little", rs.getString("comm_intro_little"))
            if(rs.getString("comm_image")==null)
                imageBitmap=null
            else {
                val bytesarr =
                    obsconn.getObject("bug1", rs.getString("comm_image")).objectContent.readBytes()
                imageBitmap = BitmapFactory.decodeByteArray(bytesarr,0,bytesarr.size)
            }
            if(rs.getString("comm_background")==null)
                backgBitmap=null
            else {
                val bytesarr =
                    obsconn.getObject("bug1", rs.getString("comm_background")).objectContent.readBytes()
                backgBitmap = BitmapFactory.decodeByteArray(bytesarr,0,bytesarr.size)
            }
            oneData.put("community_image", if(imageBitmap==null) null else imageBitmap)
            oneData.put("background", if(backgBitmap==null) null else backgBitmap)
            Commudata.add(oneData)
        }
        st.close()
        return Commudata
    }

    @Throws(SQLException::class)
    fun users_init(conn: Connection?, user_name: String?, obsconn: ObsClient): List<Map<String,Any?>>{
        var Commudata = mutableListOf<MutableMap<String,Any?>>()
        val sql_string="SELECT * FROM user_table where community = '$user_name';"
        val st = conn!!.createStatement() as Statement
        val rs = st.executeQuery(sql_string)
        var userBitmap:Bitmap?=null
        while(rs.next()){
            var oneData = mutableMapOf<String,Any?>("comm_name" to "123")
            oneData.put("username",rs.getString("username"))
            oneData.put("career", rs.getString("career"))
            if(rs.getString("picture")==null)
                userBitmap=null
            else {
                val bytesarr =
                    obsconn.getObject("bug1", rs.getString("picture")).objectContent.readBytes()
                userBitmap = BitmapFactory.decodeByteArray(bytesarr,0,bytesarr.size)
            }
            oneData.put("picture", if(userBitmap==null) null else userBitmap)
            Commudata.add(oneData)
        }
        st.close()
        return Commudata
    }

    @Throws(SQLException::class)
    fun apply_init(conn: Connection?, user_name: String?,obsconn: ObsClient): List<Map<String,Any?>>{
        var Commudata = mutableListOf<MutableMap<String,Any?>>()
        val sql_string="SELECT picture,apply_name,$user_name.career FROM user_table inner join $user_name on $user_name.apply_name=user_table.username where username = (select apply_name from $user_name) "
        val st = conn!!.createStatement() as Statement
        val rs = st.executeQuery(sql_string)
        var userBitmap:Bitmap?=null
        while(rs.next()){
            var oneData = mutableMapOf<String,Any?>("comm_name" to "123")
            oneData.put("username",rs.getString("apply_name"))
            oneData.put("career", rs.getString("career"))
            if(rs.getString("picture")==null)
                userBitmap=null
            else {
                val bytesarr =
                    obsconn.getObject("bug1", rs.getString("picture")).objectContent.readBytes()
                userBitmap = BitmapFactory.decodeByteArray(bytesarr,0,bytesarr.size)
            }
            oneData.put("picture", if(userBitmap==null) null else userBitmap)
            Commudata.add(oneData)
        }
        st.close()
        return Commudata
    }

    @Throws(SQLException::class)
    fun comm_delete(conn: Connection?, username: String?){
        val comm_name_que="select comm_name from community_table where leader = '$username'"
        val sql_string="drop table $username"
        val sql_string3="delete from community_table where leader = '$username'"
        val st = conn!!.createStatement() as Statement
        val rs = st.executeQuery(comm_name_que)
        rs.next()
        val comm_name=rs.getString("comm_name")
        rs.close()
        val sql_string2="update user_table set community=null, career=null where community = '$comm_name'"
        st.execute(sql_string)
        st.execute(sql_string3)
        st.execute(sql_string2)
        st.close()
        return
    }


    @Throws(SQLException::class)
    fun Intro_init(conn: Connection?, community_name: String?): String{
        val sql_string="select comm_intro from community_table where comm_name = '$community_name'"
        val st = conn!!.createStatement() as Statement
        val rs = st.executeQuery(sql_string)
        rs.next()
        val comm_intros=rs.getString("comm_intro")
        st.close()
        return comm_intros
    }

    @Throws(SQLException::class)
    fun info_update(conn: Connection?, username: String){

    }

    fun encode(text: String): String {
        try {
            //获取md5加密对象
            val instance: MessageDigest = MessageDigest.getInstance("MD5")
            //对字符串加密，返回字节数组
            val digest:ByteArray = instance.digest(text.toByteArray())
            var sb : StringBuffer = StringBuffer()
            for (b in digest) {
                //获取低八位有效值
                var i :Int = b.toInt() and 0xff
                //将整数转化为16进制
                var hexString = Integer.toHexString(i)
                if (hexString.length < 2) {
                    //如果是一位的话，补0
                    hexString = "0" + hexString
                }
                sb.append(hexString)
            }
            return sb.toString()

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return ""
    }

    companion object {
        private const val TAG = "rime"
        private const val sql_ip = "114.116.221.105"
        private const val dbName = "minecraft"
        private const val port = "3306"
        private const val url = "jdbc:mysql://" + sql_ip + ":" + port + "/" + dbName + "?useSSL=false"
        private const val sql_username = "root"
        private const val sql_passwd = "AAss161895"
    }
}