package edu.bupt.trust.kxlab.data;

import java.io.File;
import java.util.Random;

import com.google.gson.Gson;

import android.os.AsyncTask;
import android.os.Environment;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.model.UserInformation;
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
				String response = "{\"vote\":[],\"loginOrNot\":" + counter(2) + "}";
				String urlFilename = urlToFileName(Urls.build(ProfileDAOweb.urlBase, path));
				listener.onLogin(new RawResponse(response, urlFilename));
			}
	
		}.execute();
	}

	@Override protected void readUsers(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override protected void readUserInformation(final String path) {
		Loggen.i(this, "Read user info for: " + path);
		
		new AsyncTask<Void, Integer, Void>  (){
			@Override protected Void doInBackground(Void... params) {
				try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
				return null;
			}

			@Override protected void onPostExecute(Void v) {
				String response = "{\"photoImage\":\"www\",\"userName\":\"wang\"," +
						"\"userEmail\":\"1@qq.com\",\"phoneNumber\":\"15810531590\"," +
						"\"timeEnter\":\"2013-10-14\",\"activityScore\":\"10\",\"Source\":\"0\"}";
				String urlFilename = urlToFileName(Urls.build(ProfileDAOweb.urlBase, path));
				
				if(counter(10) == 0){
					listener.onReadUserInformation(new RawResponse(RawResponse.Error.SERVER_REPLY));
				} else {
					listener.onReadUserInformation(new RawResponse(response, urlFilename));
				}	
			}
	
		}.execute();
		
	}
	
	public File randomPic(){
		String filename = "user";

		int i = counter(4);
		filename += (i < 3) ? i + ".jpg" : "0.png";
		
		return new File(cacheDir, filename);
	}
	
	public String randomUserInformation(){

		String response = "{\"photoImage\":\"www\",\"userName\":\"wang\"," +
				"\"userEmail\":\"1@qq.com\",\"phoneNumber\":\"15810531590\"," +
				"\"timeEnter\":\"2013-10-14\",\"activityScore\":\"10\",\"Source\":\"0\"}" ;
		
		int index = counter(3);
		switch(index){
		case 0:
			response = "{\"photoImage\":\"ţ\",\"userName\":\"George Soros\"," +
				"\"userEmail\":\"somewhere@someone.qp\",\"phoneNumber\":\"987234232\"," +
				"\"timeEnter\":\"2013-10-14\",\"activityScore\":\"10\",\"Source\":\"0\"}" ;
		case 1:
			response = "{\"photoImage\":\"ţ\",\"userName\":\"sagas\"," +
					"\"userEmail\":\"3\",\"phoneNumber\":\"12\"," +
					"\"timeEnter\":\"2013-10-14\",\"activityScore\":\"1\",\"Source\":\"1\"}" ;
		}
		return response;

	}
	
	public User randomUser(){
		// create a new person using random user info and picture
		UserInformation userinfo = new Gson().fromJson(randomUserInformation(), UserInformation.class);
		User user = new User(userinfo);
		user.setPhotoLocation(randomPic().getAbsolutePath());
		
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
