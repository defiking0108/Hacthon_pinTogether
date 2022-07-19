package org.topnetwork.pintogether.utils;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import org.topnetwork.pintogether.dialog.LoadingDialog;

import java.lang.ref.WeakReference;

public class ProgressHUD {
    private WeakReference<LoadingDialog> loadingDialog;

    public ProgressHUD(LoadingDialog dialog) {
        loadingDialog = new WeakReference<>(dialog);
    }

    /**
     * 初始化
     *
     * @param activity
     * @return
     */
    public static ProgressHUD show(FragmentActivity activity, String msg) {
        Bundle args = new Bundle();
        LogUtils.eTag("ProgressHUD","show->"+ activity.toString());
        args.putString(LoadingDialog.KEY_MSG, msg);
        LoadingDialog dialog = LoadingDialog.show(activity.getSupportFragmentManager(), LoadingDialog.class, args);
        return new ProgressHUD(dialog);
    }

    /**
     * 初始化
     *
     * @param activity
     * @return
     */
    public static ProgressHUD show(FragmentActivity activity, String msg, boolean canCancel) {
        Bundle args = new Bundle();
        args.putString(LoadingDialog.KEY_MSG, msg);
        args.putBoolean(LoadingDialog.KEY_CAN_CANCEL, canCancel);
        LoadingDialog dialog = LoadingDialog.show(activity.getSupportFragmentManager(), LoadingDialog.class, args);
        LogUtils.eTag("ProgressHUD","show->"+ activity.toString());
        return new ProgressHUD(dialog);
    }

    //销毁
    public void dismiss() {
        LogUtils.eTag("ProgressHUD","dismiss->");
        try {
            if (loadingDialog != null) {
                LoadingDialog dialog = loadingDialog.get();
                if (dialog != null) {
                    dialog.dismissAllowingStateLoss();
                    dialog.dismissAllowingStateLoss();
                }
                loadingDialog.clear();
                loadingDialog = null;
            }
        } catch (Exception e) {
            LogUtils.eTag("ProgressHUD", e.getMessage());
        }
    }
}

