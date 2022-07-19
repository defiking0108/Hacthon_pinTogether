package org.topnetwork.pintogether.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.View;

import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;

import org.topnetwork.pintogether.base.NormalBaseConfig;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SharePicUtils {

    public static String rootFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/";


    /**
     * 得到view的cache bitmap
     *
     * @param view
     * @param width 宽度
     * @return
     */
    public static Bitmap getCacheBitmapFromView(View view, int width) {
        view.setDrawingCacheEnabled(true);//设置能否缓存图片信息
        view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),

                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY));

        view.layout(0, 0, width, width);


        view.buildDrawingCache();
        Bitmap newBitmap;
        try {

            Bitmap bitmap = view.getDrawingCache();
            if (bitmap == null) return null;
            newBitmap = Bitmap.createBitmap(bitmap);

        } catch (OutOfMemoryError e) {
            return null;
        }

        Canvas c = new Canvas(newBitmap);

        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */

        view.draw(c);

        view.setDrawingCacheEnabled(false);//设置能否缓存图片信息

        view.destroyDrawingCache();
        return newBitmap;
    }

    /**
     * 得到view的cache bitmap
     *
     * @param view
     * @param isFullScreen 是否是全屏
     * @return
     */
    public static Bitmap getCacheBitmapFromView(View view, boolean isFullScreen) {
        view.setDrawingCacheEnabled(true);//设置能否缓存图片信息
        if (isFullScreen) {
            view.measure(View.MeasureSpec.makeMeasureSpec(UIUtils.getScreenWidth(NormalBaseConfig.getContext()), View.MeasureSpec.EXACTLY),

                    View.MeasureSpec.makeMeasureSpec(UIUtils.getScreenHeight(NormalBaseConfig.getContext()), View.MeasureSpec.EXACTLY));

            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        view.buildDrawingCache();
        Bitmap newBitmap;
        try {

            Bitmap bitmap = view.getDrawingCache();
            if (bitmap == null) return null;
            newBitmap = Bitmap.createBitmap(bitmap);

        } catch (OutOfMemoryError e) {
            return null;
        }

        Canvas c = new Canvas(newBitmap);

        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */

        view.draw(c);

        view.setDrawingCacheEnabled(false);//设置能否缓存图片信息

        view.destroyDrawingCache();
        return newBitmap;
    }

    /**
     * View截图
     */
    public static Bitmap shotView(View view, int width, int height) {
        Bitmap newBitmap;
        try {
            newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        } catch (OutOfMemoryError e) {
            return null;
        }

        Canvas c = new Canvas(newBitmap);
        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */
        if (view instanceof NestedScrollView) {
            ((NestedScrollView) view).getChildAt(0).draw(c);
        } else {
            view.draw(c);
        }
        return newBitmap;
    }


    /**
     * 保存bitmap
     *
     * @param path   图片路径
     * @param bitmap 大位图
     * @throws IOException
     */
    public static void savePic(String path, Bitmap bitmap) throws IOException {
        if (bitmap == null) return;
        //save qrcode
        File file = new File(path);
        if (file.exists())
            file.delete();

        BufferedOutputStream bos;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        //if we use CompressFormat.JPEG,bitmap will have black background
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        bos.flush();
        bos.close();
        bitmap.recycle();


    }

    /**
     * 保存图片到图库
     *
     * @param context
     * @param bmp
     */
    public static void saveImageToGallery(Context context, String path, Bitmap bmp) {
        // 首先保存图片
        File file = new File(path);
        if (file.exists())
            file.delete();

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        // 其次把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                    file.getAbsolutePath(), path, null);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        // 最后通知图库更新
//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                Uri.fromFile(new File(file.getPath()))));

//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        Uri contentUri = Uri.fromFile(file);
//        mediaScanIntent.setData(contentUri);
//        context.sendBroadcast(mediaScanIntent);
//
//                // 最后通知图库更新
//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                Uri.fromFile(new File(file.getPath()))));

        //发送广播通知系统图库刷新数据
        Uri uri = Uri.fromFile(file);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    }


    /**
     * 跳转系统分享
     *
     * @param activity 当前活动
     * @param savePath 图片路径
     */
    public static void SharePic(Activity activity, String savePath) {
        Uri imageUri;
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                File file = new File(savePath);
                imageUri = FileProvider.getUriForFile(activity, NormalBaseConfig.getContext().getPackageName() + ".fileprovider", file);
            } else {
                imageUri = Uri.fromFile(new File(savePath));
            }
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.setType("image/*");
            activity.startActivity(Intent.createChooser(shareIntent, ""));
        } catch (Exception e) {
            e.getMessage();
        }

    }

}

