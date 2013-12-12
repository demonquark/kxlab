package edu.bupt.trust.kxlab.data;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * <p>The ServicesDAOweb attempts to get the services from the web.</p>
 * 
 * TODO: Figure out who should check for Internet connection. Currently that is the Activity's responsibility. 
 * Which kind of makes sense, because we need context to determine connectivity...
 * 
 * @author Krishna
 *
 */

class ServicesDAOweb extends ServicesDAOabstract{
	
	// Server URL address strings
	private String urlBase 		= Urls.urlBASE;
	
	// HTTP client 
	private AsyncHttpClient asyncHttpClient;
	
	/** <p>Constructor with a listener to send the raw data back to. </p> */
	public ServicesDAOweb(OnServicesRawDataReceivedListener listener){
		
		// make a http client
		asyncHttpClient = new AsyncHttpClient();
		this.listener = listener;
	}

	@Override protected void readServices(String path) {
		// Send the request to the server 
		Log.i("Kris", "Sending request: " + Urls.build(urlBase, path));
		asyncHttpClient.get(Urls.build(urlBase, path), new ServicesResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					listener.onReadServices(new RawResponse(response, urlToFileName(getRequestURI().toString()))); } }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					listener.onReadServices(new RawResponse(error, content, urlToFileName(getRequestURI().toString()))); } }
		});
	}

	@Override protected void readService(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateServiceScore(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createServiceComment(String path) {
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
	private class ServicesResponseHandler extends AsyncHttpResponseHandler {
		@Override public void onStart() { Log.v("DAOweb" , "onStart: not implemented."); }
		@Override public void onSuccess(String response) { Log.v("DAOweb" , "onSuccess: not implemented."); }
		@Override public void onFailure(Throwable error, String content) { Log.e("DAOweb" , "onFailure: not implemented."); }
		@Override public void onFinish() { Log.v("DAOweb" , "onFinish: not implemented."); }		

	} 
}
