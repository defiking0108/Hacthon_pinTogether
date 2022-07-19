package org.topnetwork.pintogether.event;

import org.topnetwork.pintogether.model.LocationSearch;

/**
 * 作者    lgc
 * 时间    2022/7/14 15:37
 * 文件    PinTogether
 * 描述
 */
public class SignInLocationEvent {
    public LocationSearch locationSearch;
    public String ranges;

    public SignInLocationEvent(LocationSearch locationSearch,String ranges){
        this.locationSearch = locationSearch;
        this.ranges = ranges;

    }
}
