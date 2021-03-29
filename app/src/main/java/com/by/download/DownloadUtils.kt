package com.by.download

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import com.by.download.Constants.Companion.START_INSTALL_FILE_PATH
import java.io.File


/**
+--------------------------------------+
+ @author Catherine Liu
+--------------------------------------+
+ 2021/3/29 20:09
+--------------------------------------+
+ Des:下载工具类
+--------------------------------------+
 */

class DownloadUtils(
    private val context: Context,
    private val url: String,
    private val name: String
) {
    private val ANDROID_PACKAGE_TYPE = "application/vnd.android.package-archive"

    //下载器
    private var downloadManager: DownloadManager? = null
    private val mContext: Context? = null

    //下载的ID
    private var downloadId: Long = 0
    private var mFile: File? = null
    private var mRequest: DownloadManager.Request? = null


    //下载apk
    private fun downloadAPK(url: String, name: String) {

        //TODO 上一次的下载id
        val lastVersionCode =0L
        if (lastVersionCode > 0) {
            if (downloadManager == null) {
                downloadManager =
                    mContext!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            }
            val query = DownloadManager.Query()
            //通过下载的id查找
            query.setFilterById(lastVersionCode)
            val cursor = downloadManager!!.query(query)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val lastModifiedTime =
                        cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP))
                    if (status == DownloadManager.STATUS_SUCCESSFUL && System.currentTimeMillis() - lastModifiedTime < 60 * 60 * 1000) {
                        //最近1小时内，成功下载的文件
                        val fileName: String = MD5Util.encrypt(url)
                        mFile = File(
                            mContext!!.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                            "$fileName.apk"
                        )
                        if (mFile!!.exists()) {
                            //TODO 打开安装包
                            openApkFile(mFile)
                            return
                        }
                        downloadManager?.remove(lastVersionCode)
                        //TODO 删除当前的下载ID
                    } else if (status == DownloadManager.STATUS_RUNNING) {
                        downloadId = lastVersionCode
                        val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED)
                        //注册广播接收者，监听下载状态
                        mContext!!.registerReceiver(receiver, intentFilter)
                        cursor.close()
                        return
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        downloadManager?.remove(lastVersionCode)
                        //TODO 删除当前的下载ID
                    }
                }
                cursor.close()
            }
        }
        try {
            createDownloadTask(url, name)
            val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED)
            //注册广播接收者，监听下载状态
            mContext!!.registerReceiver(receiver, intentFilter)
        } catch (e: Exception) {
            e.printStackTrace()

            //11
        }
    }

    //广播监听下载的各个状态
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            checkStatus()
        }
    }

    //检查下载状态
    private fun checkStatus() {
        val query = DownloadManager.Query()
        //通过下载的id查找
        query.setFilterById(downloadId)
        val cursor = downloadManager!!.query(query)
        if (cursor.moveToFirst()) {
            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            val downSize =
                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
            println("状态:$status\t下载的大小:$downSize")
            when (status) {
                DownloadManager.STATUS_PAUSED -> {
                }
                DownloadManager.STATUS_PENDING -> {
                }
                DownloadManager.STATUS_RUNNING ->     println("下载的大小:$downSize")
                DownloadManager.STATUS_SUCCESSFUL -> {
                    //下载完成安装APK
                    openApkFile(mFile)
                    mContext!!.unregisterReceiver(receiver)
                    cursor.close()
                }
                DownloadManager.STATUS_FAILED -> {
                    println("下载失败=====")
                    cursor.close()
                    mContext!!.unregisterReceiver(receiver)
                }
                else -> {
                }
            }
        }
    }

    /**
     * 创建下载任务
     *
     * @param url
     * @param name
     */
    private fun createDownloadTask(url: String, name: String) {
        //创建下载任务
        mRequest = DownloadManager.Request(Uri.parse(url))
        //移动网络情况下是否允许漫游
        mRequest?.setAllowedOverRoaming(false)
        //在通知栏中显示，默认就是显示的
        mRequest?.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        mRequest?.setTitle(name)
        mRequest?.setDescription("正在下载更新中")
        mRequest?.setVisibleInDownloadsUi(true)
        mRequest?.setMimeType(ANDROID_PACKAGE_TYPE)

        //设置下载的路径
        val fileName: String = MD5Util.encrypt(url)
        mFile = File(
            mContext?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            "$fileName.apk"
        )
        if (mFile!!.exists()) {
            mFile!!.delete()
        }
        mRequest?.setDestinationUri(Uri.fromFile(mFile))
        //获取DownloadManager
        if (downloadManager == null) {
            downloadManager = mContext?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        }
        //将下载请求加入下载队列，加入下载队列后会给该任务返回一个long型的id，通过该id可以取消任务，重启任务、获取下载的文件等等
        if (downloadManager != null) {
            downloadId = downloadManager!!.enqueue(mRequest)
        }
        //TODO 存储当前的下载id
    }


    /**
     * 打开安装包
     *
     * @param file
     */
     fun openApkFile(file: File?) {
        if (file == null || !file.exists()) {
            //文件不存在，什么都不做，返回
            return
        }
        val packageManager: PackageManager =context.getPackageManager()
        if (packageManager != null) {
            val packageInfo = packageManager.getPackageArchiveInfo(
                file.absolutePath, 0
            )
            if (packageInfo != null) {
                val versionCode = packageInfo.versionCode
                //versionCode至少不为0，为0是可能没取到
                if (versionCode != 0 && versionCode <= BuildConfig.VERSION_CODE) {
                    //如果本地apk文件版本号小于等于此版本，不安装
                    return
                }
            }
        }
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        /* 调用getMIMEType()来取得MimeType */
        val type: String = ANDROID_PACKAGE_TYPE
        val uri: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val b: Boolean = context.packageManager.canRequestPackageInstalls()
            if (b) { //可以按照
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                val contentUri = FileProvider.getUriForFile(
                    context,
                    context.applicationContext.packageName.toString() +
                            ".fileprovider",
                    file
                )
                intent.setDataAndType(contentUri, type)
            } else { //不可以安装
                intent.setClass(context, InstallActivity::class.java)
                intent.putExtra(START_INSTALL_FILE_PATH, file.absolutePath)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                val contentUri = FileProvider.getUriForFile(
                    context,
                    context.applicationContext.packageName.toString() +
                            ".fileprovider",
                    file
                )
                intent.setDataAndType(contentUri, type)
            } else {
                uri = Uri.fromFile(file)
                intent.setDataAndType(uri, type)
            }
        }
        context.startActivity(intent)
    }
    fun getDownloadManager(): DownloadManager? {
        return downloadManager
    }

    fun getDownloadId(): Long {
        return downloadId
    }


}