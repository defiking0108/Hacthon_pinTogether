package org.topnetwork.pintogether.utils.image.transform;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * Created by lgc on 2019/6/20.
 */
public class GlideSelectRoundTransform extends BitmapTransformation {

    private float leftTop = 0f;
    private float rightTop = 0f;
    private float rightBottom = 0f;
    private float leftBottom = 0f;

    public GlideSelectRoundTransform(int leftTop, int rightTop, int rightBottom, int leftBottom) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        this.leftTop = leftTop * density + 0.5f;
        this.rightTop = rightTop * density + 0.5f;
        this.rightBottom = rightBottom * density + 0.5f;
        this.leftBottom = leftBottom * density + 0.5f;
    }

    private Bitmap roundCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;
        int bw = source.getWidth();
        int bh = source.getHeight();
        //由于有这个对象，可以这样的获取尺寸，方便对图片的操作，和对垃圾的回收
        Bitmap target = pool.get(bw, bh, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        Canvas canvas = new Canvas(target);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        RectF rect = new RectF(0, 0, bw, bh);
        float[] radii = {leftTop, leftTop, rightTop, rightTop, rightBottom, rightBottom, leftBottom, leftBottom};
        Path path = new Path();
        path.addRoundRect(rect, radii, Path.Direction.CW);
        canvas.drawPath(path, paint);
        return target;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return roundCrop(pool, toTransform);
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }
}