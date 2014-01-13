package edu.bupt.trust.kxlab.data;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import android.content.Context;
import android.util.Log;
import edu.bupt.trust.kxlab.data.DaoFactory.Source;
import edu.bupt.trust.kxlab.data.RawResponse.Page;
import edu.bupt.trust.kxlab.model.JsonActivityRecord;
import edu.bupt.trust.kxlab.model.JsonUser;
import edu.bupt.trust.kxlab.model.JsonUserInformation;
import edu.bupt.trust.kxlab.model.SortKey;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.JsonTools;
import edu.bupt.trust.kxlab.utils.Loggen;

public class ProfileDAO implements ProfileDAOabstract.OnProfileRawDataReceivedListener{
	
	ProfileDAOweb web;
	ProfileDAOlocal local;
	ProfileDAOdummy dummy;
	WeakReference <ProfileListener> profileListener;
	
	// Outward facing methods (used by the class requesting the data)
	public void setProfileListener(ProfileListener listener) { 
		this.profileListener = new WeakReference <ProfileListener> (listener); 
	}
	
	// Inward facing methods (used to communicate with the class providing the data)
	protected ProfileDAO(Context c, ProfileListener listener){
		local = new ProfileDAOlocal(this, c);
		web = new ProfileDAOweb(this);
		dummy = new ProfileDAOdummy(this);
		this.profileListener = new WeakReference <ProfileListener> (listener);
	}
	
	public User generateUser(){
		return dummy.randomUser();
	}

	/**
	 * Logging in can only be done via the web. There is no local counterpart.
	 * The fall back scenario is handled in the raw response call back function.
	 * If an of the parameters are null, the method will NOT make a server call, but just forward a failure the listener
	 * @param email
	 * @param password
	 */
	public void login(Source source, String email, String password){
		// contact the web
		if(source == Source.WEB) { 
			web.login(email, password); 
		} else { 
			dummy.login(email, password); 
		}
	}
	
	/**
	 * Getting the details of a profile gets you the information of a person.
	 * The user information is saved to cache and loaded if the web request fails.
	 * @param email
	 * @param source
	 */
	public void readUserInformation(Source source, String email){
		if(email != null){
			// let the correct source handle the request
			switch (source) {
			case DEFAULT:
			case WEB:
				web.readUserInformation(email); 
				break;
			case LOCAL:
				local.readUserInformation(email); 
				break;
			case DUMMY:
				dummy.readUserInformation(email); 
				break;
			}
		}else {
			// no user email was provided. Fail by default. 
			this.onReadUserInformation(new RawResponse(RawResponse.Error.ILLEGALARGUMENT, "", ""));
		}
	}
	
	/**
	 * Getting the details of a profile gets you the information of a person.
	 * The user information is saved to cache and loaded if the web request fails.
	 * @param email
	 * @param source
	 */
	public void readUserList(Source source, SortKey sortKey, List<User> users, Page page){
		
		// save the current page to the cache 
		if(page != Page.CURRENT || (source == Source.WEB && users != null ) ){ 
			overwriteUserList(sortKey, users); 
		}
		
		// determine the records size 
		int size = (users != null) ? users.size() : 0;

		// let the correct source handle the request
		switch (source) {
		case DEFAULT:
		case WEB:
			web.readUsers(sortKey.getServerType(), size, page);
			break;
		case LOCAL:
			local.readUsers(sortKey.getServerType(), size, page);
			break;
		case DUMMY:
			dummy.readUsers(sortKey.getServerType(), size, page);
			break;
		}
	}

	/** Update from the old user to the new user. 
	 *  Because each change to the user is an individual server request, 
	 *  we will only send requests for the changes provided.
	 *  If the old user is null, we will update all the values of the given user. */
	public void changeUser(Source source, User oldUser, User newUser){
		// can't do anything if don't have new user.
		if(newUser != null ){
			// create JSON users.
			JsonUser oldJson = (oldUser != null) ? oldUser.getJsonUser() : new JsonUser();
			JsonUser newJson = (newUser != null) ? newUser.getJsonUser() : new JsonUser();
			
			// let the correct source handle the request
			switch (source) {
			case DEFAULT:
			case WEB:
				changeUser(oldJson, newJson);
				break;
			case LOCAL:
			case DUMMY:
				changeUser(newJson, newJson);
				break;
			}
		} else {
			if(profileListener.get() != null){ 
				profileListener.get().onChangeUser(null, RawResponse.Error.ILLEGALARGUMENT.toString()); }
		}
	}
	
	
	/** Reads the activity history of the given user.
	 *  Note: only the email variable of the user is used to get the history. */
	public void readActivityHistory(Source source, User user, List<JsonActivityRecord> records, Page page){
		
		// save the current page to the cache 
		if(page != Page.CURRENT || (source == Source.WEB && records != null ) ){ 
			overwriteActivityHistory(user, records); 
		}
		
		// determine the records size 
		int recordSize = (records != null) ? records.size() : 0;

		if(user != null && user.getEmail() != null){
			// let the correct source handle the request
			switch (source) {
			case DEFAULT:
			case WEB:
				web.readActivityHistory(user.getEmail(), recordSize, page);
				break;
			case LOCAL:
				local.readActivityHistory(user.getEmail(), recordSize, page);
				break;
			case DUMMY:
				dummy.readActivityHistory(user.getEmail(), recordSize, page);
				break;
			}
		}else {
			// no user email was provided. Fail by default. 
			this.onReadActivityHistory(new RawResponse(RawResponse.Error.ILLEGALARGUMENT, "", ""));
		}
	}

	/** Update from the old user to the new user. 
	 *  Because each change to the user is an individual server request, 
	 *  we will only send requests for the changes provided.
	 *  If the old user is null, we will update all the values of the given user. */
	private void changeUser(JsonUser oldUser, JsonUser newUser){
		
		Gson gson = new Gson();
		
		// save the users to the cache.
		String oldUserFileName = ProfileDAOlocal.getOldUserFilename(oldUser.email);
		String newUserFileName = ProfileDAOlocal.getNewUserFilename(newUser.email);
		local.writeToFile(oldUserFileName, gson.toJson(oldUser));
		local.writeToFile(newUserFileName, gson.toJson(newUser));
		
		Loggen.v(this, "Request to change user");
		if(newUser.phonenumber != null && !newUser.phonenumber.equals(oldUser.phonenumber)){
			// change the phone number
			Loggen.v(this, "Request to change phone number");
			web.changePhonenumber(newUser.email, newUser.phonenumber);
		} else if(newUser.type != oldUser.type){
			// change the type
			Loggen.v(this, "Request to change type");
			web.changeSource(newUser.email, newUser.type);
		} else if (newUser.localPhoto != null && !newUser.localPhoto.equals(oldUser.localPhoto)){
			// change the photo
			// TODO: Fix the photo logic
			Loggen.v(this, "Request to change photo");
			web.changePhoto(newUser.email, dummy.randomString());
		} else if (newUser.password != null && !newUser.password.equals(oldUser.password)){
			// change the password
			Loggen.v(this, "Request to change password");
			web.changePassword(newUser.email, oldUser.password, newUser.password);
		} else {
			// convert the JSON user to user info format
			JsonUserInformation userinfo = new JsonUserInformation();
			userinfo.UserInformation = newUser;

			// save the user information to the cache
			String userinfoFileName = ProfileDAOlocal.getUserInformationFilename(newUser.email);
			local.writeToFile(userinfoFileName, gson.toJson(userinfo));
			
			// done updating. Call the listener
			User updatedUser = new User(newUser);
			updatedUser.setLogin(true);
			if(profileListener.get() != null){ profileListener.get().onChangeUser(updatedUser, null); }
		}
	}
	
	private void overwriteActivityHistory(User user, List<JsonActivityRecord> records) {
		if(user != null && user.getEmail() != null){
			// create a JSON representation of the records
			ArrayList <JsonActivityRecord> jsonRecords = new ArrayList<JsonActivityRecord> ();
			if(records != null) { jsonRecords.addAll(records); }

			// write to file
			String cachefilename = ProfileDAOlocal.getActivityHistoryFilename(user.getEmail());
			local.writeToFile(cachefilename, new Gson().toJson(jsonRecords));
			Loggen.v(this, "saved content to file.");

		}
	}
	
	private void overwriteUserList(SortKey sortKey, List<User> users) {
		if(users != null){
			// create a JSON representation of the records
			ArrayList <JsonUser> jsonUsers = new ArrayList<JsonUser> ();
			if(users != null) { for(User user : users){ jsonUsers.add(user.getJsonUser()); } }

			// write to file
			String cachefilename = ProfileDAOlocal.getUserListFilename(sortKey.getServerType());
			local.writeToFile(cachefilename, new Gson().toJson(jsonUsers));
			Loggen.v(this, "saved content to file.");

		}
	}
	
	private boolean success(RawResponse response, String elementName){
		boolean success = false;

		if(response.errorStatus == RawResponse.Error.NONE){
			try { // Check the JSON for incidence of the element name
				JSONObject jo = new JSONObject(response.message);
				int successInt = (jo.has(elementName)) ? jo.getInt(elementName) : 0;
				success = (successInt > 0); 
			} catch (JSONException e){
				Loggen.e(this, "We were given an invalid JSON string.");
			} catch (ClassCastException e){
				Loggen.e(this, "The error message could not be parsed to a string.");
			}
		} else {
			Loggen.e(this, "We encountered an error: " + response.errorStatus.toString());
		}
		
		return success;
	}

	@Override public void onLogin(RawResponse response) {
		Loggen.v(this, "Got a response onLogin: " + response.message);
		
		// set the default values
		boolean success = false;
		String errorMessage = "";
		
		if(response.errorStatus == RawResponse.Error.NONE){
			try { // Check the JSON for incidence of the loginOrNot variable
				JSONObject login = new JSONObject(response.message);
				int successInt = (login.has(Urls.jsonLoginOrNot)) ? login.getInt(Urls.jsonLoginOrNot) : 0;
				success = (successInt > 0); 
				errorMessage = (login.has(Urls.jsonLoginErrorMessage)) ? (String) login.get(Urls.jsonLoginErrorMessage) : "";
			} catch (JSONException e){
				errorMessage = e.toString();
				Loggen.e(this, "We were given an invalid JSON string.");
			} catch (ClassCastException e){
				errorMessage = e.toString();
				Loggen.e(this, "The error message could not be parsed to a string.");
			}
		} else {
			errorMessage = response.errorStatus.toString();
			Loggen.e(this, "We encountered an error onLogin: " + response.errorStatus.toString());
		}
		
		if(profileListener.get() != null){ profileListener.get().onLogin(success, errorMessage); }
	}
	
	@Override public void onReadUserList(RawResponse response) {
		List <User> users = null;
		Gson gson = new Gson();
		
		if (response.errorStatus == RawResponse.Error.NONE
				&& JsonTools.isValidJSON(response.message)) {

			// Step 1 - convert the message into a JSON object
			java.lang.reflect.Type listType = new TypeToken<ArrayList<JsonUser>>() { }.getType();
			List <JsonUser> jsonUsers = gson.fromJson(response.message, listType);
			if(jsonUsers == null) { jsonUsers = new ArrayList<JsonUser> (); } 
			
			// Step 2 - update the message with the cache content
			if(response.page != Page.CURRENT){
			
				// Step 2a - read the existing data from cache. 
				Loggen.v(this, "Start getting old users.");
				List <JsonUser> oldRecords = gson.fromJson(local.readFromFile(response.path), listType);

				// Step 2b - read the existing data from cache. 
				if(oldRecords != null){
					// loop through all the old users
					for(int i = oldRecords.size() - 1; i >= 0; i--){

						// get the id and set overlap to false
						int oldId = oldRecords.get(i).getId();
						boolean overlap = false;

						// compare each old record to all the new records
						for(JsonUser user : jsonUsers){
							// if the old record is also in the new records, the records overlap
							if( oldId == user.getId()){ overlap = true; }
						}
						
						// if the records did not overlap, add this record to the list of records
						if(!overlap)
							jsonUsers.add(0,oldRecords.get(i));
					}
				}
			}
			
			// TODO: FIGURE OUT WHAT TO DO WITH IMAGES 
			for(JsonUser user : jsonUsers){

				String userinfoFileName = ProfileDAOlocal.getUserInformationFilename(user.email);

				// Step 3 - get the saved user information
				JsonUserInformation olduser = 
						gson.fromJson(local.readFromFile(userinfoFileName), JsonUserInformation.class);
				
				// Step 4 - Copy the user image to the file
				String userimage;
				if(olduser != null && olduser.UserInformation!= null 
						&& (userimage = olduser.UserInformation.localPhoto) != null 
						&& (userimage.endsWith(Gegevens.FILE_EXT_JPG) 
						|| userimage.endsWith(Gegevens.FILE_EXT_PNG) 
						|| userimage.endsWith(Gegevens.FILE_EXT_GIF))){
					user.localPhoto = userimage;
				} else {
					user.localPhoto = dummy.randomPic().getAbsolutePath();
				}
				
				// Step 5 - Save the user as json user information
				JsonUserInformation newUser = new JsonUserInformation();
				newUser.UserInformation = user;

				// Step 6 - Write the userinformation to file
				local.writeToFile(userinfoFileName, gson.toJson(newUser));
			}
			
			// Step 7 - save the date to the cache
			if (response.path != null) {
				local.writeToFile(response.path, gson.toJson(jsonUsers));
			}
			
			// Step 8 - convert the json users to users
			users = new ArrayList<User> ();
			for(JsonUser user : jsonUsers){ users.add(new User(user)); }
			
		} else {
			Log.e("Kris", "We encountered an error: " + response.message);
		}
		
		if (profileListener.get() != null){ profileListener.get().onReadUserList(users); }
	}
	
	@Override public void onReadUserInformation(RawResponse response) {
		Loggen.v(this, "Got a response onReadUserInformation: " + response.message);
		
		User user = null;
		if(response.errorStatus == RawResponse.Error.NONE){
			try { 
				// Step 1 - convert the message into a JSON object
				JsonUserInformation userinfo = new Gson().fromJson(response.message, JsonUserInformation.class);
				
				// Step 2 - convert the JSON object into a User
				if(userinfo != null && userinfo.UserInformation != null){
					
					// TODO: FIGURE OUT WHAT TO DO WITH IMAGES 
					// Step 2 - get the saved user information
					Loggen.v(this, "Read file: " + local.readFromFile(response.path));
					JsonUserInformation olduser = 
							new Gson().fromJson(local.readFromFile(response.path), JsonUserInformation.class);
					
					// Step 3 - Copy the user image to the file
					String userimage;
					if(olduser != null && olduser.UserInformation != null 
							&& (userimage = olduser.UserInformation.localPhoto) != null 
							&& (userimage.endsWith(Gegevens.FILE_EXT_JPG) 
							|| userimage.endsWith(Gegevens.FILE_EXT_PNG) 
							|| userimage.endsWith(Gegevens.FILE_EXT_GIF))){
						userinfo.UserInformation.localPhoto = userimage;
					} else {
						userinfo.UserInformation.localPhoto = dummy.randomPic().getAbsolutePath();
					}

					// Step 4 - do the final conversion
					user = new User(userinfo.UserInformation);

					// Step 5 - save the user information to the cache
					local.writeToFile(response.path, new Gson().toJson(userinfo));
				}
				
			} catch (com.google.gson.JsonSyntaxException e){
				Loggen.e(this, "We were given an invalid JSON string.");
			}
		} else {
			Loggen.e(this, "We encountered an error onReadUserInformation: " + response.errorStatus.toString());
		}
		
		// send the user back
		if (profileListener.get() != null){ profileListener.get().onReadUserInformation(user); }
	}
	
	@Override public void onReadActivityHistory(RawResponse response) {
		List <JsonActivityRecord> records = null;
		Gson gson = new Gson();
		
		if (response.errorStatus == RawResponse.Error.NONE
				&& JsonTools.isValidJSON(response.message)) {

			// Step 1 - convert the message into a JSON object
			java.lang.reflect.Type listType = new TypeToken<ArrayList<JsonActivityRecord>>() { }.getType();
			records = gson.fromJson(response.message, listType);
			if(records == null) { records = new ArrayList<JsonActivityRecord> (); } 
			
			// Step 2 - update the message with the cache content
			if(response.page != Page.CURRENT){
			
				// Step 2a - read the existing data from cache. 
				Loggen.i(this, "Start getting old records.");
				List <JsonActivityRecord> oldRecords = gson.fromJson(local.readFromFile(response.path), listType);

				// Step 2b - read the existing data from cache. 
				if(oldRecords != null){
					// loop through all the old records
					for(int i = oldRecords.size() - 1; i >= 0; i--){

						// get the id and set overlap to false
						int oldId = oldRecords.get(i).ahId;
						boolean overlap = false;

						// compare each old record to all the new records
						for(JsonActivityRecord record : records){
							// if the old record is also in the new records, the records overlap
							if( oldId == record.ahId){ overlap = true; }
						}
						
						// if the records did not overlap, add this record to the list of records
						if(!overlap)
							records.add(0,oldRecords.get(i));
					}
				}
			}

			// Step 3 - save the date to the cache
			if (response.path != null) {
				local.writeToFile(response.path, gson.toJson(records));
			}
		} else {
			Log.e("Kris", "We encountered an error: " + response.message);
		}

		// send the activity records back
		if (profileListener.get() != null){ profileListener.get().onReadActivityHistory(records); }
	}
	
	@Override public void onChangePhoto(RawResponse response) {
		
		// read the cached users
		JsonUser oldUser = 
				new Gson().fromJson(local.readFromFile(response.path), JsonUser.class);
		JsonUser newUser = 
				new Gson().fromJson(local.readFromFile(
						response.path.replaceFirst(Urls.fileOldUser, Urls.fileNewUser)), 
						JsonUser.class);

		// check if the user has changed
		if(success(response, Urls.jsonPhotoChangeOrNot)){
			oldUser.localPhoto = newUser.localPhoto;
		} else {
			newUser.localPhoto = oldUser.localPhoto;
		}

		// let the DAO determine if there are any more user variables that need to be changed
		changeUser(oldUser, newUser);
	}
	
	@Override public void onChangePassword(RawResponse response) {
		// read the cached users
		JsonUser oldUser = 
				new Gson().fromJson(local.readFromFile(response.path), JsonUser.class);
		JsonUser newUser = 
				new Gson().fromJson(local.readFromFile(
						response.path.replaceFirst(Urls.fileOldUser, Urls.fileNewUser)), 
						JsonUser.class);

		// check if the user has changed
		if(success(response, Urls.jsonPasswordChangeOrNot)){
			oldUser.password = newUser.password;
		} else {
			newUser.password = oldUser.password;
		}
		
		// let the DAO determine if there are any more user variables that need to be changed
		changeUser(oldUser, newUser);
	}
	
	@Override public void onChangePhonenumber(RawResponse response) {
		// read the cached users
		JsonUser oldUser = 
				new Gson().fromJson(local.readFromFile(response.path), JsonUser.class);
		JsonUser newUser = 
				new Gson().fromJson(local.readFromFile(
						response.path.replaceFirst(Urls.fileOldUser, Urls.fileNewUser)), 
						JsonUser.class);

		// check if the user has changed
		if(success(response, Urls.jsonPhoneChangeOrNot)){
			oldUser.phonenumber = newUser.phonenumber;
		} else {
			newUser.phonenumber = oldUser.phonenumber;
		}
		
		// let the DAO determine if there are any more user variables that need to be changed
		changeUser(oldUser, newUser);
	}
	
	@Override public void onChangeSource(RawResponse response) {
		Loggen.v(this, "source change response: " + response.message + " | " + response.errorStatus);
		// read the cached users
		JsonUser oldUser = 
				new Gson().fromJson(local.readFromFile(response.path), JsonUser.class);
		JsonUser newUser = 
				new Gson().fromJson(local.readFromFile(
						response.path.replaceFirst(Urls.fileOldUser, Urls.fileNewUser)), 
						JsonUser.class);

		// check if the user has changed
		if(success(response, Urls.jsonSourceChangeOrNot)){
			oldUser.type = newUser.type;
		} else {
			newUser.type = oldUser.type;
		}
		
		// let the DAO determine if there are any more user variables that need to be changed
		changeUser(oldUser, newUser);
	}

	public interface ProfileListener {
		public void onLogin(boolean success, String errorMessage);
		public void onReadUserList(List <User> users);
		public void onReadUserInformation(User user);
		public void onReadActivityHistory(List<JsonActivityRecord> records);
		public void onChangeUser(User newUser, String errorMessage);
	}
}
