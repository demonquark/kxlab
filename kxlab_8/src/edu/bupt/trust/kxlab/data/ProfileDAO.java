package edu.bupt.trust.kxlab.data;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.os.AsyncTask;
import edu.bupt.trust.kxlab.data.DaoFactory.Source;
import edu.bupt.trust.kxlab.model.ActivityHistory;
import edu.bupt.trust.kxlab.model.ActivityRecord;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.model.UserInformation;
import edu.bupt.trust.kxlab.utils.Loggen;

public class ProfileDAO implements ProfileDAOabstract.OnProfileRawDataReceivedListener{
	
	ProfileDAOweb web;
	ProfileDAOlocal local;
	ProfileDAOdummy dummy;
	WeakReference <LoginListener> loginlistener;
	WeakReference <ProfileListener> profileListener;
	
	
	// Outward facing methods (used by the class requesting the data)
	public void setLoginListener(LoginListener listener) { 
		this.loginlistener = new WeakReference <LoginListener> (listener); 
	}
	public void setProfileListener(ProfileListener listener) { 
		this.profileListener = new WeakReference <ProfileListener> (listener); 
	}
	public void setCacheDir(Context c) { local.setCacheDir(c); }
	
	// Inward facing methods (used to communicate with the class providing the data)
	protected ProfileDAO(Context c, LoginListener listener){
		local = new ProfileDAOlocal(this, c);
		web = new ProfileDAOweb(this);
		dummy = new ProfileDAOdummy(this);
		this.loginlistener = new WeakReference <LoginListener> (listener);
	}
	
	protected ProfileDAO(Context c, ProfileListener listener){
		local = new ProfileDAOlocal(this, c);
		web = new ProfileDAOweb(this);
		dummy = new ProfileDAOdummy(this);
		this.profileListener = new WeakReference <ProfileListener> (listener);
	}
	
	
	public User generateUser(){
		return dummy.randomUser();
	}

	public void login(String email, String password){
		login(Source.WEB, email, password);
	}

	/**
	 * Logging in can only be done via the web. There is no local counterpart.
	 * The fall back scenario is handled in the raw response call back function.
	 * If an of the parameters are null, the method will NOT make a server call, but just forward a failure the listener
	 * @param email
	 * @param password
	 */
	public void login(Source source, String email, String password){
		
		// determine the path to send to the server
		String path = Urls.pathProfileLogin;

		if(email != null && password != null){
			// build the query
			RequestParams params = new RequestParams();
			params.put(Urls.paramUserEmail, email);
			params.put(Urls.paramProfilePassword, password);
			path = ServicesDAOweb.getPath(true, path, params);

			// contact the web
			if(source == Source.WEB) { web.login(path); 
			} else { dummy.login(path);  }
		}else {
			// no user name or password was provided. Fail by default. 
			this.onLogin(new RawResponse(RawResponse.Error.ILLEGALARGUMENT, "", path));
		}
	}
	
	public void readUserInformation(String email){
		readUserInformation(Source.WEB, email);
	}

	/**
	 * Getting the details of a profile gets you the information of a person.
	 * The user information is saved to cache and loaded if the web request fails.
	 * @param email
	 * @param source
	 */
	public void readUserInformation(Source source, String email){
		// determine the path to send to the server
		String path = Urls.pathProfileUserInfo;

		if(email != null){
			// build the query
			RequestParams params = new RequestParams();
			params.put(Urls.paramUserEmail, email);
			path = ServicesDAOweb.getPath(false, path, params);

			// contact the web
			if(source == Source.WEB) { 
				web.readUserInformation(path); 
			}else if(source == Source.LOCAL) { 
				local.readFromFile(ProfileDAOlocal.pathToFileName(path));
			} else {
				dummy.readUserInformation(path);
			}
		}else {
			// no user name was provided. Fail by default. 
			this.onReadUserInformation(new RawResponse(RawResponse.Error.ILLEGALARGUMENT, "", path));
		}

	}

	/**
	 * Getting the details of a profile gets you the information of a person.
	 * The user information is saved to cache and loaded if the web request fails.
	 * @param email
	 * @param source
	 */
	public void readUserList(Source source){
			dummy.readUsers(null);
	}

	/** Update from the old user to the new user. 
	 *  Because each change to the user is an individual server request, 
	 *  we will only send requests for the changes provided.
	 *  If the old user is null, we will update all the values of the given user. */
	public void updateUser(User oldUser, User newUser){
		
		final User updatedUser = new User(newUser);
		
		// determine the path to send to the server
		new AsyncTask<Void, Integer, Void>  (){
			@Override protected Void doInBackground(Void... params) {
				try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
				return null;
			}

			@Override protected void onPostExecute(Void v) {
				if(profileListener.get() != null){ profileListener.get().onChangeUser(updatedUser, null); }
			}
	
		}.execute();
	}

	/** Reads the activity history of the given user.
	 *  Note: only the email variable of the user is used to get the history. */
	public void readActivityHistory(User user){
		
		final ActivityHistory history = new ActivityHistory();
		for (int i = 0; i < 30; i++){
			Calendar x = Calendar.getInstance();
			x.set(2000 + i, i % 12, (i * 2 ) % 28);
			ActivityRecord r = new ActivityRecord(x.getTimeInMillis(), "a string for " + i, (i % 3) - (i % 4));
			history.addRecord(r);
		}
		
		// determine the path to send to the server
		new AsyncTask<Void, Integer, Void>  (){
			@Override protected Void doInBackground(Void... params) {
				try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
				return null;
			}

			@Override protected void onPostExecute(Void v) {
				if(profileListener.get() != null){ profileListener.get().onReadActivityHistory(history); }
			}
	
		}.execute();
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
		
		if(loginlistener.get() != null){ loginlistener.get().onLogin(success, errorMessage); }
	}
	
	@Override public void onReadUserList(RawResponse response) {
		
		ArrayList<User> users = new ArrayList<User> ();
		for(int i =0; i < 4; i++){
			users.add(dummy.randomUser());
		}
		
		if (profileListener.get() != null){ profileListener.get().onReadUserList(users); }
	}
	
	@Override public void onReadUserInformation(RawResponse response) {
		Loggen.v(this, "Got a response onReadUserInformation: " + response.message);
		
		User user = null;
		
		if(response.errorStatus == RawResponse.Error.NONE){
			try { 
				// Try to convert the JSON to userInformation and save it to user object 
				UserInformation userinfo = new Gson().fromJson(response.message, UserInformation.class);
				user = new User(userinfo);
				
				// TODO: save the image to file (Right now we use the server results)
				
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
		// TODO Auto-generated method stub
		
	}
	
	@Override public void onChangePhoto(RawResponse response) {
		// TODO Auto-generated method stub
		
	}
	
	@Override public void onChangePassword(RawResponse response) {
		// TODO Auto-generated method stub
		
	}
	
	@Override public void onChangePhonenumber(RawResponse response) {
		// TODO Auto-generated method stub
		
	}
	
	@Override public void onChangeSource(RawResponse response) {
		// TODO Auto-generated method stub
		
	}

	public interface LoginListener {
		public void onLogin(boolean success, String errorMessage);
	}
	
	public interface ProfileListener {
		public void onReadUserList(List <User> users);
		public void onReadUserInformation(User user);
		public void onReadActivityHistory(ActivityHistory history);
		public void onChangeUser(User newUser, String errorMessage);
		public void onChangePhoto(boolean success, String errorMessage);
		public void onChangePassword(boolean success, String errorMessage);
		public void onChangePhonenumber(boolean success, String errorMessage);
		public void onChangeSource(boolean success, String errorMessage);
		public void onLocalFallback();
	}
}
