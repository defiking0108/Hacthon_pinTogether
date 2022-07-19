package org.topnetwork.pintogether.permission;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;


/**
 * Created by lgc on 2019/6/11.
 * <p>
 * Permissions helper for {@link AppCompatActivity}.
 */
class AppCompatActivityPermissionHelper extends BaseSupportPermissionHelper<AppCompatActivity> {


    protected AppCompatActivityPermissionHelper(@NonNull AppCompatActivity host) {
        super(host);
    }

    @Override
    public FragmentManager getSupportFragmentManager() {
        return getHost().getSupportFragmentManager();
    }

    @Override
    public void directRequestPermissions(int requestCode, @NonNull String... perms) {
        ActivityCompat.requestPermissions(getHost(), perms, requestCode);
    }

    @Override
    public boolean shouldShowRequestPermissionRational(@NonNull String perm) {
        return ActivityCompat.shouldShowRequestPermissionRationale(getHost(), perm);
    }

    @Override
    public Context getContext() {
        return getHost();
    }
}
