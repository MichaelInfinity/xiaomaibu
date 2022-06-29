package com.example.xiaomaibu

import com.example.xiaomaibu.ui.home.HomeViewModel
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.xiaomaibu.ui.gallery.GalleryViewModel
import com.example.xiaomaibu.ui.slideshow.SlideshowViewModel
import com.example.xiaomaibu.ImageUtils
import android.os.Build
import androidx.core.content.FileProvider
import androidx.appcompat.widget.AppCompatImageView
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import com.mikhaellopez.circularimageview.CircularImageView
import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import com.example.xiaomaibu.usermainAcivity
import com.example.xiaomaibu.R
import android.view.View.OnFocusChangeListener
import android.graphics.Typeface
import com.example.xiaomaibu.mysql_minecraft
import com.example.xiaomaibu.signup
import kotlin.Throws
import com.coorchice.library.SuperTextView
import com.example.xiaomaibu.MainActivity
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.example.xiaomaibu.userInfo_Activity
import android.provider.MediaStore
import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import java.sql.Connection
import java.sql.SQLException

class signup : AppCompatActivity() {
    var username: EditText? = null
    var mailbox: EditText? = null
    var passwd: EditText? = null
    var confirm_passwd: EditText? = null
    var signup_button: SuperTextView? = null
    var flag = 0
    var flag1 = 0
    var flag2 = 0
    var flag3 = 0
    var flag4 = 0
    var handler = Handler {
        if (flag4 == 0) Toast.makeText(this@signup, "注册失败", Toast.LENGTH_SHORT).show() else Toast.makeText(this@signup, "注册成功", Toast.LENGTH_SHORT).show()

        val intent = Intent()
        intent.setClass(this@signup, MainActivity::class.java)
        startActivity(intent)
        finish()
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        flag3 = 0
        flag2 = flag3
        flag1 = flag2
        flag = flag1
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        supportActionBar!!.hide()
        username = findViewById<View>(R.id.username_text) as EditText
        mailbox = findViewById<View>(R.id.mailbox_text) as EditText
        passwd = findViewById<View>(R.id.password_text) as EditText
        confirm_passwd = findViewById<View>(R.id.password_confirm_text) as EditText
        signup_button = findViewById<View>(R.id.real_signup) as SuperTextView
    }

    override fun onResume() {
        super.onResume()
        username!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                username_click(v)
            } else {
                username!!.clearFocus()
            }
        }
        mailbox!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                Mailbox_click(v)
            } else {
                mailbox!!.clearFocus()
            }
        }
        passwd!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                passwd_click(v)
            } else {
                passwd!!.clearFocus()
            }
        }
        confirm_passwd!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                Confirm_passwd_click(v)
            } else {
                confirm_passwd!!.clearFocus()
            }
        }
        signup_button!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                signup_button_click(v)
            } else {
                signup_button!!.clearFocus()
            }
        }
    }

    fun username_click(v: View?) {
        if (flag == 0) username!!.setText("")
        flag = 1
    }

    fun Mailbox_click(v: View?) {
        if (flag1 == 0) mailbox!!.setText("")
        flag1 = 1
    }

    fun passwd_click(v: View?) {
        if (flag2 == 0) passwd!!.setText("")
        flag2 = 1
    }

    fun Confirm_passwd_click(v: View?) {
        if (flag3 == 0) confirm_passwd!!.setText("")
        flag3 = 1
    }

    fun signup_button_click(v: View?) {
        val mysqlMinecraft = mysql_minecraft()
        val sql_username = username!!.text.toString()
        val sql_passwd = passwd!!.text.toString()
        val sql_mailbox = mailbox!!.text.toString()
        val sql_passwd_confirm = confirm_passwd!!.text.toString()
        if(sql_passwd!=sql_passwd_confirm)
        {
            Toast.makeText(this@signup, "两次密码输入不一致", Toast.LENGTH_SHORT).show()
            return
        }
        val msg = Message.obtain()
        msg.what = 0
        val bundle = Bundle()
        Thread(object : Runnable {
            var conn: Connection? = null
            override fun run() {
                try {
                    println("getin?")
                    conn = mysqlMinecraft.sql_connect()
                    flag4 = mysqlMinecraft.signup(conn, sql_username, sql_passwd, sql_mailbox)
                    conn!!.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                    flag4 = 0
                }
                bundle.putString("key", "我是一笑消息")
                msg.data = bundle
                handler.sendMessage(msg)
            }
        }).start()
    }

    companion object {
        private val toast: Toast? = null
        var mutex1 = 0
    }
}