package edu.bupt.trust.kxlab.data;

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

	@Override
	protected void createService(String path) {
		// TODO Auto-generated method stub
		Loggen.v(this, "Sending request: " + Urls.build(urlBase, path));
		asyncHttpClient.get(Urls.build(urlBase, path), new ServicesResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					listener.onCreateService(new RawResponse(response, urlToFileName(getRequestURI().toString()))); } }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					listener.onCreateService(new RawResponse(error, content, urlToFileName(getRequestURI().toString()))); } }
		});
	}

	@Override
	protected void deleteService(String path) {
		// TODO Auto-generated method stub
		Loggen.v(this, "Sending request: " + Urls.build(urlBase, path));
		asyncHttpClient.get(Urls.build(urlBase, path), new ServicesResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					listener.onDeleteService(new RawResponse(response, urlToFileName(getRequestURI().toString()))); } }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					listener.onDeleteService(new RawResponse(error, content, urlToFileName(getRequestURI().toString()))); } }
		});
	}
	
	public void editService(String path) {
		// TODO Auto-generated method stub
		Loggen.v(this, "Sending request: " + Urls.build(urlBase, path));
		asyncHttpClient.get(Urls.build(urlBase, path), new ServicesResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					listener.onEditService(new RawResponse(response, urlToFileName(getRequestURI().toString()))); } }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					listener.onEditService(new RawResponse(error, content, urlToFileName(getRequestURI().toString()))); } }
		});
	}

	@Override 
	protected void readServices(String email, ServiceFlavor flavor, ServiceType type, int size, final Page page) {

		// build the query
		String path = Urls.build(Urls.urlBASE, 
				(flavor == ServiceFlavor.MYSERVICE ) ?  Urls.pathMyServiceList: Urls.pathServiceList);
		RequestParams params = new RequestParams();
		params.put(Urls.paramUserEmail, email != null ? email : "");
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
	
	@Override
	protected void searchService(String path) {
		// TODO Auto-generated method stub
		Loggen.v(this, "Sending request: " + Urls.build(urlBase, path));
		asyncHttpClient.get(Urls.build(urlBase, path), new ServicesResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					listener.onSearchServices(new RawResponse(response, urlToFileName(getRequestURI().toString()))); } }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					listener.onSearchServices(new RawResponse(error, content, urlToFileName(getRequestURI().toString()))); } }
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
	
	@Override
	public void updateServiceScore(String path) {
		// TODO Auto-generated method stub
		Loggen.v(this, "Sending request: " + Urls.build(urlBase, path));
		asyncHttpClient.get(Urls.build(urlBase, path), new ServicesResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					listener.writeServiceScore(new RawResponse(response, urlToFileName(getRequestURI().toString()))); } }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					listener.writeServiceScore(new RawResponse(error, content, urlToFileName(getRequestURI().toString()))); } }
		});
	}

	@Override
	protected void createServiceComment(String path) {
		// TODO Auto-generated method stub
		Loggen.v(this, "Sending request: " + Urls.build(urlBase, path));
		asyncHttpClient.get(Urls.build(urlBase, path), new ServicesResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					listener.writeServiceComment(new RawResponse(response, urlToFileName(getRequestURI().toString()))); } }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					listener.writeServiceComment(new RawResponse(error, content, urlToFileName(getRequestURI().toString()))); } }
		});
	}
	
	/**
	 * Handler for the HTTP response from the server.
	 * This just forwards the HTTP response to the ServicesDAOweb methods
	 * @author Krishna
	 */
	private class ServicesResponseHandler extends AsyncHttpResponseHandler {
		@Override public void onStart() { Loggen.v(this , "onStart: not implemented."); }
		@Override public void onSuccess(String response) { Loggen.v(this , "onSuccess: not implemented."); }
		@Override public void onFailure(Throwable error, String content) { Loggen.v(this, "onFailure: not implemented."); }
		@Override public void onFinish() {Loggen.v(this , "onFinish: not implemented."); }		

	}
}
