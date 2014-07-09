package com.deimoslabs.nsbaloonlocator;

import java.util.Date;

import android.location.Location;

public class LocationData {
	
	private Location mLocation;
	private Date mDate;
	
	public LocationData(Location _loc, Date _date) {
		this.mLocation = _loc;
		this.mDate = _date;
	}
	
	public void setDate(Date _date) {
		this.mDate = _date;
	}
	
	public void setLocation(Location _loc) {
		this.mLocation = _loc;
	}
	
	public Location getLocation() {
		return mLocation;
	}
	
	public Date getDate() {
		return mDate;
	}

}
