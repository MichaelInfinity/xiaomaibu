package com.example.xiaomaibu

import com.example.xiaomaibu.ui.home.HomeViewModel
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
import android.view.*
import android.widget.ImageView
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.xiaomaibu.databinding.ActivityUsermainBinding
import com.example.xiaomaibu.Data
import org.w3c.dom.Text
import java.sql.Connection
import java.sql.SQLException

class usermainAcivity : AppCompatActivity() {
    private var mAppBarConfiguration: AppBarConfiguration? = null
    private var binding: ActivityUsermainBinding? = null
    private var navigationView: NavigationView? = null
    private var imageView_navheader: ImageView? = null
    private var username_navheader:TextView?=null
    private var email_navheader:TextView?=null
    var decodebitmap:Bitmap?=null

    var updatehandler = Handler {
        if(decodebitmap!=null)
            imageView_navheader!!.setImageBitmap(decodebitmap)
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsermainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val headerview = navigationView!!.getHeaderView(0)
        imageView_navheader = headerview.findViewById(R.id.imageView)
        username_navheader = headerview.findViewById(R.id.username_nav)
        email_navheader=headerview.findViewById(R.id.email_nav)
        setSupportActionBar(binding!!.appBarUsermainAcivity.toolbar)
        binding!!.appBarUsermainAcivity.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        val drawer = binding!!.drawerLayout
        val navigationView = binding!!.navView
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = AppBarConfiguration.Builder(
            R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
            .setOpenableLayout(drawer)
            .build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_usermain_acivity)
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration!!)
        NavigationUI.setupWithNavController(navigationView, navController)
        if(Data.getcareer() != "leader")
            navigationView.menu.findItem(R.id.nav_slideshow).setVisible(false)
    }

    override fun onResume() {
        super.onResume()
        updateinfo()
    }

    fun updateinfo(){
        if(Data.getusername()!=null)
            username_navheader!!.setText(Data.getusername())
        if(Data.getmailbox()!=null)
            email_navheader!!.setText(Data.getmailbox())
        val mysqlMinecraft = mysql_minecraft()
        val msg = Message.obtain()
        msg.what = 0
        val bundle = Bundle()
        var sql_username=username_navheader!!.text.toString()

        Thread(object : Runnable {
            var conn: Connection? = null
            override fun run() {
                try {
                    println("upload?")
                    conn = mysqlMinecraft.sql_connect()
                    decodebitmap = mysqlMinecraft.image_download(conn, sql_username)
                    conn!!.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                bundle.putString("key", "我是一笑消息")
                msg.data = bundle
                updatehandler.sendMessage(msg)
            }
        }).start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.usermain_acivity, menu)
        //mMenu.findItem(R.id.nav_slideshow).setVisible(false)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_usermain_acivity)
        return (NavigationUI.navigateUp(navController, mAppBarConfiguration!!)
                || super.onSupportNavigateUp())
    }

    fun user_head_image_click(v: View?) {
        val intent1 = Intent()
        intent1.setClass(this@usermainAcivity, userInfo_Activity::class.java)
        startActivity(intent1)
    }

}