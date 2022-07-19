package org.topnetwork.pintogether.permission;

import android.app.FragmentManager;

import androidx.annotation.NonNull;


/**
 * Created by lgc on 2019/6/11.
 */

public abstract class BaseFrameworkPermissionHelper<T> extends PermissionHelper<T> {

	protected BaseFrameworkPermissionHelper(@NonNull T host) {
		super(host);
	}

	public abstract FragmentManager getFragmentManager();

	@Override
	public void showRequestPermissionRational(@NonNull String rational, int requestCode, @NonNull String... perms) {

	}
}
