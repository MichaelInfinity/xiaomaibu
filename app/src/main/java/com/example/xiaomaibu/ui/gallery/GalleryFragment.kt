package com.example.xiaomaibu.ui.gallery

import com.example.xiaomaibu.ui.home.HomeViewModel
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
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
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.view.View
import android.widget.FrameLayout
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import com.example.xiaomaibu.*
import com.example.xiaomaibu.databinding.FragmentGalleryBinding
import com.william.base_component.extension.logE
import com.william.base_component.extension.logV
import com.william.easykt.utils.CropParams
import com.william.easykt.utils.CropPhotoContract
import com.william.easykt.utils.SelectPhotoContract
import com.william.easykt.utils.TakePhotoContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.sql.Connection
import java.sql.SQLException

class GalleryFragment : Fragment() {
    private var galleryViewModel: GalleryViewModel? = null
    private var binding: FragmentGalleryBinding? = null
    var needCrop: Boolean=false
    var backOrimage: Boolean=true
    var nameIsEmpty:Boolean=true
    var introIsEmpty:Boolean=true
    var flag:Int=1
    var ImageBitmap:Bitmap?=null
    var BackGroundBitmap:Bitmap?=null

    var handler = Handler {
        if (flag == 1) Toast.makeText(activity, "上传成功", Toast.LENGTH_SHORT).show() else Toast.makeText(activity, "上传失败", Toast.LENGTH_SHORT).show()
        false
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)
        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding!!.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onResume() {
        super.onResume()
        binding!!.communityUploadImageButton.setOnClickListener {
            imageUpload()
        }
        binding!!.communityUploadBackgroundButton.setOnClickListener {
            backgroundUpload()
        }
        binding!!.communityUploadName.setOnClickListener {
            if(nameIsEmpty) {
                binding!!.communityUploadName.setText("")
                nameIsEmpty=false
            }
        }
        binding!!.communityUploadIntro.setOnClickListener {
            if(introIsEmpty){
                binding!!.communityUploadIntro.setText("")
                introIsEmpty=false
            }
        }
        binding!!.communityUploadButton.setOnClickListener {
            communityUpload()
        }
    }

    fun imageUpload(){
        needCrop=true
        backOrimage=false
        selectPhoto.launch(null)
    }

    fun backgroundUpload(){
        needCrop=false
        backOrimage=true
        selectPhoto.launch(null)
    }

    fun communityUpload()
    {
        val mysqlMinecraft = mysql_minecraft()
        val msg = Message.obtain()
        msg.what = 0
        val bundle = Bundle()
        val obsbug= ObsBug()
        val obsClient=obsbug.connect_obsClient()
        Thread(object : Runnable {
            var conn: Connection? = null
            override fun run() {
                try {
                    conn = mysqlMinecraft.sql_connect()
                    flag = mysqlMinecraft.newCommUpload(conn, binding!!.communityUploadName.text.toString(),binding!!.communityUploadIntro.text.toString(),ImageBitmap!!,BackGroundBitmap!!,obsClient)
                    conn!!.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                    flag = 0
                }
                bundle.putString("key", "我是一笑消息")
                msg.data = bundle
                handler.sendMessage(msg)
            }
        }).start()
    }

    val cropPhoto = registerForActivityResult(CropPhotoContract()) { uri: Uri? ->
        if (uri != null) {
            val bitmap = BitmapFactory.decodeStream(this.requireActivity().contentResolver.openInputStream(uri!!))
            if(!backOrimage) {
                ImageBitmap = bitmap
                binding!!.communityUploadImage.setImageBitmap(bitmap)
            }
            else {
                BackGroundBitmap = bitmap
                binding!!.backgroundImageCommunity.setImageBitmap(bitmap)
            }
        }
    }
    val selectPhoto = registerForActivityResult(SelectPhotoContract()) { uri: Uri? ->
        if (uri != null) {
            if (needCrop) {
                lifecycleScope.launch {
                    System.out.println(uri.toString())
                    val newUri = saveImageToCache(requireContext(), uri)
                    // 剪裁图片
                    kotlin.runCatching {
                        cropPhoto.launch(CropParams(newUri))
                    }.onFailure {
                        "crop failed: $it".logE()
                    }
                }
            } else {
                val bitmap = BitmapFactory.decodeStream(this.requireActivity().contentResolver.openInputStream(uri))
                BackGroundBitmap = bitmap
                binding!!.backgroundImageCommunity.setImageBitmap(bitmap)
            }
        } else {
            //"您没有选择任何图片".toast()
        }
    }
    val takePhoto =
        registerForActivityResult(TakePhotoContract()) { uri: Uri? ->
            if (uri != null) {
                if (needCrop) {
                    cropPhoto.launch(CropParams(uri))
                } else {
                    binding!!.communityUploadImage.setImageURI(uri)
                }
            }
        }



    private suspend fun saveImageToCache(context: Context, uri: Uri): Uri {
        val imageName = "${System.currentTimeMillis()}.jpg"
        val parent = if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            context.externalCacheDir?.absolutePath
        } else {
            context.cacheDir?.absolutePath
        }
        val path = parent + File.separator + imageName

        withContext(Dispatchers.IO) {
            copyInputStream(context, uri, path)
        }

        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                context, "${context.packageName}.fileProvider",
                File(parent, imageName)
            )
        } else {
            Uri.fromFile(File(path))
        }
        "uri: $result".logV()
        return result
    }

    /**
     * 字节流读写复制文件
     * @param context 上下文
     * @param uri 图片uri
     * @param outputPath 输出地址
     */
    private fun copyInputStream(context: Context, uri: Uri, outputPath: String) {
        "copy file begin...".logV()
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            outputStream = FileOutputStream(outputPath)
            val bytes = ByteArray(1024)
            var num: Int
            while (inputStream?.read(bytes).also { num = it ?: -1 } != -1) {
                outputStream.write(bytes, 0, num)
                outputStream.flush()
            }
        } catch (e: Exception) {
            "exception: $e".logE()
        } finally {
            try {
                outputStream?.close()
                inputStream?.close()
                "copy file end...".logV()
            } catch (e: IOException) {
                "exception: $e".logE()
            }
        }
    }
}