package com.by.download.view

import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.ActivityUtils
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.by.download.R

/**
+--------------------------------------+
+ @author Catherine Liu
+--------------------------------------+
+ 2021/3/8 21:54
+--------------------------------------+
+ Des:弹框基类
+--------------------------------------+
 */

open class BaseDialog(private val res: Int) :
    AppCompatDialogFragment() {
    lateinit var viewBinding: ViewDataBinding

    /**
     * 是否正在显示,防止在特殊情况下弹出多层
     */
    open var isShowing = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //全屏
        setStyle(STYLE_NO_TITLE, R.style.BaseDialogFragment)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val dialogWindow = dialog.window
            if (dialogWindow != null) {
                dialogWindow.setGravity(Gravity.CENTER)
                dialogWindow.statusBarColor = Color.parseColor("#00000000")
                dialogWindow.setLayout(getWidth(dialogWindow), ViewGroup.LayoutParams.MATCH_PARENT)
                //                dialogWindow.setBackgroundDrawable(new ColorDrawable(R.color.color_black_60));
                dialog.setCanceledOnTouchOutside(false)
                dialog.setCancelable(false)
                dialogWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }

    /**
     * 获取屏幕的宽度
     *
     * @param dialogWindow Window
     * @return width
     */
    open fun getWidth(dialogWindow: Window): Int {
        val wm = dialogWindow.windowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.x
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (res == 0) {
            throw NullPointerException("请在getLayoutId()方法中传入布局Id")
        }
        viewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            res,
            null,
            false
        )
        return viewBinding.root
    }


    override fun show(manager: FragmentManager, tag: String?) {
        try {
            if (isShowing) {
                return
            }
            super.show(manager, tag)
            isShowing = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun dismiss() {
        try {
            // 不要使用super.dismiss()，会出现Can not perform this action after onSaveInstanceState异常
            // 当Activity被杀死或者按下Home回调用系统的onSaveInstance(),保存状态后，如果再次执行dismiss()会报错
            super.dismissAllowingStateLoss()
            isShowing = false
            dismissListener?.run {
                this.dismiss()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun show(tag: String) {
        ActivityUtils.getTopActivity()?.run {
            if (this is FragmentActivity) {
                show(this.supportFragmentManager, tag)
            }
        }
    }

    private var dismissListener: DialogFragmentDismissListener? = null
    fun setDismissListener(listener: DialogFragmentDismissListener) {
        this.dismissListener = listener
    }
}

interface DialogFragmentDismissListener {
    fun dismiss()
}