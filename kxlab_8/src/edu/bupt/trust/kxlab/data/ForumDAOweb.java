package edu.bupt.trust.kxlab.data;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import edu.bupt.trust.kxlab.data.RawResponse.Page;
import edu.bupt.trust.kxlab.utils.Loggen;

public class ForumDAOweb extends ForumDAOabstract {

	// HTTP client 
	private AsyncHttpClient asyncHttpClient;

	public ForumDAOweb(OnForumRawDataReceivedListener listener){
		asyncHttpClient = new AsyncHttpClient();
		this.listener = listener;
	}
		
	@Override protected void readPostList(String type, int currentSize, final Page page) {

		// determine the path to send to the server
		String path = Urls.build(Urls.urlBASE, Urls.pathForumPostList);
		RequestParams params = new RequestParams();
		params.put(Urls.paramForumPostListSize, String.valueOf(listSize));
		params.put(Urls.paramForumPostListPage, String.valueOf(determinePage(currentSize, page))); 
		params.put(Urls.paramForumPostType, type);
		path = AsyncHttpClient.getUrlWithQueryString(true, path, params);
		
		// determine the cache file name
		final String cachefilename = ForumDAOlocal.getPostListFilename(type);
		
		// Send the request to the server 
		Loggen.v(this, "Sending request: " + path + " | cache: " + cachefilename );
		asyncHttpClient.get(path, new AsyncHttpResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					RawResponse rawResponse = new RawResponse(response, cachefilename);
					rawResponse.page = page;
					listener.onReadPostList(rawResponse);
				} }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					RawResponse rawResponse = new RawResponse(error, content, cachefilename);
					rawResponse.page = page;
					listener.onReadPostList(rawResponse); 
				} }
		});
	}

	@Override protected void readAnnounceList(String type, int currentSize, final Page page) {
		// determine the path to send to the server
		String path = Urls.build(Urls.urlBASE, Urls.pathForumAnnounceorguideList);
		RequestParams params = new RequestParams();
		params.put(Urls.paramForumPostListSize, String.valueOf(listSize));
		params.put(Urls.paramForumPostListPage, String.valueOf(determinePage(currentSize, page))); 
		params.put(Urls.paramForumAgType, type);
		path = AsyncHttpClient.getUrlWithQueryString(true, path, params);
		
		// determine the cache file name
		final String cachefilename = ForumDAOlocal.getPostListFilename(type);
		
		// Send the request to the server 
		Loggen.v(this, "Sending request: " + path + " | cache: " + cachefilename );
		asyncHttpClient.get(path, new AsyncHttpResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					RawResponse rawResponse = new RawResponse(response, cachefilename);
					rawResponse.page = page;
					listener.onReadAnnounceList(rawResponse);
				} }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					RawResponse rawResponse = new RawResponse(error, content, cachefilename);
					rawResponse.page = page;
					listener.onReadAnnounceList(rawResponse); 
				} }
		});
	}
	
	@Override protected void readPost(String postType, int currentSize, final Page page, int postId) { 
	
		// determine the path to send to the server
		String path = Urls.build(Urls.urlBASE, Urls.pathForumPostDetail);
		RequestParams params = new RequestParams();
		params.put(Urls.paramForumPostReplyListSize, String.valueOf(listSize));
		params.put(Urls.paramForumPostReplyListPage, String.valueOf(determinePage(currentSize, page))); 
		params.put(Urls.paramForumPostId, String.valueOf(postId));
		path = AsyncHttpClient.getUrlWithQueryString(true, path, params);
		
		// determine the cache file name
		final String cachefilename = ForumDAOlocal.getPostDetailFilename(postId, postType);
		
		// Send the request to the server 
		Loggen.v(this, "Sending request: " + path + " | cache: " + cachefilename  + " | size =" + currentSize);
		asyncHttpClient.get(path, new AsyncHttpResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					RawResponse rawResponse = new RawResponse(response, cachefilename);
					rawResponse.page = page;
					listener.onReadPost(rawResponse);
				} }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					RawResponse rawResponse = new RawResponse(error, content, cachefilename);
					rawResponse.page = page;
					listener.onReadPost(rawResponse);
				} }
		});
		
	}	
	
	@Override protected void readAnnounceFAQ(String type, int postId) {
		// determine the path to send to the server
		String path = Urls.build(Urls.urlBASE, Urls.pathForumPostAnnounceDetail);
		RequestParams params = new RequestParams();
		params.put(Urls.paramForumPostType, type); 
		params.put(Urls.paramForumPostId, String.valueOf(postId));
		path = AsyncHttpClient.getUrlWithQueryString(true, path, params);
		
		// determine the cache file name
		final String cachefilename = ForumDAOlocal.getPostDetailFilename(postId, type);
		
		// Send the request to the server 
		Loggen.v(this, "Sending request: " + path + " | cache: " + cachefilename );
		asyncHttpClient.get(path, new AsyncHttpResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					listener.onReadAnnounceFAQ(new RawResponse(response, cachefilename));
				} }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					listener.onReadAnnounceFAQ(new RawResponse(error, content, cachefilename));
				} }
		});
	}

	@Override protected void createPost(String email, String type, String title, String content) {
		
		// determine the path to send to the server
		String path = Urls.build(Urls.urlBASE, Urls.pathForumCreatePost);
		RequestParams params = new RequestParams();
		params.put(Urls.paramForumPostTitle, title);
		params.put(Urls.paramForumPostDetail, content); 
		params.put(Urls.paramForumPostType, type);
		params.put(Urls.paramForumEmail, email);
		path = AsyncHttpClient.getUrlWithQueryString(true, path, params);
		
		// Send the request to the server 
		Loggen.v(this, "Sending request: " + path );
		asyncHttpClient.get(path, new AsyncHttpResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					listener.onCreatePost(new RawResponse(response));
				} }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					listener.onCreatePost(new RawResponse(error, content, ""));
				} }
		});
	}

	@Override protected void createReply(String email, String type, int postId, String content) {
		// determine the path to send to the server
		String path = "";
		RequestParams params = new RequestParams();
		if(type == null || type.equals("")){
			// this is a reply to a reply
			path = Urls.build(Urls.urlBASE, Urls.pathForumReplyReply);
			params.put(Urls.paramForumUserEmail, email);
			params.put(Urls.paramForumReplyDetail, content); 
			params.put(Urls.paramForumReReplyId, String.valueOf(postId));
		} else {
			// this is a reply to a post
			path = Urls.build(Urls.urlBASE, Urls.pathForumReplyPost);
			params.put(Urls.paramForumEmail, email);
			params.put(Urls.paramForumPostDetail, content); 
			params.put(Urls.paramForumPostType, type);
			params.put(Urls.paramForumPostId, String.valueOf(postId));
		}
		path = AsyncHttpClient.getUrlWithQueryString(true, path, params);
		
		// Send the request to the server 
		Loggen.v(this, "Sending request: " + path );
		asyncHttpClient.get(path, new AsyncHttpResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					listener.onCreateReply(new RawResponse(response));
				} }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					listener.onCreateReply(new RawResponse(error, content, ""));
				} }
		});
	}

	@Override protected void createVote(String email, int voteId, int voteScore) {
		// determine the path to send to the server
		String path = Urls.build(Urls.urlBASE, Urls.pathForumVoteUser);
		RequestParams params = new RequestParams();
		params.put(Urls.paramForumVoteId, String.valueOf(voteId));
		params.put(Urls.paramForumVoteScore, String.valueOf(voteScore)); 
		params.put(Urls.paramForumEmail, email);
		path = AsyncHttpClient.getUrlWithQueryString(true, path, params);
		
		// Send the request to the server 
		Loggen.v(this, "Sending request: " + path );
		asyncHttpClient.get(path, new AsyncHttpResponseHandler(){
			@Override public void onSuccess(String response) {
				if(listener != null){
					listener.onCreatePost(new RawResponse(response));
				} }
			@Override public void onFailure(Throwable error, String content) {
				if(listener != null){
					listener.onCreatePost(new RawResponse(error, content, ""));
				} }
		});

	}

	@Override protected void searchPostList(String path) {
	}
}
