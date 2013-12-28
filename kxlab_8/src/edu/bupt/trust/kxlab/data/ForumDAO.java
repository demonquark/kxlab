package edu.bupt.trust.kxlab.data;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.content.Context;
import edu.bupt.trust.kxlab.data.RawResponse.Page;
import edu.bupt.trust.kxlab.data.DaoFactory.Source;
import edu.bupt.trust.kxlab.jsonmodel.JsonPost;
import edu.bupt.trust.kxlab.jsonmodel.JsonReply;
import edu.bupt.trust.kxlab.jsonmodel.WebPostForumDetail;
import edu.bupt.trust.kxlab.model.Post;
import edu.bupt.trust.kxlab.model.PostType;
import edu.bupt.trust.kxlab.model.Reply;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.Loggen;

public class ForumDAO implements ForumDAOabstract.OnForumRawDataReceivedListener{
	
	ForumDAOdummy dummy;
	ForumDAOlocal local;
	WeakReference <ForumListener> forumlistener;
	
	// Outward facing methods (used by the class requesting the data)
	public void setForumListener(ForumListener listener) { 
		this.forumlistener = new WeakReference <ForumListener> (listener); 
	}
	
	// Inward facing methods (used to communicate with the class providing the data)
	protected ForumDAO(Context c, ForumListener listener){
		dummy = new ForumDAOdummy(this);
		local = new ForumDAOlocal(this);
		this.forumlistener = new WeakReference <ForumListener> (listener);
	}

	/**
	 * Get a list of post objects 
	 * The list of posts is saved to cache and loaded if a future web request fails.
	 * @param source can be DUMMY, WEB, LOCAL
	 * @param type an instance of Post type
	 */
	public void readPostList(Source source, PostType type, Page page){ 
		// determine the path to send to the server
		dummy.readPostList("forum");
	}

	/**
	 * Get a list of post objects 
	 * The list of posts is saved to cache and loaded if a future web request fails.
	 * @param source can be DUMMY, WEB, LOCAL
	 * @param type an instance of Post type
	 */
	public void readPost(Source source, Post post, ArrayList<Reply> replies, Page page){ 

		// save the current page to the cache 
		if(page != Page.CURRENT || (source == Source.WEB && replies != null ) ){ 
			Loggen.v(this, "saved content to file.");
			overwriteWebPostForumDetail(post, replies); }
		
		// send the request to the right DAO
		dummy.readPost(Urls.fileWebpostforumdetail, page);
		
	}
	
	private void overwriteWebPostForumDetail(Post post, ArrayList<Reply> replies){

		// create a JSON representation of the replies
		ArrayList <JsonReply> jsonreplies = new ArrayList<JsonReply> ();
		if(replies != null){ for(Reply reply : replies){ jsonreplies.add(reply.getJsonReply()); } }

		Loggen.i(this, "Start writing to file.");
		
		// create a JSON representation of the current forum detail page.
		WebPostForumDetail oldDetails = new WebPostForumDetail(
				post.getJsonPost(), 
				post.getPostSponsor().getJsonUser(), 
				jsonreplies);
		
		// write to file
		local.writeToFile(Urls.fileWebpostforumdetail, 
				new GsonBuilder().serializeNulls().create().toJson(oldDetails));
		
		Loggen.i(this, "Done writing to file.");

	} 
	
	
	

	@Override public void onReadPostList(RawResponse response) {
		Loggen.v(this, "Got a response onReadPostList: " + response.message);
		
		ArrayList <Post> posts = null;
		
		if(response.errorStatus == RawResponse.Error.NONE){
			try{
				// TODO: first save the data to the cache
				if (response.path != null && response.message != null) { }
				
				// Generic Gson instance used for conversion
				Gson gson = new Gson();

				// Step 1 - convert the message into a JSON object
				JsonElement je = new JsonParser().parse(response.message);
				JsonObject jobj = je.getAsJsonObject();
				
				// Step 2 - convert the JSON object into a list of post objects
				JsonElement pl = jobj.get(Urls.jsonPostList); // get post list
				if(pl != null ) {
					// Step 2a - read the object as an array
					JsonArray pla = pl.getAsJsonArray();
					posts = new ArrayList <Post> ();
					
					// Step 2b - convert each element in the JSON array to post and add it to the list 
					for(JsonElement ele : pla){
						posts.add(new Post (gson.fromJson(ele, JsonPost.class)));
					}
				}
				
			}catch(Exception e){
				// If an error occurs while parsing the message, just stop and reply with what we've got.
				Loggen.e(this, "Error (" + e.toString() + ") while parsing " + response.message);
			}
		} else {
			Loggen.e(this, "We encountered an error onReadPostList: " + response.errorStatus.toString());
		}
		
		// send the user back
		if (forumlistener.get() != null){ forumlistener.get().onReadPostList(posts); }
		
	}

	@Override
	public void onCreatePost(RawResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCreateReply(RawResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCreateVote(RawResponse response) {
		// TODO Auto-generated method stub
		
	}


	@Override public void onReadPost(RawResponse response) {

		// initial results
		ArrayList<Reply> replies = null;
		Post post = null;
		Gson gson = new GsonBuilder().serializeNulls().create();
		
		// convert the provided JSON string to a JSON object
		if(response.errorStatus == RawResponse.Error.NONE){
			try{
				WebPostForumDetail newPost = null;
				if(response.message != null){
					newPost = gson.fromJson(response.message,WebPostForumDetail.class);
				} else {
					newPost = gson.fromJson(local.readFromFile(response.path), WebPostForumDetail.class);
				}
				
				// update the local file with the content we have now.
				if(response.page != Page.CURRENT){
				
					Loggen.i(this, "Start getting old post.");

					// read and update the existing post from cache. 
					WebPostForumDetail oldPost = 
							gson.fromJson(local.readFromFile(response.path), WebPostForumDetail.class);

					Loggen.i(this, "End getting old post. " + oldPost.getPostDetail().getEmail());
					newPost.updateWithNew(oldPost, response.page == Page.LATEST);
					Loggen.i(this, "End post");
				}
	
				// save the values to file
				local.writeToFile(response.path, gson.toJson(newPost));
				
				// TODO: Figure out when to get overlap?
				
				// get the post from the data
				post = new Post(newPost.getPostDetail());
				post.setPostSponsor(new User(newPost.getPostSponsor()));
				replies = new ArrayList<Reply> ();
				
				// get the replies from the data
				List<JsonReply> jsonreplies = newPost.getPostReply();
				for(JsonReply reply : jsonreplies){
					replies.add(new Reply(reply));
				}
			}catch(Exception e){
				// If an error occurs while parsing the message, just stop and reply with what we've got.
				Loggen.e(this, "Error (" + e.toString() + ") while parsing " + response.message);
			}

		}
		
		// return the information to the listener
		if (forumlistener.get() != null){ forumlistener.get().onReadPost(post, replies); }
	}

	@Override
	public void onReadAnnounceFAQ(RawResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSearchPostList(RawResponse response) {
		// TODO Auto-generated method stub
		
	}	
	
	public interface ForumListener {
		void onCreatePost(boolean success);
		void onCreateReply(boolean success);
		void onCreateVote(boolean success);
		void onReadPostList(List <Post> posts);
		void onReadPost(Post post, List <Reply> replies);
		void onReadAnnounceFAQ(Post post);
		void onSearchPostList(List <Post> posts);
	}
}
