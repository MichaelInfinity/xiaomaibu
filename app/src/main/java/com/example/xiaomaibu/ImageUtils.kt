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
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.format.DateFormat
import android.util.Log
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import java.io.File
import java.io.IOException
import java.util.*

object ImageUtils {
    private const val TAG = "Test"
    var tempFile: File? = null
    fun getImageUri(content: Context): Uri {
        val file = setTempFile(content)
        try {
            file!!.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return if (Build.VERSION.SDK_INT >= 24) {
            //将File对象转换成封装过的Uri对象，这个Uri对象标志着照片的真实路径
            FileProvider.getUriForFile(content, "com.example.xiaomaibu.fileprovider", file!!)
        } else {
            //将File对象转换成Uri对象，这个Uri对象标志着照片的真实路径
            Uri.fromFile(file)
        }
    }

    fun setTempFile(content: Context): File? {
        //自定义图片名称
        val name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)).toString() + ".png"
        Log.i(TAG, " name : $name")
        //定义图片存放的位置
        tempFile = File(content.externalCacheDir, name)
        Log.i(TAG, " tempFile : " + tempFile)
        return tempFile
    }
}