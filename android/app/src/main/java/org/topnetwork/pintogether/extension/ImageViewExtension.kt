package org.topnetwork.pintogether.extension

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import org.topnetwork.pintogether.R
import org.topnetwork.pintogether.base.NormalBaseConfig
import org.topnetwork.pintogether.utils.LogUtils
import org.topnetwork.pintogether.utils.StringUtils
import org.topnetwork.pintogether.utils.image.transform.GlideCircleTransform
import org.topnetwork.pintogether.utils.image.transform.RoundTransform


fun ImageView.loadImage(@DrawableRes resId:Int){
    val options = RequestOptions()
        .centerInside()
        .placeholder(resId)
        .skipMemoryCache(false)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .error(resId)
    Glide.with(NormalBaseConfig.getContext())
        .load(resId)
        .apply(options)
        .into(this)
}

fun ImageView.loadCircleImage(url: String?) {
    val options = RequestOptions()
        .centerInside()
        .placeholder(R.drawable.bg_shape_oval_e1e1e1)
        .error(R.drawable.bg_shape_oval_e1e1e1)
        .transform(GlideCircleTransform())
    Glide.with(NormalBaseConfig.getContext())
        .load(url)
        .apply(options)
        .into(this)
}

fun ImageView.loadCircleImage(url: String?,placeholderRes:Int) {
    val options = RequestOptions()
        .centerInside()
        .placeholder(placeholderRes)
        .error(placeholderRes)
        .transform(GlideCircleTransform())
    Glide.with(NormalBaseConfig.getContext())
        .load(url)
        .apply(options)
        .into(this)
}

fun ImageView.loadImage(url: String?) {
    if(StringUtils.isEmpty(url))return
    LogUtils.dTag("iconUrl", url)
    val options = RequestOptions()
        .centerInside()
        .placeholder(R.drawable.bg_shape_oval_e1e1e1)
        .error(R.drawable.bg_shape_oval_e1e1e1)
        .skipMemoryCache(false)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
    Glide.with(NormalBaseConfig.getContext())
        .load(url)
        .apply(options)
        .into(this)
}

fun ImageView.loadImage(url: String?,placeholderRes:Int?) {
    if(StringUtils.isEmpty(url))return
    LogUtils.dTag("iconUrl", url)
    val options:RequestOptions
    if(StringUtils.isEmpty(placeholderRes)){
        options = RequestOptions()
            .centerInside()
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
    }else{
       options = RequestOptions()
            .centerInside()
            .placeholder(placeholderRes!!)
            .error(placeholderRes)
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
    }

    Glide.with(NormalBaseConfig.getContext())
        .load(url)
        .apply(options)
        .into(this)
}

fun ImageView.loadImage(@DrawableRes resId:Int,url:String){
    LogUtils.dTag("iconUrl", url)
    val options: RequestOptions = RequestOptions()
        .centerInside()
        .placeholder(R.drawable.bg_shape_oval_e1e1e1)
        .error(R.drawable.bg_shape_oval_e1e1e1)
        .skipMemoryCache(false)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
    Glide.with(NormalBaseConfig.getContext())
        .load(url)
        .apply(options)
        .into(this)
}

fun ImageView.loadImage(@DrawableRes resId:Int,url:String,placeholderRes:Int){
    LogUtils.dTag("iconUrl", url)
    val options = RequestOptions()
        .centerInside()
        .placeholder(placeholderRes)
        .error(placeholderRes)
        .skipMemoryCache(false)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
    Glide.with(NormalBaseConfig.getContext())
        .load(url)
        .apply(options)
        .into(this)
}

fun ImageView.loadImage(url: String?, placeholderRes: Int) {
    LogUtils.dTag("iconUrl", url)
    val options = RequestOptions()
        .centerInside()
        .placeholder(placeholderRes)
        .error(placeholderRes)
        .skipMemoryCache(false)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
    Glide.with(NormalBaseConfig.getContext())
        .load(url)
        .apply(options)
        .into(this)
}

fun ImageView.loadRoundImage(url: String?, radius: Int, placeholderRes: Int) {
    val options = RequestOptions()
        .placeholder(placeholderRes)
        .error(placeholderRes)
        .skipMemoryCache(false)
        .override(this.width, this.height)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .transform(RoundTransform(radius))
    Glide.with(NormalBaseConfig.getContext())
        .load(url)
        .apply(options)
        .into(this)
}




