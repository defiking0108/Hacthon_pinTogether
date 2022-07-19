package org.topnetwork.pintogether

import android.content.Intent
import org.topnetwork.pintogether.model.NearBy
import org.topnetwork.pintogether.ui.activity.view.*
import org.topnetwork.pintogether.utils.ActivityUtils

fun startNftDetails(){
    val intent = Intent()
    ActivityUtils.push(GetNftDetailActivity::class.java, intent)
}

fun startNftDetails(giftId:String,type:Int,nearBy:NearBy?){
    val intent = Intent()
    intent.putExtra(key_gift_id,giftId)
    intent.putExtra(key_gift_details_type,type)
    nearBy?.run {
        intent.putExtra(key_gift_details,this)
    }
    ActivityUtils.push(GetNftDetailActivity::class.java, intent)
}

fun startMain(){
    val intent = Intent()
    ActivityUtils.push(MainActivity::class.java, intent)
}

fun startCreateSuccessActivity(giftId:String){
    val intent = Intent()
    intent.putExtra(key_gift_id,giftId)
    ActivityUtils.push(CreateSuccessActivity::class.java, intent)

}

fun startToReceiveSuccessActivity(){
    val intent = Intent()
    ActivityUtils.push(ToReceiveSuccessActivity::class.java, intent)

}
fun startLogin(){
    val intent = Intent()
    ActivityUtils.push(LoginActivity::class.java, intent)
}

fun startToReceiveActivity(nearBy: NearBy){
    val intent = Intent()
    intent.putExtra(key_near_by,nearBy)
    ActivityUtils.push(ToReceiveActivity::class.java, intent)
}

fun startToReceiveActivity(id: String){
    val intent = Intent()
    intent.putExtra(key_gift_id,id)
    ActivityUtils.push(ToReceiveActivity::class.java, intent)
}

fun startCreateNftActivity(){
    val intent = Intent()
    ActivityUtils.push(CreateNftActivity::class.java, intent)

}

fun startMeCodeActivity(giftId:String){
    val intent = Intent()
    intent.putExtra(key_gift_id,giftId)
    ActivityUtils.push(MeCodeActivity::class.java, intent)
}

fun startToReceiveLocationActivity(one:Double,two:Double,three:Double,four:Double,pic:String,radius: Double){
    val intent = Intent()
    intent.putExtra(key_receive_location_from_lat,one)
    intent.putExtra(key_receive_location_from_lon,two)
    intent.putExtra(key_receive_location_to_lat,three)
    intent.putExtra(key_receive_location_to_lon,four)
    intent.putExtra(key_receive_location_pic,pic)
    intent.putExtra(key_receive_location_radius,radius)
    ActivityUtils.push(ToReceiveLocationActivity::class.java, intent)
}


fun startSignInLocationActivity(one:Double,two:Double,isChange:Boolean,nearBy: NearBy?){
    val intent = Intent()
    intent.putExtra(key_receive_location_from_lat,one)
    intent.putExtra(key_receive_location_from_lon,two)
    intent.putExtra(key_is_change,isChange)
    nearBy?.run {
        intent.putExtra(key_near_by,nearBy)
    }
    ActivityUtils.push(SignInLocationActivity::class.java, intent)

}

fun startLocationSearchActivity(){
    val intent = Intent()
    ActivityUtils.push(LocationSearchActivity::class.java, intent)

}