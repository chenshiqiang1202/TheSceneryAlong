/**
 * @description: Gps有效性判定工具
 * @author chenshiqiang E-mail:csqwyyx@163.com
 */
package com.csq.thesceneryalong.utils.location;

import android.location.Location;
import android.location.LocationManager;

public class LastLocationValidate {
    
    /**
     * 仅仅判断位置的精度，是否符合要求
     */
    public boolean isAccurateLocation(Location location){
    	if(location.getLatitude() > 0 && location.getLongitude() > 0){
    		if(location.getProvider().equals(LocationManager.GPS_PROVIDER)){
    			//gps,米以内
        		if(location.getAccuracy() < 150){
        			return true;
        		}
        	}else{
        		//其他，网络米以内
        		if(location.getAccuracy() < 100){
        			return true;
        		}
        	}
    	}
    	return false;
    }
    
    
    /** 有效时间差值 */
    protected int VALID_TIME = 1000 * 60;
    /** 有效精度差值 */
    protected int VALID_ACCACURY = 200;
    /** 最大有效距离,超过当作漂移 */
    protected int MAX_DISTANCE = 20;

    /** Determines whether one Location reading is better than the current Location fix
      * @param location  The new Location that you want to evaluate
      * @param currentBestLocation  The current Location fix, to which you want to compare the new one
      */
    public boolean isBetterLocation(Location location, Location currentBestLocation) {
    	if (location == null) {
    		return false;
    	}
    	
    	//1.精度判定
    	if(!isAccurateLocation(location)){
        	//new location is not a accurate location
        	return false;
        }
    	
    	if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

    	//2.时间判定
        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > VALID_TIME;
        boolean isSignificantlyOlder = timeDelta < -VALID_TIME;
        boolean isNewer = timeDelta > 0;

        if(!isNewer){
        	return false;
        }
        
        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
        // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > VALID_ACCACURY;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        
        // check distance 
        int distDetal = (int)location.distanceTo(currentBestLocation);
        if( isNewer && distDetal <= MAX_DISTANCE)
        {
            return true;
        }
        
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
          return provider2 == null;
        }
        return provider1.equals(provider2);
    }
    
}
