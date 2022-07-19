package org.topnetwork.pintogether.utils.image.transform;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.security.MessageDigest;

/**
 * Created by lgc on 2019/6/20.
 */
public class RoundTransform extends CenterCrop {

    private float radius = 0f;

    public RoundTransform(int dp) {
        this.radius = Resources.getSystem().getDisplayMetrics().density * dp;
    }

    private Bitmap roundCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;
        int bw = source.getWidth();
        int bh = source.getHeight();

        //由于有这个对象，可以这样的获取尺寸，方便对图片的操作，和对垃圾的回收
        Bitmap target = pool.get(bw, bh, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        RectF f = new RectF(0, 0, bw, bh);
        Canvas canvas = new Canvas(target);
        canvas.drawRoundRect(f, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap transform = super.transform(pool, toTransform, outWidth, outHeight);
		return roundCrop(pool, transform);
    }

	@Override
	public void updateDiskCacheKey(MessageDigest messageDigest) {

	}

}