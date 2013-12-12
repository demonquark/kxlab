package edu.bupt.trust.kxlab.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Random;

import edu.bupt.trust.kxlab.utils.Loggen;

import android.content.Context;
import android.os.Environment;

/**
 *  <p>ServicesDAOlocal reads data from a local file. <br />
 *  We assume that all cache files will be relatively small, thus it will be okay to handle all the 
 *  IO operations in the main thread. Unlike the DAOweb, this class does not spin data access to a separate thread. </p>
 */
class ServicesDAOlocal extends ServicesDAOabstract {

	private File cacheDir;
	protected String fileReadServices 	= pathToFileName(Urls.pathServiceList);
	protected String fileReadService 	= pathToFileName(Urls.pathServiceDetail);
	
	static String REPLACE_appname = "edu.bupt.trust.kxlab8";  
	static String REPLACE_separator = "/";  
	static String REPLACE_extension = ".dat";
	static String REPLACE_cachefolder = "Android" + REPLACE_separator + "data" 
								 + REPLACE_separator + REPLACE_appname + REPLACE_separator + "cache";
	int maxTimeInCache = 48 * 60 * 60 * 1000; // Ignore anything that has been cached for more than 48 hours
	
	/**
	 *  <p>Constructor with context. Use the Android default cache folder:<br />
	 *  - If SD is available on the SD card from getExternalCacheDir()
	 *  - If SD is not available on the internal memory from getCacheDir()
	 *  </p>
	 */
	public ServicesDAOlocal(OnServicesRawDataReceivedListener listener, Context c){ 
		setCacheDir(c);
		this.listener = listener;
	}


	@Override
	protected void readServices(String filename) {
		Loggen.v(this, "Request to read services: " + filename);
		
		// Read the file and return the content
		String response = readFromFile(filename); 
		listener.onReadServices(new RawResponse(response, filename));
		
	}

	@Override
	protected void readService(String filename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void updateServiceScore(String filename) {
	}

	@Override
	protected void createServiceComment(String filename) {
		// TODO Auto-generated method stub
		
	}
	
	protected File getCacheDir(){
		return cacheDir;
	}

	private boolean isExternalStorageAvailable() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	public void setCacheDir(Context c) {

		if(isExternalStorageAvailable()){
			// If we have access to the external card. Create a folder there. 
			if(c != null){
				cacheDir = c.getExternalCacheDir();
			} else {
				// If we have access to the external card. Create a folder there. 
				cacheDir = new File(Environment.getExternalStorageDirectory(), REPLACE_cachefolder);
			}
		} else {
			// If we have no access to the external card. Create a subfolder in the data folder
			if(c != null){
				cacheDir = c.getCacheDir();
			} else {
				// If we have no access to the external card. Create a subfolder in the data folder 
				cacheDir = new File(Environment.getDataDirectory(), "data"+REPLACE_separator+ REPLACE_appname+REPLACE_separator+"cache");
			}
		}

		// Make sure the directory exists
		if(!cacheDir.exists()){ cacheDir.mkdirs(); }
	}

	public boolean fileExists(String filename) {
		File file = new File (cacheDir, filename + REPLACE_extension);
		return (file.exists() && file.lastModified() > System.currentTimeMillis() - maxTimeInCache);
	}

	public void writeToFile(String filename, String message) {
        File file = new File(cacheDir, filename + REPLACE_extension);
        Loggen.v(this, "Writing to file: " + file.getName());
		BufferedWriter writer = null;
		try {// write the output to the file
        	writer = new BufferedWriter(new FileWriter(file, false));
			writer.write(message);
		} catch (IOException e) { e.printStackTrace(); 
		} finally { 
			if (writer != null) { 
				try { writer.close(); } catch (IOException e) { e.printStackTrace(); } 
			} 
		}
	}

	public String readFromFile(String filename) {
        File file = new File(cacheDir, filename + REPLACE_extension);
        byte[] b  = new byte[(int)file.length()];
		int len = b.length;
        Loggen.v(this, "Reading from file: " + file.getName());

		InputStream in = null;
		try{
			in = new FileInputStream(file);
			int total = 0;
			while (total < len) {
				int result = in.read(b, total, len - total);
				if (result == -1) { break; }
				total += result;
			}
		} catch (IOException e) { e.printStackTrace(); 
		} finally {
			if (in != null) { 
				try { in.close(); } catch (IOException e) { e.printStackTrace(); } 
			} 
		}
		
		return Charset.forName("UTF-8").decode(ByteBuffer.wrap(b)).toString();
		
	}
	
//	public static String readString(File file) throws IOException {
//		InputStream in = new FileInputStream(file);
//		byte[] b  = new byte[(int)file.length()];
//		int len = b.length;
//		int total = 0;
//
//		while (total < len) {
//		  int result = in.read(b, total, len - total);
//		  if (result == -1) {
//		    break;
//		  }
//		  total += result;
//		}
//
//		return Charset.forName("UTF-8").decode(ByteBuffer.wrap(b)).toString();
//	}
	
}
