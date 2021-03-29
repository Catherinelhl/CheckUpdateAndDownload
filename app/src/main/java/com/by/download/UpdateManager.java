package com.by.download;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;

import org.jetbrains.annotations.NotNull;

/**
 * 检查更新
 */
public class UpdateManager {

    public static final int PROGRESS_FLAG = 1001;

    private Activity mActivity;
    boolean mShowTip;

    private Toast mToast;
    private DownloadUtils mDownloadUtils;

    private DownloadManager mDownloadManager;
    private long mDownloadId;

    private final QueryRunnable mQueryProgressRunnable = new QueryRunnable();
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == PROGRESS_FLAG) {
                    //TODO 更新进度
                    System.out.println(msg.arg1+"%");
                if (msg.arg1 == 100) {
                    stopQuery();
                    //TOdo 进度框显示的话，需要dismiss
                }
            }
        }
    };

    public void showDownloadProgressDialog(final UpdateBean updateVO) {
        System.out.println("showDownloadProgressDialog");
        //TODO 检查是否是强更
//        mUpdateProgressDialog = new UpdateProgressDialog(mActivity, R.style.dialog);
//        mUpdateProgressDialog.show();
//
//        mUpdateProgressDialog.setCanceledOnTouchOutside(updateVO.getMandatory() == 1 ? false :
//                true);
//        mUpdateProgressDialog.setCancelable(updateVO.getMandatory() == 1 ? false : true);
//        mUpdateProgressDialog.setOkClickListen(() -> {
//            mUpdateProgressDialog.dismiss();
//            if (updateVO.getMandatory() == 1) {//强更
//                mActivity.finish();
//            }
//            if (mDownloadUtils != null) {
//                mDownloadUtils.showNotification();
//            }
//            stopQuery();
//        });
    }


    //更新下载进度
    private void startQuery(UpdateBean updateDto) {
        if (mDownloadId != 0) {
            showDownloadProgressDialog(updateDto);
            mHandler.post(mQueryProgressRunnable);
        }
    }

    //停止查询下载进度
    private void stopQuery() {
        mHandler.removeCallbacks(mQueryProgressRunnable);
    }


    public void showErrorToast() {
        System.out.println("下载失败");
        //TODO 关闭更新狂
    }



    //查询下载进度
    private class QueryRunnable implements Runnable {
        @Override
        public void run() {
            queryState();
            mHandler.postDelayed(mQueryProgressRunnable, 100);
        }
    }

    //查询下载进度
    private void queryState() {
        // 通过ID向下载管理查询下载情况，返回一个cursor
        Cursor c = mDownloadManager.query(new DownloadManager.Query().setFilterById(mDownloadId));
        if (c == null) {
            showErrorToast();
        } else { // 以下是从游标中进行信息提取
            if (!c.moveToFirst()) {
                showErrorToast();
                if (!c.isClosed()) {
                    c.close();
                }
                return;
            }
            float mDownload_so_far =
                    c.getFloat(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            float mDownload_all =
                    c.getFloat(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            Message msg = Message.obtain();
            if (mDownload_all > 0) {
                msg.what = PROGRESS_FLAG;
                float progressStr = 0;
                if (mDownload_all != 0) {
                    progressStr = mDownload_so_far * 100 / mDownload_all;
                }
                msg.arg1 = Math.round(progressStr);
                mHandler.sendMessage(msg);
            }
            if (!c.isClosed()) {
                c.close();
            }
        }
    }


    public interface UpdateCallbackListen {
        void checkResult(UpdateBean updateVO);
    }

}
