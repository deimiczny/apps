package com.deimoslabs.nsbaloonlocator;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;


public class GPSManager {

	private LocationManager mLocationManager;
	private LocationListener mLocationListener;	
	private Context ctx;
	private static final int INTERVAL = 60000;
	
	public GPSManager(Context _ctx, LocationListener listener) {
		this.ctx = _ctx;
		this.mLocationListener = listener;
		this.mLocationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
		initGPS();
	}
	
	private void initGPS() {
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL, 5, mLocationListener);
	}
	
	public void stopGPS() {
		mLocationManager.removeUpdates(mLocationListener);
	}
	
	public void startGPS() {
		initGPS();
	}
	 
}
