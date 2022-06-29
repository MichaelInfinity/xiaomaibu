package com.example.xiaomaibu.ui.home

import com.example.xiaomaibu.ui.home.HomeViewModel
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.xiaomaibu.ui.gallery.GalleryViewModel
import com.example.xiaomaibu.ui.slideshow.SlideshowViewModel
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
import android.view.View.OnFocusChangeListener
import android.graphics.Typeface
import kotlin.Throws
import com.coorchice.library.SuperTextView
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.provider.MediaStore
import android.app.Activity
import android.graphics.BitmapFactory
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.AnimationUtils.loadAnimation
import android.view.animation.LayoutAnimationController
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xiaomaibu.*
import com.example.xiaomaibu.databinding.FragmentHomeBinding
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import java.sql.Connection
import java.sql.SQLException
import java.util.*
import com.github.ybq.android.spinkit.style.DoubleBounce

import com.github.ybq.android.spinkit.sprite.Sprite

import android.R
import android.os.*

import android.widget.ProgressBar
import com.github.ybq.android.spinkit.style.Wave


class HomeFragment : Fragment() {
    private var homeViewModel: HomeViewModel? = null
    private var binding: FragmentHomeBinding? = null
    var recycleView:RecyclerView?=null
    var data:List<Map<String,Any?>>?=null

    var inithandler = Handler {
        //创建adapter,getActivity获得Fragment依附的Activity对象
        val animation = AnimationUtils.loadAnimation(activity,R.anim.slide_in_left)
        val lac = LayoutAnimationController(animation)
        lac.order=LayoutAnimationController.ORDER_NORMAL
        lac.delay=0.6f
        binding!!.communityIndex.layoutAnimation=lac
        val adapter=community_Adapter(this.activity,data)
        binding!!.communityIndex.adapter=adapter
        binding!!.communityIndex.itemAnimator!!.addDuration=120;
        val layoutManager = LinearLayoutManager(this.activity)
        layoutManager.orientation=RecyclerView.VERTICAL
        binding!!.communityIndex.layoutManager = layoutManager
        binding!!.spinKit.visibility=View.GONE
        false
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        var wave: Sprite = Wave()
        binding!!.spinKit.setIndeterminateDrawable(wave)
        val root: View = binding!!.root
        getData()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
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
                    data = mysqlMinecraft.community_init(conn,obsClient)
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
}