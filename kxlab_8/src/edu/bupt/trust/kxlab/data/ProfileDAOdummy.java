package edu.bupt.trust.kxlab.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;

import android.os.AsyncTask;
import android.os.Environment;
import edu.bupt.trust.kxlab.data.RawResponse.Page;
import edu.bupt.trust.kxlab.model.JsonActivityRecord;
import edu.bupt.trust.kxlab.model.JsonUser;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;

/**
 * <p>The ProfileDAOdummy generates dummy data to send to the screen.</p>
 * 
 * @author Krishna
 *
 */

class ProfileDAOdummy extends ProfileDAOabstract{
	
	private File cacheDir;
	private Random rand;
	
	/** <p>Constructor with a listener to send the raw data back to. </p> */
	public ProfileDAOdummy(OnProfileRawDataReceivedListener listener){
		cacheDir = new File(Environment.getExternalStorageDirectory(), 
				Gegevens.FILE_USERDIRSD + Gegevens.FILE_SEPARATOR + Gegevens.FILE_CACHE);
		this.listener = listener;
		this.rand = new Random();
	}

	@Override protected void login(String email, String password) {
		Loggen.i(this, "Dummy login for: " + email + " | " + password);
		
		new AsyncTask<Void, Integer, Void>  (){
			@Override protected Void doInBackground(Void... params) {
				try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
				return null;
			}

			@Override protected void onPostExecute(Void v) {
				String response = "{\"vote\":[],\"loginOrNot\":" + counter(2) 
						+ ", \"loginerrormessage\":\"This is a test result, try again\"}";
				listener.onLogin(new RawResponse(response, "dummylogin"));
			}
	
		}.execute();
	}

	@Override protected void readUsers(String sortkey, int size, Page page) {
		Loggen.i(this, "Dummy read user lists for: " + sortkey);
		
		// determine the cache file name
		final String filename = ProfileDAOlocal.getUserListFilename(sortkey);
		
		// create a bunch of users
		ArrayList<JsonUser> users = new ArrayList<JsonUser> ();
		for(int i =0; i < 4; i++){
			users.add(randomUser().getJsonUser());
		}
		final String response = new Gson().toJson(users);
		
		new AsyncTask<Void, Integer, Void>  (){
			@Override protected Void doInBackground(Void... params) {
				try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
				return null;
			}

			@Override protected void onPostExecute(Void v) {
				listener.onReadUserList(new RawResponse(response, filename));
			}
	
		}.execute();
	}

	@Override protected void readUserInformation(final String path) {
		Loggen.i(this, "Read user info for: " + path);
		
		new AsyncTask<Void, Integer, Void>  (){
			@Override protected Void doInBackground(Void... params) {
				try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
				return null;
			}

			@Override protected void onPostExecute(Void v) {
				
				String response = "{\"id\":9,\"name\":\"wang\",\"password\":\"wss\",\"type\":0," 
						+ "\"sex\":\"男\",\"photo\":\""+ randomPic().getAbsolutePath() +"\","
						+ "\"email\":\"1@qq.com\",\"phonenumber\":\"15810531590\","
						+ "\"jointime\":1385538570000,\"lastLoginTime\":1385538570000,"
						+ "\"activityScore\":10,\"roleId\":1}";
				
				if(counter(10) == 0){
					listener.onReadUserInformation(new RawResponse(RawResponse.Error.SERVER_REPLY, "", "dummyuserinfo"));
				} else {
					listener.onReadUserInformation(new RawResponse(response, "dummyuserinfo"));
				}	
			}
	
		}.execute();
		
	}
	
	public File randomPic(){
		String filename = "user";

		int i = counter(5);
		filename += (i < 3) ? i + ".jpg" : (i < 4) ?  (i - 3) + ".png" : "0.gif";
		
		return new File(cacheDir, filename);
	}
	
	public String randomUserInformation(){

		String response = "{\"id\":9,\"name\":\"wang\",\"password\":\"wss\",\"type\":0," 
				+ "\"sex\":\"男\",\"photo\":\""+ randomPic().getAbsolutePath() +"\","
				+ "\"email\":\"1@qq.com\",\"phonenumber\":\"15810531590\","
				+ "\"jointime\":1385538570000,\"lastLoginTime\":1385538570000,"
				+ "\"activityScore\":10,\"roleId\":1}";
		
		int index = counter(3);
		switch(index){
		case 0:
			response = "{\"id\":9,\"name\":\"George Soros\",\"password\":\"wss\",\"type\":0," 
					+ "\"sex\":\"男\",\"photo\":\""+ randomPic().getAbsolutePath() +"\","
					+ "\"email\":\"1@qq.com\",\"phonenumber\":\"15810531590\","
					+ "\"jointime\":1385538570000,\"lastLoginTime\":1385538570000,"
					+ "\"activityScore\":10,\"roleId\":1}";
		case 1:
			response = "{\"id\":9,\"name\":\"Felix the Cat\",\"password\":\"wss\",\"type\":0," 
					+ "\"sex\":\"女\",\"photo\":\""+ randomPic().getAbsolutePath() +"\","
					+ "\"email\":\"1@qq.com\",\"phonenumber\":\"15810531590\","
					+ "\"jointime\":1385538570000,\"lastLoginTime\":1385538570000,"
					+ "\"activityScore\":10,\"roleId\":1}";
		}
		return response;

	}
	
	public User randomUser(){
		// create a new person using random user info and picture
		JsonUser userinfo = new Gson().fromJson(randomUserInformation(), JsonUser.class);
		User user = new User(userinfo);
		user.setLocalPhoto(randomPic().getAbsolutePath());
		user.setPassword("xxxxx");
		return user;
	}
	
	private int counter(int modulus){
		return (rand.nextInt(100) % modulus);
	}
	
	public String randomString(){
		String [] randomStrings = {"randomString","lalala","好久不见","algodifferente" };
		
		return randomStrings[counter(randomStrings.length)] + counter(50);
	}
	
	@Override protected void readActivityHistory(String email, int size, Page page) {
		
		// Generate a bunch of records
		List <JsonActivityRecord> newrecords = new ArrayList<JsonActivityRecord> ();
		for (int i = 0; i < 5; i++){
			Calendar x = Calendar.getInstance();
			x.set(2000 + i, i % 12, (i * 2 ) % 28);
			JsonActivityRecord r = new JsonActivityRecord();
			r.ahId = counter(20);
			r.whatDo = "a string for " + r.ahId;
			newrecords.add(r);
		}
		
		// create a raw response
		String response = new Gson().toJson(newrecords);
		final RawResponse rawResponse = new RawResponse(response);
		rawResponse.path =  ProfileDAOlocal.getActivityHistoryFilename(email);
		rawResponse.page = page;
		
		// determine the path to send to the server
		new AsyncTask<Void, Integer, Void>  (){
			@Override protected Void doInBackground(Void... params) {
				try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
				return null;
			}

			@Override protected void onPostExecute(Void v) {
				listener.onReadActivityHistory(rawResponse);
			}
		}.execute();
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
	
}
