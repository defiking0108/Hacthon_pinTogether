package org.topnetwork.pintogether.base;

import android.annotation.SuppressLint;
import android.content.Context;

public class NormalBaseConfig {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;//上下文

    public static Context getContext() {
        return mContext;
    }

    public static void init(Context context) {
        mContext = context;
    }

}