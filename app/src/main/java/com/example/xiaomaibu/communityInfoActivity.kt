package com.example.xiaomaibu

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Base64
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coorchice.library.SuperTextView
import com.mikhaellopez.circularimageview.CircularImageView
import com.obs.services.ObsClient
import java.io.ByteArrayOutputStream
import java.sql.Connection
import java.sql.SQLException

class communityInfoActivity : AppCompatActivity() {
    var comm_image: CircularImageView?=null
    var comm_backg:ImageView?=null
    var comm_intro:SuperTextView?=null
    var comm_name:SuperTextView?=null
    var comm_user:RecyclerView?=null
    var career_app:EditText?=null
    var apply_but:SuperTextView?=null
    var data:List<Map<String,Any?>>?=null
    var comm_introduction:String? = null
    var flag:Int=1

    var inithandler = Handler {
        //创建adapter,getActivity获得Fragment依附的Activity对象
        val adapter=community_user_Adapter(this,data)
        comm_user!!.adapter=adapter
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation=RecyclerView.VERTICAL
        comm_user!!.layoutManager = layoutManager
        false
    }

    var introhandler = Handler {
        //创建adapter,getActivity获得Fragment依附的Activity对象
        comm_intro!!.setText(comm_introduction!!)
        false
    }

    var applyhandler = Handler {
        //创建adapter,getActivity获得Fragment依附的Activity对象
        if (flag == 0) {
            Toast.makeText(this, "申请失败", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "申请成功", Toast.LENGTH_SHORT).show()
            finish()
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_info)
        comm_image=findViewById<View>(R.id.community_info_image) as CircularImageView
        comm_backg=findViewById<View>(R.id.backgroundImage_info) as ImageView
        comm_intro=findViewById<View>(R.id.community_upload_intro2) as SuperTextView
        comm_name=findViewById<View>(R.id.community_info_name) as SuperTextView
        comm_user=findViewById<View>(R.id.user_info) as RecyclerView
        career_app=findViewById<View>(R.id.career_input) as EditText
        apply_but=findViewById<View>(R.id.apply_button) as SuperTextView
    }

    override fun onResume() {
        super.onResume()
        career_app!!.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                career_app_click(v)
            } else {
                // EditText失去焦点
            }
        }

        apply_but!!.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                apply_but_click(v)
            } else {
                // EditText失去焦点
            }
        }
        val ImageString=Data.getChoose_community_Image()
        val backgroundString = Data.getChoose_community_Background()
        val obsbug = ObsBug()
        if (backgroundString != null) {
            comm_backg!!.setImageBitmap(backgroundString)
        }
        if (ImageString != null) {
            comm_image!!.setImageBitmap(ImageString)
        }
        comm_name!!.setText(Data.getChoose_community_name())
        getData()
        getIntroData()
    }

    fun career_app_click(view:View?){
        career_app!!.setText("")
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun apply_but_click(view:View?){
        val mysqlMinecraft = mysql_minecraft()
        val msg = Message.obtain()
        msg.what = 0
        val bundle = Bundle()

        Thread(object : Runnable {
            var conn: Connection? = null
            override fun run() {
                try {
                    conn = mysqlMinecraft.sql_connect()
                    flag = mysqlMinecraft.comm_apply(conn, career_app!!.text.toString())
                    conn!!.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                bundle.putString("key", "我是一笑消息")
                msg.data = bundle
                applyhandler.sendMessage(msg)
            }
        }).start()
    }

    fun getData()
    {
        val mysqlMinecraft = mysql_minecraft()
        val msg = Message.obtain()
        msg.what = 0
        val bundle = Bundle()
        val obsbug=ObsBug()
        val obsClient=obsbug.connect_obsClient()
        Thread(object : Runnable {
            var conn: Connection? = null
            override fun run() {
                try {
                    println("upload?")
                    conn = mysqlMinecraft.sql_connect()
                    data = mysqlMinecraft.users_init(conn, comm_name!!.text.toString(),obsClient)
                    conn!!.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                bundle.putString("key", "我是一笑消息")
                msg.data = bundle
                inithandler.sendMessage(msg)
            }
        }).start()
    }

    fun getIntroData()
    {
        val mysqlMinecraft = mysql_minecraft()
        val msg = Message.obtain()
        msg.what = 0
        val bundle = Bundle()

        Thread(object : Runnable {
            var conn: Connection? = null
            override fun run() {
                try {
                    conn = mysqlMinecraft.sql_connect()
                    comm_introduction = mysqlMinecraft.Intro_init(conn, comm_name!!.text.toString())
                    conn!!.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                bundle.putString("key", "我是一笑消息")
                msg.data = bundle
                introhandler.sendMessage(msg)
            }
        }).start()
    }
}