package com.deimoslabs.nsbaloonlocator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Environment;


public class AppStorage {
	
	private static final String FILENAME_PREFIX = "nsbaloon_data_";
	private static final String LOG_PREFIX = "log_";
	private static final String FILE_TYPE = ".csv";
	private static final String CATALOG = "nsballoon";
	private static AppStorage instance = null;
	
	private AppStorage() {
		
	}
	
	public static AppStorage getInstance() {
		if (instance == null)
			instance = new AppStorage();
		
		return instance;
	}
	
	public File createGpsFile() throws IOException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String today = df.format(Calendar.getInstance().getTime());
		
		File directory = new File(Environment.getExternalStorageDirectory() + File.separator + CATALOG);
		directory.mkdirs();
		File file = new File(directory,
				FILENAME_PREFIX 
				+ today 
				+ FILE_TYPE);
		
		file.createNewFile();
		
		return file;							
	}
	
	public File createLogFile() throws IOException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String today = df.format(Calendar.getInstance().getTime());
		
		File directory = new File(Environment.getExternalStorageDirectory() + File.separator + CATALOG);
		directory.mkdirs();
		File file = new File(directory,
				LOG_PREFIX 
				+ today 
				+ FILE_TYPE);
		
		file.createNewFile();
		
		return file;							
	}
	
	public void writeToFile(File file, String data) throws IOException {
		if (file == null)
			return;
		if(file.exists() && file.canWrite()) {
		     FileWriter fw = new FileWriter(file, true);
		     fw.append(data);
		     fw.close();
		}  
	}
	
	public void writeToLog(File file, String data) throws IOException {
		if (file == null)
			return;
		if(file.exists() && file.canWrite()) {
		     FileWriter fw = new FileWriter(file, true);
		     fw.append(data);
		     fw.close();
		}  
	}
	

}
