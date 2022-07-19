package org.topnetwork.pintogether.model;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.RegeocodeAddress;

public class LocationSearch{

    public LocationSearch(AMapLocation aMapLocation){
        name = "[位置]";
        address = aMapLocation.getAddress();
        isSelect = true;
        latLng = new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());
    }

    public LocationSearch(AMapLocation aMapLocation,double lat,double lon){
        name = "[位置]";
        address = aMapLocation.getAddress();
        isSelect = true;
        latLng = new LatLng(lat,lon);
    }

    public LocationSearch(PoiItem poiItem){
        address = poiItem.getProvinceName() + poiItem.getCityName() + poiItem.getAdName() + poiItem.getSnippet()
                + poiItem.getTitle();
        name = poiItem.getTitle();
        latLng = new LatLng(poiItem.getLatLonPoint().getLatitude(),poiItem.getLatLonPoint().getLongitude());
    }

    public LocationSearch(RegeocodeAddress regeocodeAddress){
        name = "[位置]";
        address = regeocodeAddress.getFormatAddress();
        isSelect = true;
        latLng = new LatLng(regeocodeAddress.getStreetNumber().getLatLonPoint().getLatitude(),
                regeocodeAddress.getStreetNumber().getLatLonPoint().getLongitude());
    }
    public LocationSearch(){}

    public String name;
    public String address;
    public LatLng latLng;
    public boolean isSelect = false;
}
