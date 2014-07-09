package com.deimoslabs.nsbaloonlocator;



import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private String telNumber;
	private EditText etTelNumber, etTimeInterval;
	private TextView tvN, tvE, tvAlt, tvTimeStamp;
	private Button bStart;
	private LocationListener mLocationListener;
	private Boolean isGpsOn = false;
	private Boolean isGpsWorking = false;
	private GPSManager mGpsManager;
	private SMSManager mSmsManager;
	private LocationData mLastLocation;
	private File mDataFile;
	private File mLogFile;
	private Context ctx;
	private enum FieldStatus { OK, NOPHONE, NOTOK };
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = this;
        
        initControls();
    }

    private void initControls() {
    	etTelNumber = (EditText) findViewById(R.id.etTelNumber);
    	etTimeInterval = (EditText) findViewById(R.id.etTimeInterval);
    	tvN = (TextView) findViewById(R.id.tvN);
    	tvE = (TextView) findViewById(R.id.tvE);
    	tvAlt = (TextView) findViewById(R.id.tvAlt);
    	tvTimeStamp = (TextView) findViewById(R.id.tvTimeStamp);
    	bStart = (Button) findViewById(R.id.bStart);
    	
		mLocationListener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderEnabled(String provider) {				
				
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				
			}
			
			@Override
			public void onLocationChanged(Location location) {
				mLastLocation = new LocationData(location, Calendar.getInstance().getTime());
				updateLocation(location);
				
			}
		};
		
		bStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				FieldStatus result = verifyFields();
				
				switch (result) {
				
					case OK :
					
						isGpsWorking = !isGpsWorking;
						
						if (isGpsWorking) {
							startGps();
							startSms();
							controlsOff();							
							initGpsFile();
							Log.i("GPS-STATUS", "### TRACKING STARTED");
						} else {
							controlsOn();
							mGpsManager.stopGPS();
							if (mSmsManager != null)
								mSmsManager.stopSMS();

							Log.i("GPS-STATUS", "### TRACKING STOPPED");
						}
						break;
						
					case NOTOK :
						Toast.makeText(ctx, R.string.toast_error, Toast.LENGTH_SHORT).show();
						break;
						
					case NOPHONE :
						
						isGpsWorking = !isGpsWorking;
						
						if (isGpsWorking) {
							startGps();
							controlsOff();
						} else {
							controlsOn();
							mGpsManager.stopGPS();
							if (mSmsManager != null)
								mSmsManager.stopSMS();
						}
						Log.i("GPS-STATUS", "### TRACKING STOPPED");

						break;
				}
			}
		});    	
    }
    
    private void controlsOn() {
		bStart.setText(R.string.txt_start);
		etTimeInterval.setEnabled(true);
		etTelNumber.setEnabled(true);
    }
    
    private void controlsOff() {
		bStart.setText(R.string.txt_stop);
		etTimeInterval.setEnabled(false);
		etTelNumber.setEnabled(false);
    }
    
    private void startGps() {
		if (mGpsManager == null)  {
			mGpsManager = new GPSManager(getBaseContext(), mLocationListener);
		}  else { 
			mGpsManager.startGPS(); 
		}
    }
    
    private void startSms() {
		if (mSmsManager == null) {
			mSmsManager = new SMSManager(MainActivity.this, etTelNumber.getText().toString(), Integer.parseInt(etTimeInterval.getText().toString()));
		} else {
			mSmsManager.startSMSTask(Integer.parseInt(etTimeInterval.getText().toString())); 
		}
    }
    
    private void initLogFile() {
		try {
			mLogFile = AppStorage.getInstance().createLogFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void initGpsFile() {
		try {
			mDataFile = AppStorage.getInstance().createGpsFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	private void updateLocation(Location loc) {
		final String SEP = ";";
		final String END = "\n";
		String posN = getText(R.string.txt_n).toString();
		String posE = getText(R.string.txt_e).toString();
		String posAlt = getText(R.string.txt_alt).toString();
		String timeStamp = getText(R.string.txt_lastread).toString();
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss");
		String today = df.format(Calendar.getInstance().getTime());
		
		posN = posN + " " + loc.getLatitude();
		posE = posE + " " + loc.getLongitude();
		posAlt = posAlt + " " + loc.getAltitude();
		timeStamp = timeStamp + " " + today;
		
		tvN.setText(posN);
		tvE.setText(posE);
		tvAlt.setText(posAlt);
		tvTimeStamp.setText(timeStamp);
		
		String dataString = today 
				+ SEP 
				+ loc.getLatitude() 
				+ SEP
				+ loc.getLongitude()
				+ SEP
				+ loc.getAltitude()
				+ END;
		
		String logString = today
				+ SEP
				+ "POSITION READ"
				+ END;
				
		
		try {
			Log.i("GPS-READ", dataString);
			AppStorage.getInstance().writeToLog(mLogFile, logString);
			AppStorage.getInstance().writeToFile(mDataFile, dataString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public FieldStatus verifyFields() {
		
		if (isFieldEmpty(etTelNumber) || isFieldEmpty(etTimeInterval))
			return FieldStatus.NOTOK;
		
		if (etTelNumber.getText().toString().equals("0"))
			return FieldStatus.NOPHONE;
		
		return FieldStatus.OK;		
	}
	
	private boolean isFieldEmpty(EditText et) {
		if (et.getText().length() < 1)
			return true;
		
		return false;
	}
	
	public LocationData getLastReading() {
		return mLastLocation;
	}	
	
	@Override
	public void onBackPressed() {
		if (isGpsWorking) {			
			Toast.makeText(ctx, R.string.toast_back, Toast.LENGTH_SHORT).show();
		} else {
			super.onBackPressed();;
		}
	}
}
