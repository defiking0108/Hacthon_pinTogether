package org.topnetwork.pintogether.adapter


import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import org.topnetwork.pintogether.R
import org.topnetwork.pintogether.model.LocationSearch

class SignInLocationAdapter : BaseQuickAdapter<LocationSearch, BaseViewHolder>(R.layout.item_sign_in_location){
    override fun convert(helper: BaseViewHolder, item: LocationSearch?) {
        item?.run {
              helper.setText(R.id.tv_name,name)
              helper.setText(R.id.tv_name1,address)
              if(isSelect){
                  helper.setVisible(R.id.iv_select,true)
              }else{
                  helper.setVisible(R.id.iv_select,false)
              }

//            val objectMapper = ObjectMapper()
//            LogUtils.eTag("SignInLocationAdapter", objectMapper.writeValueAsString(this))

        }
    }
}