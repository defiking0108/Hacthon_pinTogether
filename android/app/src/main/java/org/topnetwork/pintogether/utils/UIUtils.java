package org.topnetwork.pintogether.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.RequiresApi;

import org.topnetwork.pintogether.base.NormalBaseConfig;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

public class UIUtils {

    private static float sDensity;

    /**
     * dip转换成px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        return dip2px(dpValue);
    }

    public static int dip2px(float dpValue) {
        ensureDensity();
        return (int) (dpValue * sDensity + 0.5f);
    }

    /**
     * px转换成dip
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        return px2dip(pxValue);
    }

    public static int px2dip(float pxValue) {
        ensureDensity();
        return (int) (pxValue / sDensity + 0.5f);
    }

    public static int px2sp(float pxValue) {
        final float fontScale = NormalBaseConfig.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static float sp2px(float spValue) {
        final float fontScale = NormalBaseConfig.getContext().getResources().getDisplayMetrics().scaledDensity;
        return spValue * fontScale + 0.5f;
    }

    public static void toastMessage(String msg) {
        if (StringUtils.isEmpty(msg)) {
            return;
        }
        ToastUtil.toastMessage(NormalBaseConfig.getContext(), msg);
    }

    public static void toastMessagelong(String msg) {
        if (StringUtils.isEmpty(msg)) {
            return;
        }
        ToastUtil.toastMessagelong(NormalBaseConfig.getContext(), msg);
    }

    public static void toastMessage(Context context, String msg) {
        if (StringUtils.isEmpty(msg)) {
            return;
        }
        ToastUtil.toastMessage(context, msg);
    }


    private static void ensureDensity() {
        if (sDensity == 0) {
            sDensity = NormalBaseConfig.getContext().getResources().getDisplayMetrics().density;
        }
    }

    /**
     * 判断是否有刘海屏
     * 已适配华为/OPPO/小米
     */
    public static boolean hasNotchInScreen(Context context) {
        boolean retGoogle = false;
        boolean retHw = false;
        boolean retVivo = false;
        boolean retMi = false;
        boolean oppoHasNotch = context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
        try {
            retMi = getInt(context, "ro.miui.notch", 0) == 1;
            // Log.i("hasNotchInScreen", "xiaomi: " + getInt(context, "ro.miui.notch", 0));
        } catch (Exception e) {
            //Log.e("hasNotchInScreen", "hasNotchInScreen MiException");
        }
        ClassLoader cl = context.getClassLoader();
        try {
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method getHw = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            retHw = (boolean) getHw.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            //Log.e("hasNotchInScreen", "hasNotchInScreen ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            //Log.e("hasNotchInScreen", "hasNotchInScreen NoSuchMethodException");
        } catch (Exception e) {
            //Log.e("hasNotchInScreen", "hasNotchInScreen Exception");
        }
        try {
            Class FtFeature = cl.loadClass("com.util.FtFeature");
            Method getVivo = FtFeature.getMethod("isFeatureSupport", int.class);
            retVivo = (boolean) getVivo.invoke(FtFeature, 0x00000020);
        } catch (ClassNotFoundException e) {
            //Log.e("hasNotchInScreen", "hasNotchInScreen ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            //Log.e("hasNotchInScreen", "hasNotchInScreen NoSuchMethodException");
        } catch (Exception e) {
            //Log.e("hasNotchInScreen", "hasNotchInScreen Exception");
        }
        if (context instanceof Activity && Build.VERSION.SDK_INT >= 28) {
            Activity activity = (Activity) context;
            if (activity.getWindow().getDecorView().getRootWindowInsets() != null) {
                DisplayCutout cutout = activity.getWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
                if (cutout != null) {
                    Log.i("hasNotchInScreen", "hasNotchInScreen: " + cutout.getSafeInsetTop());
                    retGoogle = cutout.getSafeInsetTop() > 0;
                }
            }
        }
        Log.i("hasNotchInScreen", "hasNotchInScreen: " + (retHw || retVivo || oppoHasNotch || retMi || retGoogle));
        return retHw || retVivo || oppoHasNotch || retMi || retGoogle;
    }

    /**
     * 获取刘海屏高度(以最高刘海高度为准)
     * 已适配华为/OPPO/小米
     */
    public static int getNotchHeight(Context context) {
        return 89;
    }

    /**
     * 设置透明的状态栏及全屏
     *
     * @param context
     */
    public static void setTransparentStatusBar(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = ((Activity) context).getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static void clearToast() {
        ToastUtil.cancel();
    }

    private static class ToastUtil {

        private static Toast sToast;

        static void toastMessage(Context context, String msg) {
            toastMessage(context, msg, Toast.LENGTH_SHORT);
        }

        static void toastMessagelong(Context context, String msg) {
            toastMessage(context, msg, Toast.LENGTH_LONG);
        }

        static void toastMessage(Context context, int resId) {
            toastMessage(context, context.getString(resId), Toast.LENGTH_SHORT);
        }

        static void toastMessage(Context context, int resId, int time) {
            toastMessage(context, context.getString(resId), time);
        }

        static void toastMessage(Context context, String msg, int time) {
            if (sToast == null) {
                sToast = Toast.makeText(context, msg, time);
            } else {
                sToast.setText(msg);
            }
            if (sToast != null) {
                sToast.show();
                // LogUtils.dTag("yf", "show Toast..................");
            }
        }

        static void cancel() {
            if (sToast != null) {
                sToast.cancel();
                sToast = null;
            }
        }

    }

    /**
     * 根据给定的key返回int类型值.
     *
     * @param key 要查询的key
     * @param def 默认返回值
     * @return 返回一个int类型的值, 如果没有发现则返回默认值
     * @throws IllegalArgumentException 如果key超过32个字符则抛出该异常
     */
    public static Integer getInt(Context context, String key, int def) throws IllegalArgumentException {

        Integer ret = def;

        try {

            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");

            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = int.class;

            Method getInt = SystemProperties.getMethod("getInt", paramTypes);

            //参数
            Object[] params = new Object[2];
            params[0] = new String(key);
            params[1] = new Integer(def);

            ret = (Integer) getInt.invoke(SystemProperties, params);

        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            ret = def;
            //TODO
        }

        return ret;

    }

    /**
     * 获取屏幕的宽
     *
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取屏幕的高度
     *
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 是否在屏幕右侧
     *
     * @param mContext 上下文
     * @param xPos     位置的x坐标值
     * @return true：是。
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isInRight(Context mContext, int xPos) {
        return (xPos > getScreenWidth(mContext) / 2);
    }

    /**
     * 是否在屏幕左侧
     *
     * @param mContext 上下文
     * @param xPos     位置的x坐标值
     * @return true：是。
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isInLeft(Context mContext, int xPos) {
        return (xPos < getScreenWidth(mContext) / 2);
    }


    /**
     * @param str
     * @return
     */
    public static String emptyProtect(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        } else {
            return str;
        }
    }

    /**
     * 获取Android ID
     *
     * @return
     */
    public static String getDeviceId(Context context) {
        return Settings.System.getString(context.getContentResolver(),
                Settings.System.ANDROID_ID);
    }


    /**
     * 获取 serial
     *
     * @return
     */
    public static String getSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialnocustom");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     */
    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    //判断字节是否符合 超出15个汉字或30个字母
    public static boolean isAccordInputChar(String inputStr) {
        int orignLen = inputStr.length();
        int resultLen = 0;
        String temp = null;
        for (int i = 0; i < orignLen; i++) {
            temp = inputStr.substring(i, i + 1);
            try {// 3 bytes to indicate chinese word,1 byte to indicate english
                // word ,in utf-8 encode
                if (temp.getBytes("utf-8").length == 3) {
                    resultLen += 2;
                } else {
                    resultLen++;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (resultLen > 30) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }
}

