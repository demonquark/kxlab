package edu.bupt.trust.kxlab.data;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.content.Context;
import edu.bupt.trust.kxlab.data.DaoFactory.Source;
import edu.bupt.trust.kxlab.jsonmodel.JsonPost;
import edu.bupt.trust.kxlab.model.Post;
import edu.bupt.trust.kxlab.model.PostType;
import edu.bupt.trust.kxlab.utils.Loggen;

public class ForumDAO implements ForumDAOabstract.OnForumRawDataReceivedListener{
	
	ForumDAOdummy dummy;
	WeakReference <ForumListener> forumlistener;
	
	// Outward facing methods (used by the class requesting the data)
	public void setForumListener(ForumListener listener) { 
		this.forumlistener = new WeakReference <ForumListener> (listener); 
	}
	
	// Inward facing methods (used to communicate with the class providing the data)
	protected ForumDAO(Context c, ForumListener listener){
		dummy = new ForumDAOdummy(this);
		this.forumlistener = new WeakReference <ForumListener> (listener);
	}

	/**
	 * Get a list of post objects 
	 * The list of posts is saved to cache and loaded if a future web request fails.
	 * @param source can be DUMMY, WEB, LOCAL
	 * @param type an instance of Post type
	 */
	public void readPostList(Source source, PostType type){ 
		// determine the path to send to the server
		dummy.readPostList("forum");
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


	@Override
	public void onReadPost(RawResponse response) {
		// TODO Auto-generated method stub
		
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
		void onReadPost(Post post);
		void onReadAnnounceFAQ(Post post);
		void onSearchPostList(List <Post> posts);
	}
}
