package org.topnetwork.pintogether.permission;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


/**
 * Created by lgc on 2019/6/11.
 */

public class SupportFragmentPermissionHelper extends BaseSupportPermissionHelper<Fragment> {

	protected SupportFragmentPermissionHelper(@NonNull Fragment host) {
		super(host);
	}

	@Override
	public FragmentManager getSupportFragmentManager() {
		return getHost().getChildFragmentManager();
	}

	@Override
	public void directRequestPermissions(int requestCode, @NonNull String... perms) {
		getHost().requestPermissions(perms, requestCode);
	}

	@Override
	public boolean shouldShowRequestPermissionRational(@NonNull String perm) {
		return getHost().shouldShowRequestPermissionRationale(perm);
	}

	@Override
	public Context getContext() {
		return getHost().getActivity();
	}
}
