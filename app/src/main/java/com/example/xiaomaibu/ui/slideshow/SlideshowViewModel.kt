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
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI

class SlideshowViewModel : ViewModel() {
    private val mText: MutableLiveData<String?>
    val text: LiveData<String?>
        get() = mText

    init {
        mText = MutableLiveData()
        mText.value = "This is slideshow fragment"
    }
}