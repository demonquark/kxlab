package edu.bupt.trust.kxlab.data;

import java.io.File;

import edu.bupt.trust.kxlab.data.RawResponse.Page;
import edu.bupt.trust.kxlab.utils.FileManager;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;

import android.content.Context;

/**
 * <p>The ProfileDAOlocal manages the profile data from the cache.</p>
 * @author Krishna
 *
 */

class ProfileDAOlocal extends ProfileDAOabstract{
	
	private File cacheDir;
	
	/** <p>Constructor with a listener to send the raw data back to. </p> */
	public ProfileDAOlocal(OnProfileRawDataReceivedListener listener, Context c){
		
		// set the cache directory
		cacheDir = FileManager.setCacheDir(c);
		this.listener = listener;
	}

	/** <p>You cannot log in using local data </p> */
	@Override protected void login(String email, String password) {
		listener.onLogin(new RawResponse(RawResponse.Error.ILLEGALARGUMENT));
	}

	@Override protected void readUsers(String sortkey, int size, Page page) {
		Loggen.v(this, "Request to read user list: " + sortkey);
		
		// determine the cache file name
		String filename = ProfileDAOlocal.getUserListFilename(sortkey);

		// Read the file and create a raw response
		String response = readFromFile(filename);
		
		// create a response
		RawResponse rawResponse = new RawResponse(response, filename);
		if(response == null){ rawResponse.errorStatus = RawResponse.Error.FILE_NOT_FOUND; }
		
		// Return the raw response
		listener.onReadUserInformation(rawResponse);
	}

	/** <p>Load saved data from file. </p> */
	@Override protected void readUserInformation(String email) {
		Loggen.v(this, "Request to read user info: " + email);
		
		// determine the cache file name
		String filename = ProfileDAOlocal.getUserInformationFilename(email);

		// Read the file and create a raw response
		String response = readFromFile(filename);
		
		// create a response
		RawResponse rawResponse = new RawResponse(response, filename);
		if(response == null){ rawResponse.errorStatus = RawResponse.Error.FILE_NOT_FOUND; }
		
		// Return the raw response
		listener.onReadUserInformation(rawResponse);
	}

	@Override protected void readActivityHistory(String email, int size, Page page) {
		Loggen.v(this, "Request to read activity history: " + email);
		
		// determine the cache file name
		String filename = ProfileDAOlocal.getActivityHistoryFilename(email);

		// Read the file and create a raw response
		String response = readFromFile(filename);
		
		// create a response
		RawResponse rawResponse = new RawResponse(response, filename);
		if(response == null){ rawResponse.errorStatus = RawResponse.Error.FILE_NOT_FOUND; }
		
		// Return the raw response
		listener.onReadUserInformation(rawResponse);
	}

	@Override protected void changePhoto(String email, String photo) {
		listener.onChangePhoto(new RawResponse(RawResponse.Error.ILLEGALARGUMENT));		
	}

	@Override protected void changePassword(String email, String password, String newPassword) {
		listener.onChangePassword(new RawResponse(RawResponse.Error.ILLEGALARGUMENT));
	}

	@Override protected void changePhonenumber(String email, String phonenumber) {
		listener.onChangePhonenumber(new RawResponse(RawResponse.Error.ILLEGALARGUMENT));
	}

	@Override protected void changeSource(String email, int type) {
		listener.onChangeSource(new RawResponse(RawResponse.Error.ILLEGALARGUMENT));
	}
	
	public String readFromFile(String filename) {
		return FileManager.readFromFile(new File(cacheDir, filename + Gegevens.FILE_EXT_DAT));
	}
	
	public boolean writeToFile(String filename, String string) {
		return FileManager.writeToFile(new File(cacheDir, filename + Gegevens.FILE_EXT_DAT), string);
	}

	public static String getOldUserFilename(String email){
		return Urls.fileOldUser + email + Gegevens.FILE_EXT_DAT;
	}

	public static String getNewUserFilename(String email){
		return Urls.fileNewUser + email + Gegevens.FILE_EXT_DAT;
	}

	public static String getUserInformationFilename(String email){
		return Urls.fileUserInformation + email + Gegevens.FILE_EXT_DAT;
	}

	public static String getActivityHistoryFilename(String email){
		return Urls.fileActivityHistory + email + Gegevens.FILE_EXT_DAT;
	}

	public static String getUserListFilename(String sortKey){
		return Urls.fileActivityHistory + sortKey + Gegevens.FILE_EXT_DAT;
	}

}
