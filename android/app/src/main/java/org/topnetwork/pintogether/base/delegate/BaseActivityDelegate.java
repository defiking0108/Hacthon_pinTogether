package org.topnetwork.pintogether.base.delegate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.topnetwork.pintogether.permission.PermissionChecker;
import org.topnetwork.pintogether.utils.ActivityUtils;
import org.topnetwork.pintogether.base.app.BaseActivity;
public class BaseActivityDelegate implements ActivityDelegate {
    private BaseActivity activity;

    public BaseActivityDelegate(BaseActivity baseActivity) {
        this.activity = baseActivity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ActivityUtils.push(activity);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        if (activity != null) {
            ActivityUtils.remove(activity);
        }

    }

    //动态权限请求之后的回调
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults, PermissionChecker.PermissionCallbacks callbacks) {
        // PermissionChecker handles the request result.
        PermissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults, callbacks);
    }
}

