package com.example.xiaomaibu


import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mikhaellopez.circularimageview.CircularImageView
import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import android.content.SharedPreferences
import android.view.View.OnFocusChangeListener
import android.graphics.Typeface
import android.os.Handler
import android.os.Message
import android.view.View
import com.github.florent37.viewanimator.ViewAnimator
import java.sql.Connection
import java.sql.SQLException

class MainActivity : AppCompatActivity() {
    private var image: CircularImageView? = null
    private var title_text: TextView? = null
    private var user_text: EditText? = null
    private var passwd_text: EditText? = null
    private var flag = 0
    private var sqlflag = 0
    private var sp:SharedPreferences?=null
    var handler = Handler {
        if (sqlflag == 0) {
            Toast.makeText(this@MainActivity, "登录失败,用户名或密码不正确", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@MainActivity, "登录成功", Toast.LENGTH_SHORT).show()
            val editor=sp!!.edit()
            editor.putString("username",Data.getmailbox())
            editor.putString("passwd",Data.getpasswd())
            editor.commit()
            val intent = Intent()
            intent.setClass(this@MainActivity, usermainAcivity::class.java)
            startActivity(intent)
            finish()
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        image = findViewById<View>(R.id.xiaotitle) as CircularImageView
        user_text = findViewById<View>(R.id.user_text) as EditText
        title_text = findViewById<View>(R.id.textView_title) as TextView
        passwd_text = findViewById<View>(R.id.passwd_text) as EditText
        sp=getSharedPreferences("UserInfo", MODE_PRIVATE)
        if(sp!!.getString("username","")!=null) {
            user_text!!.setText(sp!!.getString("username", null))
            passwd_text!!.setText(sp!!.getString("passwd", null))
        }
        user_text!!.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                username_click(v)
            } else {
                // EditText失去焦点
            }
        }
        animateSequentially()
        supportActionBar!!.hide()
        title_text!!.typeface = Typeface.createFromAsset(assets, "fonts/nailaoziti.ttf")
    }

    protected fun animateSequentially() {
        ViewAnimator.animate(image)
                .rotation(360f)
                .start()
    }

    fun signin_click(view: View?) {
        val sql_username = user_text!!.text.toString()
        val sql_passwd = passwd_text!!.text.toString()
        val mysqlMinecraft = mysql_minecraft()
        val msg = Message.obtain()
        msg.what = 0
        val bundle = Bundle()
        Thread(object : Runnable {
            var conn: Connection? = null
            override fun run() {
                try {
                    println("getin?")
                    conn = mysqlMinecraft.sql_connect()
                    sqlflag = mysqlMinecraft.signin(conn, sql_username, sql_passwd)
                    conn!!.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                bundle.putString("key", "我是一笑消息")
                msg.data = bundle
                handler.sendMessage(msg)
            }
        }).start()
    }

    fun signup_click(view: View?) {
        startActivity(Intent(this@MainActivity, signup::class.java))
        //finish()
    }

    fun username_click(view: View?) {
        if (flag == 0) user_text!!.setText("")
        flag = 1
    }
}