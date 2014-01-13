package edu.bupt.trust.kxlab.data;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import edu.bupt.trust.kxlab.data.RawResponse.Page;
import edu.bupt.trust.kxlab.utils.Loggen;

/**
 * <p>The ProfileDAOweb manages the profile data from the web.</p>
 *  
 * Which kind of makes sense, because we need context to determine connectivity...
 * 
 * @author Krishna
 *
 */

class ProfileDAOweb extends ProfileDAOabstract{
	
	// HTTP client 
	private AsyncHttpClient asyncHttpClient;
	
	/** <p>Constructor with a listener to send the raw data back to. </p> */
	public ProfileDAOweb(OnProfileRawDataReceivedListener listener){
		
		// make an HTTP client
		asyncHttpClient = new AsyncHttpClient();
		this.listener = listener;
	}

	@Override protected void login(String email, String password) {
		// determine the path to send to the server
		String path = Urls.build(Urls.urlBASE, Urls.pathProfileLogin);
		RequestParams params = new RequestParams();
		params.put(Urls.paramEmail, email);
		params.put(Urls.paramProfilePassword, password);
		path = AsyncHttpClient.getUrlWithQueryString(true, path, params);
		
		Loggen.v(this, "Sending request: " + path);
		asyncHttpClient.get(path, new AsyncHttpResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					listener.onLogin(new RawResponse(response, urlToFileName(getRequestURI().toString()))); } }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					listener.onLogin(new RawResponse(error, content, urlToFileName(getRequestURI().toString()))); } }
		});
	}

	@Override protected void readUsers(String sortkey, int size, final Page page) {

		// build the query
		String path = Urls.build(Urls.urlBASE, Urls.pathProfileuserList);
		RequestParams params = new RequestParams();
		params.put(Urls.paramProfileUserListSortKey, sortkey != null ? sortkey : "");
		params.put(Urls.paramProfileUserListSize, String.valueOf(listSize));
		params.put(Urls.paramProfileUserListPage, String.valueOf(determinePage(size, page))); 
		path = AsyncHttpClient.getUrlWithQueryString(true, path, params);
		
		// determine the cache file name
		final String cachefilename = ProfileDAOlocal.getUserListFilename(sortkey);
		
		Loggen.v(this, "Sending request: " + path);
		asyncHttpClient.get(path, new AsyncHttpResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					RawResponse rawResponse = new RawResponse(response, cachefilename);
					rawResponse.page = page;
					listener.onReadUserList(rawResponse); } }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					RawResponse rawResponse = new RawResponse(error, content, cachefilename);
					rawResponse.page = page;
					listener.onReadUserList(rawResponse); } }
		});	
	}

	@Override protected void readUserInformation(String email) {

		// build the query
		String path = Urls.build(Urls.urlBASE, Urls.pathProfileUserInfo);
		RequestParams params = new RequestParams();
		params.put(Urls.paramEmail, email != null ? email : "");
		path = AsyncHttpClient.getUrlWithQueryString(false, path, params);
		
		// determine the cache file name
		final String cachefilename = ProfileDAOlocal.getUserInformationFilename(email);
		
		Loggen.v(this, "Sending request: " + path);
		asyncHttpClient.get(path, new AsyncHttpResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					listener.onReadUserInformation(new RawResponse(response, cachefilename)); } }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					listener.onReadUserInformation(new RawResponse(error, content, cachefilename)); } }
		});
	}

	@Override protected void readActivityHistory(String email, int size, final Page page) {
		// build the query
		String path = Urls.build(Urls.urlBASE, Urls.pathProfileHistory);
		RequestParams params = new RequestParams();
		params.put(Urls.paramEmail, email != null ? email : "");
		params.put(Urls.paramProfileListSize, String.valueOf(listSize));
		params.put(Urls.paramProfileListPage, String.valueOf(determinePage(size, page))); 
		path = AsyncHttpClient.getUrlWithQueryString(true, path, params);
		
		// determine the cache file name
		final String cachefilename = ProfileDAOlocal.getActivityHistoryFilename(email);
		
		Loggen.v(this, "Sending request: " + path);
		asyncHttpClient.get(path, new AsyncHttpResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					RawResponse rawResponse = new RawResponse(response, cachefilename);
					rawResponse.page = page;
					listener.onReadActivityHistory(rawResponse); } }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					RawResponse rawResponse = new RawResponse(error, content, cachefilename);
					rawResponse.page = page;
					listener.onReadActivityHistory(rawResponse); } }
		});	
	}
	
	@Override protected void changePhoto(String email, String photo) {

		// build the query
		String path = Urls.build(Urls.urlBASE, Urls.pathProfileChangePhoto);
		RequestParams params = new RequestParams();
		params.put(Urls.paramEmail, email != null ? email : "");
		params.put(Urls.paramProfilePhotoImage, photo != null ? photo : "");
		path = AsyncHttpClient.getUrlWithQueryString(true, path, params);
		
		// determine the cache file name
		final String cachefilename = ProfileDAOlocal.getOldUserFilename(email);
		
		Loggen.v(this, "Sending request: " + path);
		asyncHttpClient.get(path, new AsyncHttpResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					listener.onChangePhoto(new RawResponse(response, cachefilename)); } }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					listener.onChangePhoto(new RawResponse(error, content, cachefilename)); } }
		});	
	}

	@Override protected void changePassword(String email, String password, String newPassword) {
		// build the query
		String path = Urls.build(Urls.urlBASE, Urls.pathProfileChangePassword);
		RequestParams params = new RequestParams();
		params.put(Urls.paramEmail, email != null ? email : "");
		params.put(Urls.paramProfilePassword, password != null ? password : "");
		params.put(Urls.paramProfileNewPassword, newPassword != null ? newPassword : "");
		path = AsyncHttpClient.getUrlWithQueryString(true, path, params);
		
		// determine the cache file name
		final String cachefilename = ProfileDAOlocal.getOldUserFilename(email);
		
		Loggen.v(this, "Sending request: " + path);
		asyncHttpClient.get(path, new AsyncHttpResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					listener.onChangePassword(new RawResponse(response, cachefilename)); } }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					listener.onChangePassword(new RawResponse(error, content, cachefilename)); } }
		});	
	}

	@Override protected void changePhonenumber(String email, String phonenumber) {
		// build the query
		String path = Urls.build(Urls.urlBASE, Urls.pathProfileChangePhone);
		RequestParams params = new RequestParams();
		params.put(Urls.paramEmail, email != null ? email : "");
		params.put(Urls.paramProfilePhoneNumber, phonenumber != null ? phonenumber : "");
		path = AsyncHttpClient.getUrlWithQueryString(true, path, params);
		
		// determine the cache file name
		final String cachefilename = ProfileDAOlocal.getOldUserFilename(email);
		
		Loggen.v(this, "Sending request: " + path);
		asyncHttpClient.get(path, new AsyncHttpResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					listener.onChangePhonenumber(new RawResponse(response, cachefilename)); } }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					listener.onChangePhonenumber(new RawResponse(error, content, cachefilename)); } }
		});	
	}

	@Override protected void changeSource(String email, int type) {
		// build the query
		String path = Urls.build(Urls.urlBASE, Urls.pathProfileChangeSource);
		RequestParams params = new RequestParams();
		params.put(Urls.paramEmail, email != null ? email : "");
		params.put(Urls.paramProfileSource, String.valueOf(type));
		path = AsyncHttpClient.getUrlWithQueryString(true, path, params);
		
		// determine the cache file name
		final String cachefilename = ProfileDAOlocal.getOldUserFilename(email);
		
		Loggen.v(this, "Sending request: " + path);
		asyncHttpClient.get(path, new AsyncHttpResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					listener.onChangeSource(new RawResponse(response, cachefilename)); } }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					listener.onChangeSource(new RawResponse(error, content, cachefilename)); } }
		});	
		
	}
}
