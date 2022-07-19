package org.topnetwork.pintogether.ui.activity.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.topnetwork.pintogether.*
import org.topnetwork.pintogether.base.app.ToolbarBaseActivity
import org.topnetwork.pintogether.databinding.ActivityGetNftDetailBinding
import org.topnetwork.pintogether.event.SignInLocationEvent
import org.topnetwork.pintogether.extension.loadImage
import org.topnetwork.pintogether.map.Location
import org.topnetwork.pintogether.model.NearBy
import org.topnetwork.pintogether.ui.activity.vm.GetNftDetailActivityVm
import org.topnetwork.pintogether.utils.CopyUtils
import org.topnetwork.pintogether.utils.LogUtils
import org.topnetwork.pintogether.utils.TimeUtils
import java.math.BigDecimal
import java.text.SimpleDateFormat

class GetNftDetailActivity :
    ToolbarBaseActivity<ActivityGetNftDetailBinding, GetNftDetailActivityVm>() {
    var id = ""
    var type = 0
    var nearBy:NearBy ?= null
    override val layoutResId: Int
        get() = R.layout.activity_get_nft_detail

    override fun createViewModel(): GetNftDetailActivityVm = ViewModelProvider(this).get(
        GetNftDetailActivityVm::class.java
    )

    @SuppressLint("SetTextI18n")
    override fun afterOnCreate(savedInstanceState: Bundle?) {
        super.afterOnCreate(savedInstanceState)
        intent.extras?.run {
            if(containsKey(key_gift_id)){
                id = intent.getStringExtra(key_gift_id).toString()
            }
            if(containsKey(key_gift_details_type)){
                type = intent.getIntExtra(key_gift_details_type,0)
                if(type == 1 || type == 2){
                    nearBy = intent.getParcelableExtra(key_gift_details)
                }
            }
        }
        EventBus.getDefault().register(this)
        viewModel?.getDetails(null,id,nearBy)

        viewModel?.details?.observe(this, Observer {
             cvb?.run {
                 nearBy = it
                 ivPic.loadImage(it.cid,R.drawable.ic_get_nft_holder)
                 tvName.text = it.name
                 tvHint.text = it.description
                 var total1 = ""
                 if(it.isNumLimit){
                     total1 = BigDecimal(it.total).add(BigDecimal(it.num)).toPlainString()
                 }else{
                     total1 = "∞"
                 }

                 var str = it.total + "/" + total1
                 var spanned = SpannableString(str)
                 spanned.setSpan(ForegroundColorSpan(Color.parseColor("#3665FD")), str.indexOf(it.total), str.indexOf(it.total) + it.total.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //设置前景色为洋红色
                 tvNumber.text = spanned

//               val time = TimeUtils.convertStamp(SimpleDateFormat(TimeUtils.yyyyMMddHHmmss), it.createdDate)
                 tvTime.text = TimeUtils.convertDate(SimpleDateFormat(TimeUtils.yyyyMMddHHmmss_F),it.createdDate.toLong())
                 tvLocation.text = it.address
                 if(it.isSign){
                     //签名
                     rlCode.setOnClickListener {

                     }
                 }
                 showUIByType(type,it)
             }


        })
    }

    private fun showUIByType(type:Int,nearBy: NearBy){
        if(type == 0 || type == 1){
            cvb?.tvTimeTitle?.text = "创建时间"
            cvb?.llCreater?.visibility = View.GONE
            cvb?.lineCreater?.visibility = View.GONE
            cvb?.llCopy?.visibility = View.GONE
            cvb?.lineCode?.visibility = View.GONE
            cvb?.llNo?.visibility = View.GONE
            cvb?.lineNo?.visibility = View.GONE
            if(nearBy.isSign){
                //创建详情
                cvb?.rlCode?.visibility = View.VISIBLE
                cvb?.rlCode?.setOnClickListener {
                    startMeCodeActivity(id)
                }
            }else{
                cvb?.lineLocation?.visibility = View.GONE
            }

            cvb?.llLocation?.setOnClickListener {
                nearBy.run {
                    startSignInLocationActivity(lat,lng,true,this)
                }

            }
        }else{
            //获取详情
            cvb?.tvTimeTitle?.text = "领取时间"
            cvb?.llCreater?.visibility = View.VISIBLE
            cvb?.llCreater?.setOnClickListener {
                nearBy.run {
                    CopyUtils.copy(this@GetNftDetailActivity,createAccount)
                }
            }
            cvb?.lineCreater?.visibility = View.VISIBLE
            cvb?.lineCode?.visibility = View.GONE
            cvb?.llCopy?.visibility = View.VISIBLE
            cvb?.llNo?.visibility = View.VISIBLE
            cvb?.lineNo?.visibility = View.VISIBLE
            cvb?.tvNo?.text = nearBy.tokenId
            cvb?.tvHash?.text = nearBy.hash
            cvb?.tvCreater?.text = nearBy.createAccount

            cvb?.llCopy?.setOnClickListener {
                nearBy.run {
                    CopyUtils.copy(this@GetNftDetailActivity,hash)
                }
            }
            cvb?.llLocation?.setOnClickListener {
                nearBy.run {
                    startToReceiveLocationActivity(aglat,aglng,lat,lng,cid,ranges.toDouble())
                }

            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: SignInLocationEvent) {
        nearBy?.address = event.locationSearch.address
        nearBy?.lat = event.locationSearch.latLng.latitude
        nearBy?.lng = event.locationSearch.latLng.longitude
        nearBy?.ranges = event.ranges
        cvb?.tvLocation?.text = event.locationSearch.address
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}