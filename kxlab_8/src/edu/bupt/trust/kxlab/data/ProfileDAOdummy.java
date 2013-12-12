package edu.bupt.trust.kxlab.data;

import android.os.AsyncTask;
import edu.bupt.trust.kxlab.utils.Loggen;

/**
 * <p>The ProfileDAOdummy generates dummy data to send to the screen.</p>
 * 
 * @author Krishna
 *
 */

class ProfileDAOdummy extends ProfileDAOabstract{
	
	/** <p>Constructor with a listener to send the raw data back to. </p> */
	public ProfileDAOdummy(OnProfileRawDataReceivedListener listener){
		this.listener = listener;
	}

	@Override protected void login(final String path) {
		Loggen.i(this, "Dummy login for: " + path);
		
		new AsyncTask<Void, Integer, Void>  (){
			@Override protected Void doInBackground(Void... params) {
				try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
				return null;
			}

			@Override protected void onPostExecute(Void v) {
				String response = "{\"vote\":[],\"loginOrNot\":1}";
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
				try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
				return null;
			}

			@Override protected void onPostExecute(Void v) {
				String response = "{\"photoImage\":\"www\",\"userName\":\"wang\"," +
						"\"userEmail\":\"1@qq.com\",\"phoneNumber\":\"15810531590\"," +
						"\"timeEnter\":\"2013-10-14\",\"activityScore\":\"10\",\"Source\":\"0\"}";
				String urlFilename = urlToFileName(Urls.build(ProfileDAOweb.urlBase, path));
				listener.onReadUserInformation(new RawResponse(response, urlFilename));
			}
	
		}.execute();
		
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
