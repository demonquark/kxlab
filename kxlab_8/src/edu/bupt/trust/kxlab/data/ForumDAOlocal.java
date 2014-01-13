package edu.bupt.trust.kxlab.data;

import java.io.File;

import edu.bupt.trust.kxlab.data.RawResponse.Page;
import edu.bupt.trust.kxlab.utils.FileManager;
import edu.bupt.trust.kxlab.utils.Gegevens;

import android.content.Context;

public class ForumDAOlocal extends ForumDAOabstract {

	private File cacheDir;

	public ForumDAOlocal(OnForumRawDataReceivedListener listener, Context c){

		cacheDir = FileManager.setCacheDir(c);
		this.listener = listener;
	}
	
	@Override protected void readPostList(String type, int currentSize, Page page) {
		// determine the cache file name
		final String cachefilename = ForumDAOlocal.getPostListFilename(type);
		
		// read from the file
		String response = readFromFile(cachefilename);
		
		// create a response
		RawResponse rawResponse = new RawResponse(response, cachefilename);
		if(response == null){ rawResponse.errorStatus = RawResponse.Error.FILE_NOT_FOUND; }
		rawResponse.page = page;
		
		// send back the response
		listener.onReadPostList(rawResponse);
	}
	
	@Override protected void readPost(String postType, int currentSize, Page page, int postId) { 
		// determine the cache file name
		final String cachefilename = ForumDAOlocal.getPostDetailFilename(postId, postType);
		
		// read from the file
		String response = readFromFile(cachefilename);
		
		// create a response
		RawResponse rawResponse = new RawResponse(response, cachefilename);
		if(response == null){ rawResponse.errorStatus = RawResponse.Error.FILE_NOT_FOUND; }
		rawResponse.page = page;
		
		// send back the response
		listener.onReadPost(rawResponse);
	}

	@Override protected void createPost(String email, String type, String title, String content) {
		listener.onCreatePost(new RawResponse(RawResponse.Error.ILLEGALARGUMENT));
	}

	@Override protected void createReply(String email, String type, int postId, String content) {
		listener.onCreateReply(new RawResponse(RawResponse.Error.ILLEGALARGUMENT));
	}

	@Override protected void createVote(String email, int voteId, int voteScore) {
		listener.onCreateVote(new RawResponse(RawResponse.Error.ILLEGALARGUMENT));
	}

	@Override protected void readAnnounceFAQ(String type, int postId) {
		// determine the cache file name
		final String cachefilename = ForumDAOlocal.getPostDetailFilename(postId, type);
		
		// read from the file
		String response = readFromFile(cachefilename);
		
		// create a response
		RawResponse rawResponse = new RawResponse(response, cachefilename);
		if(response == null){ rawResponse.errorStatus = RawResponse.Error.FILE_NOT_FOUND; }
		
		// send back the response
		listener.onReadAnnounceFAQ(rawResponse);	
	}

	@Override
	protected void readAnnounceList(String type, int currentSize, Page page) {
		// determine the cache file name
		final String cachefilename = ForumDAOlocal.getPostListFilename(type);

		// read from the file
		String response = readFromFile(cachefilename);
		
		// create a response
		RawResponse rawResponse = new RawResponse(response, cachefilename);
		if(response == null){ rawResponse.errorStatus = RawResponse.Error.FILE_NOT_FOUND; }

		// send back the response
		listener.onReadAnnounceList(rawResponse);
	}

	@Override protected void searchPostList(String key, String postType, int currentSize, Page page) {
		// determine the cache file name
		final String cachefilename = ForumDAOlocal.getPostListFilename(postType);
		
		// read from the file
		String response = readFromFile(cachefilename);
		
		// create a response
		RawResponse rawResponse = new RawResponse(response, cachefilename);
		if(response == null){ rawResponse.errorStatus = RawResponse.Error.FILE_NOT_FOUND; }
		rawResponse.page = page;
		
		// send back the response
		listener.onReadPostList(rawResponse);
	}
	
	public String readFromFile(String filename) {
		return FileManager.readFromFile(new File(cacheDir, filename + Gegevens.FILE_EXT_DAT));
	}
	
	public boolean writeToFile(String filename, String string) {
		return FileManager.writeToFile(new File(cacheDir, filename + Gegevens.FILE_EXT_DAT), string);
	}
	
	public static String getPostListFilename(String postType){
		return Urls.filePostList + postType + Gegevens.FILE_EXT_DAT;
	}

	public static String getPostDetailFilename(int postId, String postType){
		return Urls.filePostDetail + postType + "_" + postId + Gegevens.FILE_EXT_DAT;
	}
}
