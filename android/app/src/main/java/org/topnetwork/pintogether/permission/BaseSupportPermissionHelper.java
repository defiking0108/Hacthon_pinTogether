package org.topnetwork.pintogether.permission;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

/**
 * Created by lgc on 2019/6/11.
 * <p>
 * Implementation of {@link PermissionHelper} for Support Library host classes.
 */
public abstract class BaseSupportPermissionHelper<T> extends PermissionHelper<T> {

    protected BaseSupportPermissionHelper(@NonNull T host) {
        super(host);
    }

    public abstract FragmentManager getSupportFragmentManager();

    @Override
    public void showRequestPermissionRational(@NonNull String rational, final int requestCode, @NonNull final String... perms) {
        // implement
//        CommonDialog.getInstance()
//                .setTitle(rational)
//                .setConfirm(StringUtils.getString(R.string.str_confirm))
//                .setCancel(StringUtils.getString(R.string.str_cancel))
//                .setConfirmClickListener(object -> {
//                    if (getHost() instanceof Fragment) {
//                        newInstance((Fragment) getHost()).directRequestPermissions(requestCode,
//                                perms);
//                    } else if (getHost() instanceof android.app.Fragment) {
//                        newInstance((android.app.Fragment) getHost()).directRequestPermissions
//                                (requestCode, perms);
//                    } else if (getHost() instanceof Activity) {
//                        newInstance((Activity) getHost()).directRequestPermissions(requestCode,
//                                perms);
//                    }
//                    return null;
//                })
//                .setCancelClickListener(() -> {
////                    if (comDialog.getParentFragment() != null && comDialog.getParentFragment() instanceof
////                            PermissionChecker.PermissionCallbacks) {
////                        ((PermissionChecker.PermissionCallbacks) comDialog.getParentFragment())
////                                .onPermissionsDenied(requestCode, Arrays.asList(perms));
////                    } else
//                    if (getHost() instanceof PermissionChecker.PermissionCallbacks) {
//                        ((PermissionChecker.PermissionCallbacks) getHost()).onPermissionsDenied(requestCode,
//                                Arrays.asList(perms));
//                    }
//                    return null;
//                })
//                .show(getSupportFragmentManager());
//    }
    }

}
