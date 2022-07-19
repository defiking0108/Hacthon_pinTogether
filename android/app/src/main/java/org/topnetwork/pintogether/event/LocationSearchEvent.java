package org.topnetwork.pintogether.event;

import com.amap.api.maps.model.LatLng;

/**
 * 作者    lgc
 * 时间    2022/7/14 15:58
 * 文件    PinTogether
 * 描述
 */
public class LocationSearchEvent {
    public LatLng latLng;

    public LocationSearchEvent(LatLng latLng){
        this.latLng = latLng;
    }
}
