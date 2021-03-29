package com.by.download

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel


/**
+--------------------------------------+
+ @author Catherine Liu
+--------------------------------------+
+ 2021/3/29 20:04
+--------------------------------------+
+ Des:更新版本vm
+--------------------------------------+
 */

class UpdateVersionViewModel :ViewModel() {

    /**
     * 是否能够下载
     */
    fun canDownloadState(ctx: Context): Boolean {
        try {
            val state = ctx.packageManager.getApplicationEnabledSetting(
                "com.android.providers.downloads"
            )
            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }
}