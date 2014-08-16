/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年4月27日 下午7:37:06   
 * @version 1.0   
 */
package com.csq.thesceneryalong.utils.location;

import android.annotation.TargetApi;
import android.location.Geocoder;
import android.os.Build;
import android.text.TextUtils;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.csq.thesceneryalong.app.App;
import com.csq.thesceneryalong.models.events.EventReverseGeocodingResult;

import de.greenrobot.event.EventBus;

public class ReverseGeocodingUtil {
	
	/**
	 * @description: 是否支持反向地理编码
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static boolean isReverseGeocodePresent(){
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Geocoder.isPresent();
	}

	/**
	 * @description: 开启一个反向地理编码的任务,通过EventReverseGeocodingResult返回结果
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param loc
	 */
	public static void reverseGeocode(final LatLng loc, final Object model){
		GeocodeSearch geocoderSearch = new GeocodeSearch(App.app);
		geocoderSearch.setOnGeocodeSearchListener(new OnGeocodeSearchListener() {
			@Override
			public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
				// TODO Auto-generated method stub
				if (rCode == 0) {
					if (result != null && result.getRegeocodeAddress() != null
							&& result.getRegeocodeAddress().getFormatAddress() != null) {
						String address = result.getRegeocodeAddress().getFormatAddress();
						
						if(TextUtils.isEmpty(address)){
							EventBus.getDefault().post(
				            		new EventReverseGeocodingResult(loc, null, model));
						}else{
							EventBus.getDefault().post(
				            		new EventReverseGeocodingResult(loc, address, model));
						}
					}
				}
			}
			
			@Override
			public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
			}
		});
		
		// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(loc.latitude, loc.longitude), 
				1000,
				GeocodeSearch.AMAP);
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
	}

}
