package com.by.download

import android.app.Activity
import android.content.DialogInterface
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog


/**
+--------------------------------------+
+ @author Catherine Liu
+--------------------------------------+
+ 2021/3/29 20:41
+--------------------------------------+
+ Des: dialog 封装
+--------------------------------------+
 */

class DialogUtil {
    /**
     * 显示弹框
     */
    fun showConfirmDialog(
        activity: Activity,
        content: String?,
        listener: DialogInterface.OnClickListener?
    ): AlertDialog? {
        return showConfirmDialog(activity, content, listener, null)
    }

    fun showConfirmDialog(
        activity: Activity,
        content: String?,
        listener: DialogInterface.OnClickListener?,
        cancelListener: DialogInterface.OnClickListener?
    ): AlertDialog? {
        return showConfirmDialog(
            activity,
            null,
            content,
            "好的",
            "取消",
            listener,
            cancelListener
        )
    }

    fun showConfirmDialog(
        activity: Activity,
        title: String?,
        content: String?,
        listener: DialogInterface.OnClickListener?
    ) {
        showConfirmDialog(
            activity,
            title,
            content,
            "好的",
            "取消",
            listener,
            null
        )
    }

    fun showConfirmDialog(
        activity: Activity,
        title: String?,
        content: String?,
        okStr: String?,
        listener: DialogInterface.OnClickListener?
    ): AlertDialog? {
        return showConfirmDialog(
            activity,
            title,
            content,
            okStr,
            "取消",
            listener,
            null
        )
    }


    fun showConfirmDialog(
        activity: Activity?, title: String?, content: String?, okStr: String?, cancelStr: String?,
        listener: DialogInterface.OnClickListener?, cancelListener: DialogInterface.OnClickListener?
    ): AlertDialog? {
        return showConfirmDialog(
            activity,
            title,
            content,
            okStr,
            cancelStr,
            listener,
            cancelListener,
            null
        )
    }

    fun showConfirmDialog(
        activity: Activity?,
        title: String?,
        content: String?,
        okStr: String?,
        cancelStr: String?,
        listener: DialogInterface.OnClickListener?,
        cancelListener: DialogInterface.OnClickListener?,
        dismissListener: DialogInterface.OnDismissListener?
    ): AlertDialog? {
        val builder = AlertDialog.Builder(
            activity!!
        )
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        builder.setMessage(content)
        if (!TextUtils.isEmpty(okStr)) {
            builder.setPositiveButton(okStr, listener)
        }
        if (!TextUtils.isEmpty(cancelStr)) {
            builder.setNegativeButton(cancelStr, cancelListener)
        }
        builder.setOnDismissListener(dismissListener)
        return builder.show()
    }
}