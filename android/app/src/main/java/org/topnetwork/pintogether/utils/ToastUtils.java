package org.topnetwork.pintogether.utils;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import org.topnetwork.pintogether.R;
import org.topnetwork.pintogether.base.NormalBaseConfig;

import java.lang.reflect.Field;
public class ToastUtils {

    private static final int COLOR_DEFAULT = 0xFEFFFFFF;
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    private static int sGravity = -1;
    private static int sXOffset = -1;
    private static int sYOffset = -1;
    private static int sBgColor = COLOR_DEFAULT;
    private static int sBgResource = -1;
    private static int sMsgColor = COLOR_DEFAULT;

    private ToastUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化ToastUtils
     *
     * @param context
     */
    public static void init(Application context) {
        com.hjq.toast.ToastUtils.init(context);
    }


    /**
     * 设置吐司位置
     *
     * @param gravity 位置
     * @param xOffset x 偏移
     * @param yOffset y 偏移
     */
    public static void setGravity(final int gravity, final int xOffset, final int yOffset) {
        sGravity = gravity;
        sXOffset = xOffset;
        sYOffset = yOffset;
    }

    /**
     * 设置背景颜色
     *
     * @param backgroundColor 背景色
     */
    public static void setBgColor(@ColorInt final int backgroundColor) {
        sBgColor = backgroundColor;
    }

    /**
     * 设置背景资源
     *
     * @param bgResource 背景资源
     */
    public static void setBgResource(@DrawableRes final int bgResource) {
        sBgResource = bgResource;
    }

    /**
     * 设置消息颜色
     *
     * @param msgColor 颜色
     */
    public static void setMsgColor(@ColorInt final int msgColor) {
        sMsgColor = msgColor;
    }

    /**
     * 安全地显示短时吐司
     *
     * @param text 文本
     */
    public static void showShort(final String text) {
        showTop(text, Toast.LENGTH_SHORT);
//        show(text, Toast.LENGTH_SHORT);
    }

    /**
     * 安全地显示短时吐司
     *
     * @param resId 资源 Id
     */
    public static void showShort(@StringRes final int resId) {
        showTop(StringUtils.getString(resId), Toast.LENGTH_SHORT);
//        show(resId, Toast.LENGTH_SHORT);
    }

    /**
     * 安全地显示长时吐司
     *
     * @param text 文本
     */
    public static void showLong(@NonNull final CharSequence text) {
        showTop(text.toString(), Toast.LENGTH_LONG);
//        show(text, Toast.LENGTH_LONG);
    }

    /**
     * 安全地显示长时吐司
     *
     * @param resId 资源 Id
     */
    public static void showLong(@StringRes final int resId) {
        show(resId, Toast.LENGTH_LONG);
    }

    /**
     * 安全地显示长时吐司
     *
     * @param resId 资源 Id
     * @param args  参数
     */
    public static void showLong(@StringRes final int resId, final Object... args) {
        if (args != null && args.length == 0) {
            show(resId, Toast.LENGTH_SHORT);
        } else {
            show(resId, Toast.LENGTH_LONG, args);
        }
    }

    /**
     * 安全地显示长时吐司
     *
     * @param format 格式
     * @param args   参数
     */
    public static void showLong(final String format, final Object... args) {
        if (args != null && args.length == 0) {
            showTop(format, Toast.LENGTH_LONG);
            //show(format, Toast.LENGTH_SHORT);
        } else {
            show(format, Toast.LENGTH_LONG, args);
        }
    }

    /**
     * 取消吐司显示
     */
    public static void cancel() {
        // if (sToast != null) {
        // sToast.cancel();
        // sToast = null;
        // }
    }


    private static void show(@StringRes final int resId, final int duration) {
        show(StringUtils.getString(resId), duration);
    }

    private static void show(@StringRes final int resId, final int duration, final Object... args) {
        show(String.format(StringUtils.getString(resId), args), duration);
    }

    private static void show(final String format, final int duration, final Object... args) {
        show(String.format(format, args), duration);
    }

    // 自定义View
    private static View mCustomView;

    /**
     * 显示顶部Toast
     *
     * @param msg
     */
    public static void showTop(final String msg, final int duration) {
        customShow(msg, Gravity.FILL_HORIZONTAL | Gravity.TOP, null);

    }

    public static void showCenter(final String msg, final int duration, View view) {
        customShow(msg, Gravity.CENTER, view);

    }

    public static void customShow(final String msg, int gravity, View view) {
        HANDLER.post(() -> {
            //cancel();
            if (view != null) {
                com.hjq.toast.ToastUtils.setView(view);

            } else {
                if (mCustomView == null) {
                    mCustomView = View.inflate(NormalBaseConfig.getContext(), R.layout.view_white_custom_toast, null);
                    RelativeLayout relativeLayout = mCustomView.findViewById(R.id.rl);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ScreenUtils.getScreenWidth(), UIUtils.dip2px(58));
                    relativeLayout.setLayoutParams(layoutParams);
//                    TextView textView = mCustomView.findViewById(R.id.message);
//                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) textView.getLayoutParams();
//                    layoutParams.width = 900;

                }

//                IToast iToast = ToastCompat.makeText(NormalBaseConfig.getContext(), msg, Toast.LENGTH_LONG)
//                        .setGravity(Gravity.FILL_HORIZONTAL | Gravity.TOP, 0, 0)
//                        .setView(mCustomView)
//                        .setText(msg)
//                        .setDuration(duration);
//                iToast.show();

                com.hjq.toast.ToastUtils.setView(mCustomView);

            }
            com.hjq.toast.ToastUtils.setGravity(gravity, 0, 0);
            com.hjq.toast.ToastUtils.show(msg);
//            //为Toast设置动画
//            Field mTN = null;
//            try {
//                mTN = com.hjq.toast.ToastUtils.getToast().getClass().getDeclaredField("mTN");
//                mTN.setAccessible(true);
//                Object mTNObj = mTN.get(com.hjq.toast.ToastUtils.getToast());
//                Field mParams = null;
//                mParams = mTNObj.getClass().getDeclaredField("mParams");
//                WindowManager.LayoutParams params = (WindowManager.LayoutParams) mParams.get(mTNObj);
//                mParams.setAccessible(true);
//                params.windowAnimations = R.style.ToastAnim;//设置动画, 需要是style类型
//            } catch (NoSuchFieldException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
            com.hjq.toast.ToastUtils.getToast().getView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);//设置Toast可以布局到系统状态栏的下面

        });
    }

    private static View getView(@LayoutRes final int layoutId) {
        LayoutInflater inflate =
                (LayoutInflater) NormalBaseConfig.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflate != null ? inflate.inflate(layoutId, null) : null;
    }

    /**
     * @param bgColor
     * @param msgColor
     * @param gravity
     * @param xOffset
     * @param fOffset
     * @param content
     */
    public static void showCusBgMsgColorAndGravityToast(String bgColor
            , String msgColor, int gravity, int xOffset, int fOffset, String content) {
        setBgColor(Color.parseColor(bgColor));
        setMsgColor(Color.parseColor(msgColor));
        setGravity(gravity, xOffset, fOffset);
        showLong(content, NormalBaseConfig.getContext());
    }

    /**
     * 反射字段
     *
     * @param object    要反射的对象
     * @param fieldName 要反射的字段名称
     */
    private static Object getField(Object object, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        if (field != null) {
            field.setAccessible(true);
            return field.get(object);
        }
        return null;
    }


}

