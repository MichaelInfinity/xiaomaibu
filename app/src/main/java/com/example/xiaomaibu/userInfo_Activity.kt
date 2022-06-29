package com.example.xiaomaibu

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.*
import androidx.core.app.ActivityCompat
import com.esafirm.imagepicker.model.Image
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.esafirm.imagepicker.features.*
import androidx.lifecycle.lifecycleScope
import com.coorchice.library.SuperTextView
import com.example.xiaomaibu.databinding.ActivityUserInfoBinding
import com.mikhaellopez.circularimageview.CircularImageView
import com.william.base_component.extension.bindingView
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
import com.example.xiaomaibu.Data

class userInfo_Activity : AppCompatActivity() {

    val viewBinding: ActivityUserInfoBinding by bindingView()
    var image1: ImageView? = null
    var username_text_info:SuperTextView? = null
    var email_text_info:SuperTextView?=null
    var community_image_info:CircularImageView?=null
    var community_name_info:SuperTextView?=null
    var career_info:SuperTextView?=null
    var sqlflag = 0
    private var needCrop = false
    var decodebitmap:Bitmap?=null

    var uploadhandler = Handler {
        if (sqlflag == 0) {
            Toast.makeText(this, "上传失败", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "上传成功", Toast.LENGTH_SHORT).show()
        }
        false
    }

    var updatehandler = Handler {
        if(decodebitmap!=null)
            image1!!.setImageBitmap(decodebitmap)
        false
    }

    //imageUri照片真实路径
    override fun onCreate(savedInstanceState: Bundle?) {
        val cameraPermissionCheck = ContextCompat.checkSelfPermission(this@userInfo_Activity, Manifest.permission.CAMERA)
        val readPermissionCheck = ContextCompat.checkSelfPermission(this@userInfo_Activity, Manifest.permission.READ_EXTERNAL_STORAGE)
        val writePermissionCheck = ContextCompat.checkSelfPermission(this@userInfo_Activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (cameraPermissionCheck != PackageManager.PERMISSION_GRANTED || readPermissionCheck != PackageManager.PERMISSION_GRANTED || writePermissionCheck != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(
                    this@userInfo_Activity,
                    permissions,
                    0)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        image1 = findViewById<View>(R.id.imageView3) as ImageView
        username_text_info=findViewById<View>(R.id.info_username) as SuperTextView
        email_text_info=findViewById<View>(R.id.info_email) as SuperTextView
        community_image_info=findViewById<View>(R.id.info_community_image) as CircularImageView
        community_name_info=findViewById<View>(R.id.info_community_name)as SuperTextView
        career_info=findViewById<View>(R.id.info_career) as SuperTextView
        supportActionBar!!.hide()
    }

    override fun onResume(){
        super.onResume()
        updateinfo()
    }

    fun updateinfo(){
        if(Data.getusername()!=null)
            username_text_info!!.setText(Data.getusername())
        if(Data.getmailbox()!=null)
            email_text_info!!.setText(Data.getmailbox())
        if(Data.getcareer()!=null)
            career_info!!.setText(Data.getcareer())
        if(Data.getcommunity()!=null)
            community_name_info!!.setText((Data.getcommunity()))
        val mysqlMinecraft = mysql_minecraft()
        val msg = Message.obtain()
        msg.what = 0
        val bundle = Bundle()
        var sql_username=username_text_info!!.text.toString()

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

    fun user_head_upload_button(view: View?)
    {
        needCrop = true
        selectPhoto.launch(null)
    }

    val cropPhoto = registerForActivityResult(CropPhotoContract()) { uri: Uri? ->
        if (uri != null) {
            image1!!.setImageURI(uri)
        }
        val bitmap = BitmapFactory.decodeStream(this.contentResolver.openInputStream(uri!!))
        val mysqlMinecraft = mysql_minecraft()
        val msg = Message.obtain()
        msg.what = 0
        val bundle = Bundle()
        var sql_username=username_text_info!!.text.toString()

        Thread(object : Runnable {
            var conn: Connection? = null
            override fun run() {
                try {
                    println("upload?")
                    conn = mysqlMinecraft.sql_connect()
                    sqlflag = mysqlMinecraft.image_upload(conn, sql_username, bitmap)
                    conn!!.close()
                } catch (e: SQLException) {
                    e.printStackTrace()
                }
                bundle.putString("key", "我是一笑消息")
                msg.data = bundle
                uploadhandler.sendMessage(msg)
            }
        }).start()
    }
    val selectPhoto = registerForActivityResult(SelectPhotoContract()) { uri: Uri? ->
        if (uri != null) {
            if (needCrop) {
                lifecycleScope.launch {
                    val newUri = saveImageToCache(this@userInfo_Activity, uri)
                    // 剪裁图片
                    kotlin.runCatching {
                        cropPhoto.launch(CropParams(newUri))
                    }.onFailure {
                        "crop failed: $it".logE()
                    }
                }
            } else {
                viewBinding.imageView3.setImageURI(uri)
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
                    viewBinding.imageView3.setImageURI(uri)
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
