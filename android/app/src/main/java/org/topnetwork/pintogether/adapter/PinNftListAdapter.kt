package org.topnetwork.pintogether.adapter

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import org.topnetwork.pintogether.R
import org.topnetwork.pintogether.extension.loadCircleImage
import org.topnetwork.pintogether.model.NearBy
import java.math.BigDecimal

class PinNftListAdapter : BaseQuickAdapter<NearBy, BaseViewHolder>(R.layout.item_pin_nft){
    override fun convert(helper: BaseViewHolder, item: NearBy?) {
        item?.run {
            helper.setText(R.id.tv_name,name)
            helper.setText(R.id.tv_location,distance + "M")
            val iconImageView: ImageView = helper.getView(R.id.iv_pic)
            iconImageView.loadCircleImage(cid,R.drawable.ic_holder2)
            var total1: String
            if(!isNumLimit){
                total1 = "∞"
            }else{
                total1 = BigDecimal(total).add(BigDecimal(num)).toPlainString()
            }
        
            var str = "已领/总量：$total/$total1"
            var spanned = SpannableString(str)
            spanned.setSpan(ForegroundColorSpan(Color.parseColor("#3665FD")), str.indexOf(total), str.indexOf(total) + total.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //设置前景色为洋红色
            helper.setText(R.id.tv_number,spanned )
        }
    }
}