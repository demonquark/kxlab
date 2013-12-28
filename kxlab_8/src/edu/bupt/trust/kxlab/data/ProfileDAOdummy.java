package edu.bupt.trust.kxlab.data;

import java.io.File;
import java.util.Random;

import com.google.gson.Gson;

import android.os.AsyncTask;
import android.os.Environment;
import edu.bupt.trust.kxlab.jsonmodel.JsonUser;
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

	@Override protected void login(final String path) {
		Loggen.i(this, "Dummy login for: " + path);
		
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

	@Override protected void readUsers(String path) {
		Loggen.i(this, "Dummy read user lists for: " + path);
		
		new AsyncTask<Void, Integer, Void>  (){
			@Override protected Void doInBackground(Void... params) {
				try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
				return null;
			}

			@Override protected void onPostExecute(Void v) {
				listener.onReadUserList(new RawResponse("", "dummyuserslist"));
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
						+ "\"sex\":\"ÄÐ\",\"photo\":\""+ randomPic().getAbsolutePath() +"\","
						+ "\"email\":\"1@qq.com\",\"phonenumber\":\"15810531590\","
						+ "\"jointime\":\"2013-10-14\",\"lastLoginTime\":\"2013-10-14\","
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
				+ "\"sex\":\"ÄÐ\",\"photo\":\""+ randomPic().getAbsolutePath() +"\","
				+ "\"email\":\"1@qq.com\",\"phonenumber\":\"15810531590\","
				+ "\"jointime\":\"2013-10-14\",\"lastLoginTime\":\"2013-10-14\","
				+ "\"activityScore\":10,\"roleId\":1}";
		
		int index = counter(3);
		switch(index){
		case 0:
			response = "{\"id\":9,\"name\":\"George Soros\",\"password\":\"wss\",\"type\":0," 
					+ "\"sex\":\"ÄÐ\",\"photo\":\""+ randomPic().getAbsolutePath() +"\","
					+ "\"email\":\"watchalooking@qq.com\",\"phonenumber\":\"15810531590\","
					+ "\"jointime\":\"2013-10-14\",\"lastLoginTime\":\"2013-10-14\","
					+ "\"activityScore\":10,\"roleId\":1}";
		case 1:
			response = "{\"id\":9,\"name\":\"Felix the Cat\",\"password\":\"wss\",\"type\":0," 
					+ "\"sex\":\"Å®\",\"photo\":\""+ randomPic().getAbsolutePath() +"\","
					+ "\"email\":\"coolcat@places.com\",\"phonenumber\":\"15810531590\","
					+ "\"jointime\":\"2013-10-14\",\"lastLoginTime\":\"2013-10-14\","
					+ "\"activityScore\":10,\"roleId\":1}";
		}
		return response;

	}
	
	public User randomUser(){
		// create a new person using random user info and picture
		JsonUser userinfo = new Gson().fromJson(randomUserInformation(), JsonUser.class);
		User user = new User(userinfo);
		user.setPhotoLocation(randomPic().getAbsolutePath());
		user.setPassword("xxxxx");
		return user;
	}
	
	private int counter(int modulus){
		return (rand.nextInt(100) % modulus);
	}
	
	@Override protected void readActivityHistory(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override protected void changePhoto(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override protected void changePassword(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override protected void changePhonenumber(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override protected void changeSource(String path) {
		// TODO Auto-generated method stub
		
	} 
}
