package com.by.download

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
            val downloadStr="http://newupdate.feihuo.com/feihuojiasuqi/app-alpha-release_110_jiagu_update_0_sign.apk"
            startDown(UpdateBean(true, downloadStr, false))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val updateVersionViewModel by lazy { ViewModelProvider(this).get(UpdateVersionViewModel::class.java) }
    private var mDownloadUtils: DownloadUtils? = null

    private var mDownloadManager: DownloadManager? = null
    private var mDownloadId: Long = 0

    /**
     * 开始下载
     *
     * @param updateDto
     */
    private fun startDown(updateBean: UpdateBean) {
        println("===================startDown")
        if (updateVersionViewModel.canDownloadState(this)) {
            mDownloadUtils = DownloadUtils(
                this, "",
                "CheckUpdate"
            )
            mDownloadManager = mDownloadUtils!!.getDownloadManager()
            mDownloadId = mDownloadUtils!!.getDownloadId()
            startQuery(updateBean)
        } else {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(updateBean.downloadFile)
            startActivity(intent)
//            if (updateDto.getMandatory() === 1) { //强更
//                mActivity.finish()
//            }
        }
    }


    //更新下载进度
    private fun startQuery(updateBean: UpdateBean) {
        if (mDownloadId != 0L) {
            showDownloadProgressDialog(updateBean)
            mHandler.post(mQueryProgressRunnable)
        }
    }


    fun showDownloadProgressDialog(updateBean: UpdateBean) {
        //展示进图框
        println("showDownloadProgressDialog $updateBean")

    }

    private val mQueryProgressRunnable: QueryRunnable = QueryRunnable()

    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == UpdateManager.PROGRESS_FLAG) {
                //TODO 更新进度
                println("===========handleMessage:"+msg.arg1.toString() + "%")
                if (msg.arg1 == 100) {
                    stopQuery()
                    //TOdo 进度框显示的话，需要dismiss
                }
            }
        }
    }


    //停止查询下载进度
    private fun stopQuery() {
        mHandler.removeCallbacks(mQueryProgressRunnable)
    }

    //查询下载进度
    private inner class QueryRunnable : Runnable {
        override fun run() {
            queryState()
            mHandler.postDelayed(mQueryProgressRunnable, 100)
        }
    }

    //查询下载进度
    private fun queryState() {
        // 通过ID向下载管理查询下载情况，返回一个cursor
        val c = mDownloadManager!!.query(DownloadManager.Query().setFilterById(mDownloadId))
        if (c == null) {
            showErrorToast()
        } else { // 以下是从游标中进行信息提取
            if (!c.moveToFirst()) {
                showErrorToast()
                if (!c.isClosed) {
                    c.close()
                }
                return
            }
            val mDownload_so_far =
                c.getFloat(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
            val mDownload_all =
                c.getFloat(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
            val msg = Message.obtain()
            if (mDownload_all > 0) {
                msg.what = UpdateManager.PROGRESS_FLAG
                var progressStr = 0f
                if (mDownload_all != 0f) {
                    progressStr = mDownload_so_far * 100 / mDownload_all
                }
                msg.arg1 = progressStr.roundToInt()
                mHandler.sendMessage(msg)
            }
            if (!c.isClosed) {
                c.close()
            }
        }
    }


    private fun showErrorToast() {
        Toast.makeText(this, "下载失败", Toast.LENGTH_SHORT).show()
    }
}