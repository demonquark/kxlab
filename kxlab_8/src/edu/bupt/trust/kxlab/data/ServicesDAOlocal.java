package edu.bupt.trust.kxlab.data;

import java.io.File;

import edu.bupt.trust.kxlab.data.RawResponse.Page;
import edu.bupt.trust.kxlab.model.ServiceFlavor;
import edu.bupt.trust.kxlab.model.ServiceType;
import edu.bupt.trust.kxlab.utils.FileManager;
import edu.bupt.trust.kxlab.utils.Gegevens;

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


	@Override protected void readServices(String email, 
			String searchterm, ServiceFlavor flavor, ServiceType type, int size, Page page) {

		// determine the cache file name
		final String cachefilename = ServicesDAOlocal.getServicesListFilename(type.getFragName(), flavor.toString());
		
		// read from the file
		String response = readFromFile(cachefilename);
		
		// create a response
		RawResponse rawResponse = new RawResponse(response, cachefilename);
		if(response == null){ rawResponse.errorStatus = RawResponse.Error.FILE_NOT_FOUND; }
		rawResponse.page = page;
		
		// send back the response
		listener.onReadServices(rawResponse);
	}

	@Override protected void readService(int id, int size, Page page) {
		// determine the cache file name
		final String cachefilename = ServicesDAOlocal.getServicesDetailFilename(id);
		
		// read from the file
		String response = readFromFile(cachefilename);
		
		// create a response
		RawResponse rawResponse = new RawResponse(response, cachefilename);
		if(response == null){ rawResponse.errorStatus = RawResponse.Error.FILE_NOT_FOUND; }
		rawResponse.page = page;
		
		// send back the response
		listener.onReadService(rawResponse);
	}

	@Override protected void updateServiceScore(int serviceId, String userMail, int score) {
		listener.onUpdateServiceScore(new RawResponse(RawResponse.Error.ILLEGALARGUMENT));
	}

	@Override protected void createServiceComment(int serviceId, String userMail, int rootcommentid, String comment) {
		listener.onCreateComment(new RawResponse(RawResponse.Error.ILLEGALARGUMENT));
		
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

	public String readFromFile(String filename) {
		return FileManager.readFromFile(new File(cacheDir, filename + Gegevens.FILE_EXT_DAT));
	}
	
	public boolean writeToFile(String filename, String string) {
		return FileManager.writeToFile(new File(cacheDir, filename + Gegevens.FILE_EXT_DAT), string);
	}

	@Override protected void editService(int id, String title, String detail, String photo){
		listener.onEditService(new RawResponse(RawResponse.Error.ILLEGALARGUMENT));
	}

	@Override protected void createService(String email, int id, String title, String detail, String photo) {
		listener.onCreateService(new RawResponse(RawResponse.Error.ILLEGALARGUMENT));
	}
	@Override protected void deleteService(int ServiceId) {
		listener.onDeleteService(new RawResponse(RawResponse.Error.ILLEGALARGUMENT));
	}

	public static String getServicesDetailFilename(int id) {
		return Urls.fileServiceDetail + "_" + id   + Gegevens.FILE_EXT_DAT;
	}

	public static String getServicesListFilename(String type, String flavor) {
		return Urls.fileServiceList + flavor + "_" + type   + Gegevens.FILE_EXT_DAT;
	}
	
	public static String getServiceFilename(int id) {
		return Urls.fileService + id + Gegevens.FILE_EXT_DAT;
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
