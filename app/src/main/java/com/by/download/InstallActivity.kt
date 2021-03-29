package com.by.download

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.by.download.Constants.Companion.START_INSTALL_FILE_PATH
import java.io.File


/**
+--------------------------------------+
+ @author Catherine Liu
+--------------------------------------+
+ 2021/3/29 20:30
+--------------------------------------+
+ Des:安装界面
+--------------------------------------+
 */

class InstallActivity : Activity() {
    private val TAG = InstallActivity::class.java.simpleName
    private val INSTALL_PACKAGES_REQUESTCODE = 10001
    private var mFilePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFilePath = intent.getStringExtra(START_INSTALL_FILE_PATH)
        requestInstallPermission()
    }


    fun requestInstallPermission() {
        DialogUtil().showConfirmDialog(this, getString(R.string.permission_install_content_pro),
            DialogInterface.OnClickListener { dialogInterface, i ->
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                startActivityForResult(intent, INSTALL_PACKAGES_REQUESTCODE)
            }, DialogInterface.OnClickListener { dialogInterface, i -> finish() })
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == INSTALL_PACKAGES_REQUESTCODE) {
            if (packageManager.canRequestPackageInstalls()) {
                println("InstallActivity openApkFile:${File(mFilePath)} ")
                DownloadUtils(this,"","").openApkFile(File(mFilePath))
            } else {
                println(getString(R.string.permission_install_failed_pro));
            }
            finish()
        } else {
            finish()
        }
    }
}