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

import edu.bupt.trust.kxlab.data.RawResponse.Page;
import edu.bupt.trust.kxlab.model.ServiceFlavor;
import edu.bupt.trust.kxlab.model.ServiceType;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;

import android.content.Context;
import android.os.Environment;

/**
 *  <p>ServicesDAOlocal reads data from a local file. <br />
 *  We assume that all cache files will be relatively small, thus it will be okay to handle all the 
 *  IO operations in the main thread. Unlike the DAOweb, this class does not spin data access to a separate thread. </p>
 */
class MyServicesDAOlocal extends ServicesDAOabstract {

	private File cacheDir;
	protected String fileReadServices 	= pathToFileName(Urls.pathServiceList);
	protected String fileReadService 	= pathToFileName(Urls.pathServiceDetail);
	
	int maxTimeInCache = 48 * 60 * 60 * 1000; // Ignore anything that has been cached for more than 48 hours
	
	/**
	 *  <p>Constructor with context. Use the Android default cache folder:<br />
	 *  - If SD is available on the SD card from getExternalCacheDir()
	 *  - If SD is not available on the internal memory from getCacheDir()
	 *  </p>
	 */
	public MyServicesDAOlocal(OnServicesRawDataReceivedListener listener, Context c){ 
		setCacheDir(c);
		this.listener = listener;
	}


	@Override
	protected void readServices(String filename, String searchterm, ServiceFlavor flavor, ServiceType type, int size, Page page) {
		Loggen.v(this, "Request to read services: " + filename);
		
		// Read the file and return the content
		String response = readFromFile(filename); 
		listener.onReadServices(new RawResponse(response, filename));
		
	}

	@Override protected void readService(int id, int size, Page page) {
		String path = "";
		
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void updateServiceScore(int serviceId, String userMail, int score) {
	}

	@Override
	protected void createServiceComment(int serviceId, String userMail, int rootcommentid, String comment) {
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
				cacheDir = new File(Environment.getExternalStorageDirectory(), 
						Gegevens.FILE_USERDIRSD + Gegevens.FILE_SEPARATOR + Gegevens.FILE_CACHE);
			}
		} else {
			// If we have no access to the external card. Create a subfolder in the data folder
			if(c != null){
				cacheDir = c.getCacheDir();
			} else {
				// If we have no access to the external card. Create a subfolder in the data folder
				cacheDir = new File(Environment.getDataDirectory(), 
						Gegevens.FILE_USERDIRPHONE + Gegevens.FILE_SEPARATOR + Gegevens.FILE_CACHE);
			}
		}

		// Make sure the directory exists
		if(!cacheDir.exists()){ cacheDir.mkdirs(); }
	}

	public boolean fileExists(String filename) {
		File file = new File (cacheDir, filename + Gegevens.FILE_EXT_DAT);
		return (file.exists() && file.lastModified() > System.currentTimeMillis() - maxTimeInCache);
	}

	public void writeToFile(String filename, String message) {
        File file = new File(cacheDir, filename + Gegevens.FILE_EXT_DAT);
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
        File file = new File(cacheDir, filename + Gegevens.FILE_EXT_DAT);
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

	@Override
	protected void editService(int id, String title, String detail, String photo) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void createService(String email, int id, String title, String detail, String photo){
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void deleteService(int serviceId) {
		// TODO Auto-generated method stub
		
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
