package org.topnetwork.pintogether.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import org.topnetwork.pintogether.R
import org.topnetwork.pintogether.model.LocationSearch
class LocationSearchAdapter : BaseQuickAdapter<LocationSearch, BaseViewHolder>(R.layout.item_location_search){
    override fun convert(helper: BaseViewHolder, item: LocationSearch?) {
        item?.run {
            helper.setText(R.id.tv_name,name)
            helper.setText(R.id.tv_name1,address)
        }
    }
}