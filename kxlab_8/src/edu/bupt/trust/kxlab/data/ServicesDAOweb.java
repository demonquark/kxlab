package edu.bupt.trust.kxlab.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import edu.bupt.trust.kxlab.data.RawResponse.Page;
import edu.bupt.trust.kxlab.model.ServiceFlavor;
import edu.bupt.trust.kxlab.model.ServiceType;
import edu.bupt.trust.kxlab.utils.Loggen;

/**
 * <p>The ServicesDAOweb attempts to get the services from the web.</p>
 * @author Krishna
 *
 */

class ServicesDAOweb extends ServicesDAOabstract{
	
	// HTTP client 
	private AsyncHttpClient asyncHttpClient;
	
	/** <p>Constructor with a listener to send the raw data back to. </p> */
	public ServicesDAOweb(OnServicesRawDataReceivedListener listener){
		
		// make a http client
		asyncHttpClient = new AsyncHttpClient();
		this.listener = listener;
	}

	@Override protected void createService(final String email, final int id, 
			final String title, final String detail, final String photo) {

		Loggen.d(this, "request to create a new service");
		
		new AsyncTask<Void, Integer, String>  () {
			@Override protected String doInBackground(Void... params) {
				String url = Urls.build(Urls.urlBASE, Urls.pathMyServiceCreate);
				return sendMultiPart(url, email, id, -1, title, detail, photo);
			}

			@Override protected void onPostExecute(String response) {
				listener.onCreateService(new RawResponse(response));
			}
		}.execute();
	}

	@Override protected void deleteService(int serviceId) {
		// build the query
		String path = Urls.build(Urls.urlBASE, Urls.pathMyServiceDelete);
		RequestParams params = new RequestParams();
		params.put(Urls.paramServiceId, String.valueOf(serviceId));
		path = AsyncHttpClient.getUrlWithQueryString(true, path, params);
		
		Loggen.v(this, "Sending request: " + path);
		asyncHttpClient.get(path, new AsyncHttpResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					RawResponse rawResponse = new RawResponse(response);
					listener.onDeleteService(rawResponse); } }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					RawResponse rawResponse = new RawResponse(error, content, null);
					listener.onDeleteService(rawResponse); } }
		});	
	}
	
	@Override public void editService(final int id, 
			final String title, final String detail, final String photo) {

		
		new AsyncTask<Void, Integer, String>  () {
			@Override protected String doInBackground(Void... params) {
				String url = Urls.build(Urls.urlBASE, Urls.pathMyServiceEdit);
				return sendMultiPart(url, null, -1, id, title, detail, photo);
			}

			@Override protected void onPostExecute(String response) {
				listener.onEditService(new RawResponse(response));
			}
		}.execute();
	}

	private String sendMultiPart(String url, final String email, 
			int servicetype, int serviceid, String title, String detail, String photo){

	    HttpClient httpClient = new DefaultHttpClient();
	    HttpPost httpPost = new HttpPost(url);
	    String response = null;

	    // Add the HTTP entity items
    	MultipartEntityBuilder builder = MultipartEntityBuilder.create(); 
    	if(email != null){ builder.addTextBody(Urls.paramUserEmail, String.valueOf(email)); }
    	if(servicetype >= 0){ builder.addTextBody(Urls.paramServiceType, String.valueOf(servicetype)); }
    	if(serviceid >= 0){ builder.addTextBody(Urls.paramServiceId, String.valueOf(serviceid)); }
    	builder.addTextBody(Urls.paramServiceTitle, String.valueOf(title));
    	builder.addTextBody(Urls.paramServiceDetial, String.valueOf(detail));
    	File photoFile = new File(photo);
    	if(photoFile.exists()){
    		builder.addBinaryBody(Urls.paramServicePhoto, photoFile);
    	}
    	
    	// Create a post object
        httpPost.setEntity(builder.build());
	    
        try{
        	// send the post object
	        HttpResponse res = httpClient.execute(httpPost);
            
	        // Get the response
	        HttpEntity resEntity = res.getEntity();
	        
	        // read the response into a string
            String sResponse;
	        BufferedReader reader = new BufferedReader(new InputStreamReader(resEntity.getContent(), "UTF-8"));
            StringBuilder s = new StringBuilder();
            while ((sResponse = reader.readLine()) != null) {
                s = s.append(sResponse);
            }
            
            // save it as the return value
            response = s.toString();
	        
	    } catch (ClientProtocolException e) { e.printStackTrace(); Loggen.d(this, e.toString()); 
	    } catch (IOException e){ e.printStackTrace(); Loggen.d(this, e.toString()); }
        
        return response;
	}
	
	// TODO DELETE!!!!!!
	public void OLDeditService(int id, String title, String detail, String photo) {
		// build the query
		String path = Urls.build(Urls.urlBASE, Urls.pathMyServiceEdit);
		RequestParams params = new RequestParams();
		params.put(Urls.paramServiceId, String.valueOf(id));
		params.put(Urls.paramServiceTitle, String.valueOf(title));
		params.put(Urls.paramServiceDetial, String.valueOf(detail));
		params.put(Urls.paramServicePhoto, String.valueOf(photo));
		path = AsyncHttpClient.getUrlWithQueryString(true, path, params);
		
		Loggen.v(this, "Sending request: " + path);
		asyncHttpClient.get(path, new AsyncHttpResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					RawResponse rawResponse = new RawResponse(response);
					listener.onEditService(rawResponse); } }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					RawResponse rawResponse = new RawResponse(error, content, null);
					listener.onEditService(rawResponse); } }
		});	
	}

	@Override 
	protected void readServices(String email, String key, ServiceFlavor flavor, ServiceType type, int size, final Page page) {

		// build the query
		String path = "";
		if(flavor == ServiceFlavor.MYSERVICE){
			path = Urls.build(Urls.urlBASE, (key != null ) ?  Urls.pathMyServiceSearch: Urls.pathMyServiceList);
		} else {
			path = Urls.build(Urls.urlBASE, (key != null ) ?  Urls.pathSearchServiceList: Urls.pathServiceList);
		}
		
		// build the query
		RequestParams params = new RequestParams();
		if(flavor == ServiceFlavor.MYSERVICE) { params.put(Urls.paramUserEmail, email != null ? email : ""); }
		if(key != null) { params.put(Urls.paramServiceSearchKey, key); }
		params.put(Urls.paramServiceType, String.valueOf(type.getServerType()));
		params.put(Urls.paramServiceListSize, String.valueOf(listSize));
		params.put(Urls.paramServiceListPage, String.valueOf(determinePage(size, page))); 
		path = AsyncHttpClient.getUrlWithQueryString(true, path, params);
		
		// determine the cache file name
		final String cachefilename = ServicesDAOlocal.getServicesListFilename(type.getFragName(), flavor.toString());
		
		Loggen.v(this, "Sending request: " + path);
		asyncHttpClient.get(path, new AsyncHttpResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					RawResponse rawResponse = new RawResponse(response, cachefilename);
					rawResponse.page = page;
					listener.onReadServices(rawResponse); } }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					RawResponse rawResponse = new RawResponse(error, content, cachefilename);
					rawResponse.page = page;
					listener.onReadServices(rawResponse); } }
		});	
	}

	@Override protected void readService(int id, int size, final Page page) {

		// build the query
		String path = Urls.build(Urls.urlBASE, Urls.pathServiceDetail);
		RequestParams params = new RequestParams();
		params.put(Urls.paramServiceId, String.valueOf(id));
		params.put(Urls.paramCommentListSize, String.valueOf(listSize));
		params.put(Urls.paramCommentListPage, String.valueOf(determinePage(size, page))); 
		path = AsyncHttpClient.getUrlWithQueryString(true, path, params);
		
		// determine the cache file name
		final String cachefilename = ServicesDAOlocal.getServicesDetailFilename(id);
		
		Loggen.v(this, "Sending request: " + path);
		asyncHttpClient.get(path, new AsyncHttpResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					RawResponse rawResponse = new RawResponse(response, cachefilename);
					rawResponse.page = page;
					listener.onReadService(rawResponse); } }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					RawResponse rawResponse = new RawResponse(error, content, cachefilename);
					rawResponse.page = page;
					listener.onReadService(rawResponse); } }
		});	
	}	
	
	@Override public void updateServiceScore(int serviceId, String userMail, int score) {
		// build the query
		String path = Urls.build(Urls.urlBASE, Urls.pathServiceScore);
		RequestParams params = new RequestParams();
		params.put(Urls.paramUserEmail, userMail != null ? userMail : "");
		params.put(Urls.paramServiceId, String.valueOf(serviceId));
		params.put(Urls.paramCommentScore, String.valueOf(score));
		path = AsyncHttpClient.getUrlWithQueryString(true, path, params);
		
		Loggen.v(this, "Sending request: " + path);
		asyncHttpClient.get(path, new AsyncHttpResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					listener.onUpdateServiceScore(new RawResponse(response)); } }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					listener.onUpdateServiceScore(new RawResponse(error, content, null)); } }
		});	
	}

	@Override
	protected void createServiceComment(int serviceId, String userMail, int rootcommentid, String comment) {
		// build the query
		String path = Urls.build(Urls.urlBASE, (rootcommentid < 0) ? Urls.pathServiceComment : Urls.pathServiceReplyComment);
		
		RequestParams params = new RequestParams();
		params.put(Urls.paramUserEmail, userMail != null ? userMail : "");
		
		if(rootcommentid < 0){
			params.put(Urls.paramServiceId, String.valueOf(serviceId));
			params.put(Urls.paramServiceComment, comment != null ? comment : "");
		} else {
			params.put(Urls.paramRecommentid, String.valueOf(rootcommentid));
			params.put(Urls.paramCommentdetail, comment != null ? comment : "");
		}
		
		path = AsyncHttpClient.getUrlWithQueryString(true, path, params);
		
		Loggen.v(this, "Sending request: " + path);
		asyncHttpClient.get(path, new AsyncHttpResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					listener.onCreateComment(new RawResponse(response)); } }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					listener.onCreateComment(new RawResponse(error, content, null)); } }
		});	
	}
}
