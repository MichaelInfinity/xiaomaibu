package com.example.xiaomaibu

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlin.Throws
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.lang.Exception
import com.example.xiaomaibu.Data
import java.io.ByteArrayInputStream
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
        val sql = "insert into user_table(username, password, mailbox) VALUES ('$username', '$passwd','$mailbox');"
        val st = conn!!.createStatement() as Statement
        val ifconn1 = st.execute(sql)
        st.close()
        ifconn = if (ifconn1) 0 else 1
        return ifconn
    }

    @Throws(SQLException::class)
    fun newCommUpload(conn: Connection?, comm_name: String, comm_intro: String, Image: Bitmap, background: Bitmap): Int {
        var ifconn = 0
        val bitmapArr1=ByteArrayOutputStream()
        Image.compress(Bitmap.CompressFormat.PNG,100,bitmapArr1)
        var bitBytes=bitmapArr1.toByteArray()
        val imageString=Base64.encodeToString(bitBytes,Base64.DEFAULT)
        val bitmapArr2=ByteArrayOutputStream()
        background.compress(Bitmap.CompressFormat.PNG,100,bitmapArr2)
        bitBytes=bitmapArr2.toByteArray()
        val backgroundString=Base64.encodeToString(bitBytes,Base64.DEFAULT)
        val comm_intro_little=if(comm_intro.length>16) comm_intro.subSequence(0,16).toString() else comm_intro
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
        val sql = "select * from user_table where mailbox = '$username';"
        val st = conn!!.createStatement() as Statement
        val rs = st.executeQuery(sql)
        rs.next()
        val check_passwd = rs.getString("password")
        if (check_passwd != passwd) flagnum=0 else flagnum=1
        Data.setall(rs.getString("username"),rs.getString("mailbox"),check_passwd,rs.getString("career"),rs.getString("community"))
        return flagnum
    }

    @Throws(SQLException::class)
    fun image_upload(conn: Connection?, username: String, bitmap: Bitmap): Int{
        var imageString : String? = null
        try {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            var imageBytes = baos.toByteArray()
            imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        }catch (e:Exception){
            e.printStackTrace()
        }
        val sql_string="update user_table set picture = '$imageString' where username = '$username'"
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
    fun image_download(conn: Connection?, username: String): Bitmap{
        var decodeImage:Bitmap?=null
        val sql_string="select picture from user_table where username = '$username'"
        val st = conn!!.createStatement() as Statement
        val rs = st.executeQuery(sql_string)
        rs.next()
        var base64string=rs.getString("picture")
        if(base64string==null)
            base64string=""
        val imageBytes=Base64.decode(base64string,Base64.DEFAULT)
        decodeImage=BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.size)
        st.close()
        return decodeImage
    }

    fun commImage_download(conn: Connection?, username: String): Bitmap{
        var decodeImage:Bitmap?=null
        val sql_string="select comm_image from community_table where comm_name = (select community from user_table where username = '$username')"
        val st = conn!!.createStatement() as Statement
        val rs = st.executeQuery(sql_string)
        rs.next()
        var base64string=rs.getString("comm_image")
        if(base64string==null)
            base64string=""
        val imageBytes=Base64.decode(base64string,Base64.DEFAULT)
        decodeImage=BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.size)
        st.close()
        return decodeImage
    }

    @Throws(SQLException::class)
    fun community_init(conn: Connection?): List<Map<String,String>>{
        var Commudata = mutableListOf<MutableMap<String,String>>()
        val sql_string="select * from community_table"
        val st = conn!!.createStatement() as Statement
        val rs = st.executeQuery(sql_string)
        while(rs.next()){
            println("heheheheh: "+rs.getString("comm_name"))
            var oneData = mutableMapOf<String,String>("comm_name" to "123")
            oneData.put("comm_name",rs.getString("comm_name"))
            oneData.put("comm_intro_little", rs.getString("comm_intro_little"))
            oneData.put("community_image", if(rs.getString("comm_image")==null) "" else rs.getString("comm_image"))
            oneData.put("background", if(rs.getString("comm_background")==null) "" else rs.getString("comm_background"))
            Commudata.add(oneData)
        }
        st.close()
        return Commudata
    }

    @Throws(SQLException::class)
    fun users_init(conn: Connection?, user_name: String?): List<Map<String,String>>{
        var Commudata = mutableListOf<MutableMap<String,String>>()
        val sql_string="SELECT * FROM user_table where community = '$user_name';"
        val st = conn!!.createStatement() as Statement
        val rs = st.executeQuery(sql_string)
        while(rs.next()){
            var oneData = mutableMapOf<String,String>("comm_name" to "123")
            oneData.put("username",rs.getString("username"))
            oneData.put("career", rs.getString("career"))
            oneData.put("picture", if(rs.getString("picture")==null) "" else rs.getString("picture"))
            Commudata.add(oneData)
        }
        st.close()
        return Commudata
    }

    @Throws(SQLException::class)
    fun apply_init(conn: Connection?, user_name: String?): List<Map<String,String>>{
        var Commudata = mutableListOf<MutableMap<String,String>>()
        val sql_string="SELECT picture,apply_name,$user_name.career FROM user_table inner join $user_name on $user_name.apply_name=user_table.username where username = (select apply_name from $user_name) "
        val st = conn!!.createStatement() as Statement
        val rs = st.executeQuery(sql_string)
        while(rs.next()){
            var oneData = mutableMapOf<String,String>("comm_name" to "123")
            oneData.put("username",rs.getString("apply_name"))
            oneData.put("career", rs.getString("career"))
            oneData.put("picture", if(rs.getString("picture")==null) "" else rs.getString("picture"))
            Commudata.add(oneData)
        }
        st.close()
        return Commudata
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

    companion object {
        private const val TAG = "rime"
        private const val sql_ip = ""
        private const val dbName = "minecraft"
        private const val port = "3306"
        private const val url = "jdbc:mysql://" + sql_ip + ":" + port + "/" + dbName + "?useSSL=false"
        private const val sql_username = "root"
        private const val sql_passwd = ""
    }
}