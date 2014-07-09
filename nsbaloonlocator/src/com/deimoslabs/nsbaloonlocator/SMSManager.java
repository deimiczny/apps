package com.deimoslabs.nsbaloonlocator;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.telephony.SmsManager;
import android.util.Log;


public class SMSManager {

	private String telNumber;
	private Context ctx;
	private SmsManager mManager;
	private int INTERVAL;
	private int MILIS = 60000;
	private SMSTask mTask;
	
	public SMSManager(Activity _ctx, String number, int interval) {
		this.ctx = _ctx;
		this.telNumber = number;
		this.INTERVAL = interval;
		initManager();
	}
	
	private void initManager() {
		mManager = SmsManager.getDefault();
		startSMSTask(INTERVAL);
	}
	
	private void sendSMS(String sms) {
		mManager.sendTextMessage(telNumber, null, sms, null, null);
		Log.i("GPS-SMS", "SMS SENT TO: " + telNumber + "; MSG: " + sms);
	}
	
	private String buildSMS(LocationData loc) {
		final String SEP = ", ";
	
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String today = df.format(loc.getDate());
		
		String dataString = today 
				+ SEP 
				+ loc.getLocation().getLatitude() 
				+ SEP
				+ loc.getLocation().getLongitude()
				+ SEP
				+ loc.getLocation().getAltitude();
		
		return dataString;
		
	}
	
	public void startSMSTask(int interval) {
		this.INTERVAL = interval * MILIS;
		Timer timer = new Timer();
		mTask = new SMSTask();
		timer.schedule(mTask, 0, INTERVAL);
	}
	
	public void stopSMS() {
		mTask.cancel();
	}
	
	private class SMSTask extends TimerTask {

		@Override
		public void run() {
			LocationData loc = ((MainActivity)ctx).getLastReading();
			if (loc == null || loc.getLocation() == null)
				return;
			sendSMS(buildSMS(loc));
		}
		
	}
	 
}
