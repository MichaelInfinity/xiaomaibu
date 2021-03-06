package com.example.xiaomaibu.ui.slideshow

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
import android.view.View.OnFocusChangeListener
import android.graphics.Typeface
import kotlin.Throws
import com.coorchice.library.SuperTextView
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.provider.MediaStore
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xiaomaibu.*
import com.example.xiaomaibu.databinding.FragmentSlideshowBinding
import java.sql.Connection
import java.sql.SQLException

class SlideshowFragment : Fragment() {
    private var slideshowViewModel: SlideshowViewModel? = null
    private var binding: FragmentSlideshowBinding? = null
    var data:List<Map<String,Any?>>?=null
    var newbitMap: Bitmap?=null

    var inithandler = Handler {
        //??????adapter,getActivity??????Fragment?????????Activity??????
        val animation = AnimationUtils.loadAnimation(activity,R.anim.my_anim)
        val lac = LayoutAnimationController(animation)
        lac.order= LayoutAnimationController.ORDER_NORMAL
        lac.delay=0.6f
        binding!!.communityRecycle.layoutAnimation=lac
        val adapter=community_apply_Adapter(this.activity,data)
        binding!!.communityRecycle.adapter=adapter
        val layoutManager = LinearLayoutManager(this.activity)
        layoutManager.orientation= RecyclerView.VERTICAL
        binding!!.communityRecycle.layoutManager = layoutManager
        false
    }

    var deletehandler = Handler {
        //??????adapter,getActivity??????Fragment?????????Activity??????
        val intent = Intent()
        intent.setClass(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
        false
    }

    var Imagehandler = Handler {
        //??????adapter,getActivity??????Fragment?????????Activity??????
        binding!!.communityApplyImage.setImageBitmap(newbitMap!!)
        false
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        slideshowViewModel = ViewModelProvider(this).get(SlideshowViewModel::class.java)
        binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding!!.root
        getImage()
        binding!!.communityDeleteImage.setOnClickListener {
            communityDelete()
        }
        getData()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun communityDelete(){
        val alertDialog = AlertDialog.Builder(activity)
            //??????
            .setTitle("title")
            //??????
            .setMessage("??????????????????\n????????????????????????????????????")
            //??????
            .setIcon(binding!!.communityApplyImage.drawable)
            .setPositiveButton("??????", DialogInterface.OnClickListener { dialogInterface, i -> realcommunityDelete() })
            .create();
        alertDialog.show();
    }

    fun realcommunityDelete()
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
                    conn = mysqlMinecraft.sql_connect()
                    mysqlMinecraft.comm_delete(conn, Data.getusername())
                    conn!!.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                bundle.putString("key", "123")
                msg.data = bundle
                deletehandler.sendMessage(msg)
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
                    conn = mysqlMinecraft.sql_connect()
                    data = mysqlMinecraft.apply_init(conn, Data.getusername(),obsClient)
                    conn!!.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                bundle.putString("key", "123")
                msg.data = bundle
                inithandler.sendMessage(msg)
            }
        }).start()
    }

    fun getImage()
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
                    conn = mysqlMinecraft.sql_connect()
                    newbitMap = mysqlMinecraft.commImage_download(conn, Data.getusername(),obsClient)
                    conn!!.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                bundle.putString("key", "123")
                msg.data = bundle
                Imagehandler.sendMessage(msg)
            }
        }).start()
    }

}