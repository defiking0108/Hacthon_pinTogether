package org.topnetwork.pintogether.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import org.topnetwork.pintogether.R
import org.topnetwork.pintogether.extension.loadImage
import org.topnetwork.pintogether.model.MeGetNft
import org.topnetwork.pintogether.model.NearBy

class GetNftAdapter : BaseQuickAdapter<NearBy, BaseViewHolder>(R.layout.item_get_nft){
    override fun convert(helper: BaseViewHolder, item: NearBy?) {
        item?.run {
            var picImageView = helper.getView<ImageView>(R.id.iv_pic)
            picImageView.loadImage(cid,R.drawable.ic_holder1)
            helper.setText(R.id.tv_name,name)
        }
    }
}