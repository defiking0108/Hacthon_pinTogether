package org.topnetwork.pintogether.event;

import com.amap.api.maps.model.LatLng;

/**
 * 作者    lgc
 * 时间    2022/7/15 12:43
 * 文件    PinTogether
 * 描述
 */
public class LocationChangedEvent {
    public LatLng latLng;

    public LocationChangedEvent(LatLng latLng) {
        this.latLng = latLng;
    }
}
