package org.topnetwork.pintogether.permission;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;


/**
 * Created by lgc on 2019/6/11.
 *
 * Permissions helper for {@link Activity}.
 */
public class ActivityPermissionHelper extends BaseFrameworkPermissionHelper<Activity> {

	protected ActivityPermissionHelper(@NonNull Activity host) {
		super(host);
	}

	@Override
	public FragmentManager getFragmentManager() {
		return getHost().getFragmentManager();
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
