package edu.bupt.trust.kxlab.data;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import edu.bupt.trust.kxlab.utils.Loggen;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * <p>The ProfileDAOweb manages the profile data from the web.</p>
 * 
 * TODO: Figure out who should check for Internet connection. Currently that is the Activity's responsibility. 
 * Which kind of makes sense, because we need context to determine connectivity...
 * 
 * @author Krishna
 *
 */

class ProfileDAOweb extends ProfileDAOabstract{
	
	// Server URL address strings
	public static final String urlBase 		= Urls.urlBASE;
	
	// HTTP client 
	private AsyncHttpClient asyncHttpClient;
	
	/** <p>Constructor with a listener to send the raw data back to. </p> */
	public ProfileDAOweb(OnProfileRawDataReceivedListener listener){
		
		// make a http client
		asyncHttpClient = new AsyncHttpClient();
		this.listener = listener;
	}

	@Override protected void login(String path) {
		// TODO: figure out who is in charge of creating the path
		Loggen.i(this, "Sending request: " + Urls.build(urlBase, path));
		
		asyncHttpClient.get(Urls.build(urlBase, path), new ProfileResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					listener.onLogin(new RawResponse(response, urlToFileName(getRequestURI().toString()))); } }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					listener.onLogin(new RawResponse(error, content, urlToFileName(getRequestURI().toString()))); } }
		});
	}

	@Override protected void readUsers(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override protected void readUserInformation(String path) {
		// TODO: figure out who is in charge of creating the path
		Loggen.i(this, "Sending request: " + Urls.build(urlBase, path));
		
		asyncHttpClient.get(Urls.build(urlBase, path), new ProfileResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					listener.onReadUserInformation(new RawResponse(response, urlToFileName(getRequestURI().toString()))); } }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					listener.onReadUserInformation(new RawResponse(error, content, urlToFileName(getRequestURI().toString()))); } }
		});
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
	
	/** Check if we have network connectivity. No point in trying anything if we have no connection. */
	protected static boolean isNetworkAvailable(Context c) {
	    ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	
    /**
     * Will encode url, if not disabled, and adds params on the end of it
     *
     * @param url             String with URL, should be valid URL without params
     * @param params          RequestParams to be appended on the end of URL
     * @param shouldEncodeUrl whether url should be encoded (replaces spaces with %20)
     * @return encoded url if requested with params appended if any available
     */
    public static String getPath(boolean shouldEncodeUrl, String path, RequestParams params) {
    	return AsyncHttpClient.getUrlWithQueryString(shouldEncodeUrl, path, params);
    }

	
	/**
	 * Handler for the HTTP response from the server.
	 * This just forwards the HTTP response to the ServicesDAOweb methods
	 * @author Krishna
	 */
	private class ProfileResponseHandler extends AsyncHttpResponseHandler {
		@Override public void onStart() { Loggen.v(this , "onStart: staring async http client."); }
		@Override public void onSuccess(String response) { Loggen.v(this , "onSuccess: success async http client."); }
		@Override public void onFailure(Throwable error, String content) { Loggen.e(this , "onFailure: failed async http client."); }
		@Override public void onFinish() { Loggen.v(this , "onFinish: finish async http client."); }		

	}
}
