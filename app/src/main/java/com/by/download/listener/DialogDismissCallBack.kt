package com.yf.accelerator.update.listener


/**
+--------------------------------------+
+ @author Catherine Liu
+--------------------------------------+
+ 2021/4/9 13:38
+--------------------------------------+
+ Des:对话框消失回调
+--------------------------------------+
 */

interface DialogDismissCallBack {
    companion object {
        const val  dismissAllDialog: String = "dismissAllDialog"
        const val  installApp: String = "installApp"

        /**
         * 强制退出，回调给app处理退出应用
         */
        const val forceExit = "forceExit"

        /**
         * 点击立即更新，用来开启权限检查和下载服务
         */
        const val updateDownLoad = "updateDownLoad"

        /**
         * 重试按钮，进行重新下载
         */
        const val updateRetry = "updateRetry"

        /**
         * 若应用下载失败，可以选择去应用市场下载或者去浏览器下载
         */
        const val downFromBrowser = "downFromBrowser"

        /**
         * 取消更新
         */
        const val cancelUpdate = "cancelUpdate"

        /**
         * 重新安装
         */
        const val installApkAgain = "installApkAgain"
    }

    fun dismiss(dismissType: String)
}