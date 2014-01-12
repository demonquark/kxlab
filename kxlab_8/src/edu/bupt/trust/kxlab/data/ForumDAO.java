package edu.bupt.trust.kxlab.data;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.content.Context;
import edu.bupt.trust.kxlab.data.RawResponse.Page;
import edu.bupt.trust.kxlab.data.DaoFactory.Source;
import edu.bupt.trust.kxlab.model.JsonAnnounceFAQ;
import edu.bupt.trust.kxlab.model.JsonAnnounceList;
import edu.bupt.trust.kxlab.model.JsonAnnounceOrgGuideList;
import edu.bupt.trust.kxlab.model.JsonPost;
import edu.bupt.trust.kxlab.model.JsonPostAnnounceDetail;
import edu.bupt.trust.kxlab.model.JsonPostForumDetail;
import edu.bupt.trust.kxlab.model.JsonPostList;
import edu.bupt.trust.kxlab.model.JsonReply;
import edu.bupt.trust.kxlab.model.JsonUser;
import edu.bupt.trust.kxlab.model.JsonVote;
import edu.bupt.trust.kxlab.model.JsonreReply;
import edu.bupt.trust.kxlab.model.Post;
import edu.bupt.trust.kxlab.model.PostType;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.JsonTools;
import edu.bupt.trust.kxlab.utils.Loggen;

public class ForumDAO implements ForumDAOabstract.OnForumRawDataReceivedListener{
	
	ForumDAOdummy dummy;
	ForumDAOlocal local;
	ForumDAOweb web;
	WeakReference <ForumListener> forumlistener;
	
	// Outward facing methods (used by the class requesting the data)
	public void setForumListener(ForumListener listener) { 
		this.forumlistener = new WeakReference <ForumListener> (listener); 
	}
	
	// Inward facing methods (used to communicate with the class providing the data)
	protected ForumDAO(Context c, ForumListener listener){
		dummy = new ForumDAOdummy(this);
		local = new ForumDAOlocal(this, c);
		web = new ForumDAOweb(this);
		this.forumlistener = new WeakReference <ForumListener> (listener);
	}

	/**
	 * Get a list of post objects 
	 * The list of posts is saved to cache and loaded if a future web request fails.
	 * @param source can be DUMMY, WEB, LOCAL
	 * @param type an instance of Post type
	 */
	public void readPostList(Source source, PostType type, ArrayList<Post> posts, Page page){ 
		
		// save the current page to the cache 
		if(page != Page.CURRENT || (source == Source.WEB && posts != null ) ){ 
			overwriteForumList(type, posts); 
		}
		
		// determine the post size 
		int postSize = (posts != null) ? posts.size() : 0;
		
		// let the correct source handle the request
		if(type == PostType.ANNOUNCE || type == PostType.FAQ){
			switch (source) {
			case DEFAULT:
			case WEB:
				web.readAnnounceList(type.getServerType(), postSize, page);
				break;
			case LOCAL:
				local.readAnnounceList(type.getServerType(), postSize, page);
				break;
			case DUMMY:
				dummy.readAnnounceList(type.getServerType(), postSize, page);
				break;
			}
		} else {
			switch (source) {
			case DEFAULT:
			case WEB:
				web.readPostList(type.getServerType(), postSize, page);
				break;
			case LOCAL:
				local.readPostList(type.getServerType(), postSize, page);
				break;
			case DUMMY:
				dummy.readPostList(type.getServerType(), postSize, page);
				break;
			}
		}
	}

	/**
	 * Get the details of a single post and the replies to that post 
	 * The data is saved to cache and loaded if a future web request fails.
	 * @param source can be DUMMY, WEB, LOCAL
	 * @param type an instance of Post type
	 */
	public void readPost(Source source, Post post, ArrayList<JsonReply> replies, Page page){ 

		int replySize = 0;
		
		// save the current page to the cache 
		if(page != Page.CURRENT || (source == Source.WEB && replies != null ) ){ 
			Loggen.v(this, "saved content to file.");
			replySize = overwritePostDetail(post, replies);
		}

		// let the correct source handle the request
		if(post.getPostType() == PostType.ANNOUNCE || post.getPostType() == PostType.FAQ){
			switch (source) {
			case DEFAULT:
			case WEB:
				web.readAnnounceFAQ(post.getPostType().getServerType(),post.getId());
				break;
			case LOCAL:
				local.readAnnounceFAQ(post.getPostType().getServerType(), post.getId());
				break;
			case DUMMY:
				dummy.readAnnounceFAQ(post.getPostType().getServerType(), post.getId());
				break;
			}
		} else {
			switch (source) {
			case DEFAULT:
			case WEB:
				web.readPost(post.getPostType().getServerType(), replySize, page, post.getId());
				break;
			case LOCAL:
				local.readPost(post.getPostType().getServerType(), replySize, page, post.getId());
				break;
			case DUMMY:
				dummy.readPost(post.getPostType().getServerType(), replySize, page, post.getId());
				break;
			}
		}
	}
	
	/**
	 * Creates a single post
	 * @param email Email of the user
	 * @param postType Type of the post
	 * @param title Title of the post
	 * @param content Content of the post
	 */
	public void createPost(String email, PostType type, String title, String content){ 
		web.createPost(email, type.getServerType(), title, content);
	}

	/**
	 * Creates a reply to a post
	 * @param email Email of the user
	 * @param postType Type of the post
	 * @param postId ID of the post
	 * @param content Content of the reply
	 */
	public void createReplyToPost(String email, PostType type, int postId, String content){ 
		web.createReply(email, type.getServerType(), postId, content);
	}

	/**
	 * Creates a reply to a reply
	 * @param email Email of the user
	 * @param replyId ID of the reply
	 * @param content Content of the reply
	 */
	public void createReplyToReply(String email, int replyId, String content){ 
		web.createReply(email, null, replyId, content);
	}
	
	public void createVote(String email, int voteId, int voteScore){
		web.createVote(email, voteId, voteScore);
	}

	private void overwriteForumList(PostType type, ArrayList<Post> posts){

		if(type == PostType.ANNOUNCE || type == PostType.FAQ){
			// create a JSON representation of the posts
			ArrayList <JsonAnnounceFAQ> announcements = new ArrayList<JsonAnnounceFAQ> ();
			ArrayList <JsonVote> polls = new ArrayList<JsonVote> ();
			if(posts != null){ 
				for(Post post : posts){ 
					if(post.getJsonPost() instanceof JsonAnnounceFAQ){
						announcements.add((JsonAnnounceFAQ) post.getJsonPost());	
					} else if (post.getJsonPost() instanceof JsonVote){
						polls.add((JsonVote) post.getJsonPost());
					}
				} 
			}

			// create a JSON representation of the current announcements list
			JsonAnnounceOrgGuideList guidelist = new JsonAnnounceOrgGuideList(announcements);
			JsonAnnounceList announcelist = new JsonAnnounceList(guidelist, polls);
			
			// write to file
			local.writeToFile(ForumDAOlocal.getPostListFilename(type.getServerType()), 
					new GsonBuilder().serializeNulls().create().toJson(announcelist));

		} else {
			// create a JSON representation of the posts
			ArrayList <JsonPost> jsonposts = new ArrayList<JsonPost> ();
			if(posts != null){ for(Post post : posts){ jsonposts.add((JsonPost) post.getJsonPost()); } }

			// create a JSON representation of the current post lists page.
			JsonPostList postList = new JsonPostList(jsonposts);
			
			// write to file
			local.writeToFile(ForumDAOlocal.getPostListFilename(type.getServerType()), 
					new GsonBuilder().serializeNulls().create().toJson(postList));
		}
		
		Loggen.v(this, "Done writing to file.");
	} 
	
	private int overwritePostDetail(Post post, ArrayList<JsonReply> replies){
		if(post.getJsonPost() instanceof JsonVote){
			Loggen.e(this, "Cannot write vote to detail. "); 
		} else if (post.getJsonPost() instanceof JsonAnnounceFAQ) {
			overwriteAnnouncement(post);
		} else if (post.getJsonPost() instanceof JsonPost){
			return overwritePostForumDetail(post, replies);
		}
		
		return 0;
	}
	
	private void overwriteAnnouncement(Post post) {
		// create a JSON representation of the current forum detail page.
		JsonPostAnnounceDetail oldAnnouncement = new JsonPostAnnounceDetail((JsonAnnounceFAQ) post.getJsonPost());
		
		// write to file
		local.writeToFile(ForumDAOlocal.getPostDetailFilename(post.getId(), post.getPostType().getServerType()), 
				new GsonBuilder().serializeNulls().create().toJson(oldAnnouncement));
	}

	private int overwritePostForumDetail(Post post, ArrayList<JsonReply> replies){

		// create a JSON representation of the replies
		ArrayList <JsonReply> jsonreplies = new ArrayList<JsonReply> ();
		JsonreReply reReply = new JsonreReply();
		if(replies != null){ 
			for(JsonReply reply : replies){
				if(reply.rootReplyId == 0){
					// this is a top level reply add it there
					jsonreplies.add(reply);
				} else {
					// this is a re reply add it there
					boolean added = false;
					// try adding it to an existing re reply list
					for(List<JsonReply> replylist : reReply.reReplyDetail){
						if(replylist.size() > 0 && replylist.get(0).rootReplyId == reply.rootReplyId){
							replylist.add(reply);
							added = true;
						}
					}
					
					// if there is no list yet, create a re reply list
					if(!added){
						List<JsonReply> newlist = new ArrayList <JsonReply> ();
						newlist.add(reply);
						reReply.reReplyDetail.add(newlist);
					}
				}
			} 
		}

		// create a JSON representation of the current forum detail page.
		JsonPostForumDetail oldDetails = new JsonPostForumDetail(
				(JsonPost) post.getJsonPost(), 
				post.getPostSponsor() != null ? post.getPostSponsor().getJsonUser() : new JsonUser(),
				jsonreplies,
				reReply);
		
		// write to file
		local.writeToFile(ForumDAOlocal.getPostDetailFilename(post.getId(), post.getPostType().getServerType()), 
				new GsonBuilder().serializeNulls().create().toJson(oldDetails));
		
		Loggen.v(this, "Done writing to file.");
		
		return jsonreplies.size();
	} 

	@Override public void onReadPostList(RawResponse response) {
		Loggen.v(this, "Got a response onReadPostList: " + response.message);
		
		ArrayList <Post> posts = null;
		
		if(response.errorStatus == RawResponse.Error.NONE){
			try{
				if (response.path != null && response.message != null) { }
				
				// Generic Gson instance used for conversion
				Gson gson = new Gson();
				JsonPostList postList = null;
				
				// Step 1 - convert the message into a JSON object
				if(JsonTools.isValidJSON(response.message)){
					postList = gson.fromJson(response.message,JsonPostList.class);
					
					// Step 2 - update the message with the cache content
					if(response.page != Page.CURRENT){
					
						// Step 2a - read the existing data from cache. 
						Loggen.i(this, "Start getting old post.");
						JsonPostList cacheData = gson.fromJson(local.readFromFile(response.path), JsonPostList.class);

						// Step 2b - read the existing data from cache. 
						cacheData.updateWithNew(postList, response.page == Page.PREVIOUS);
						postList = cacheData;
					}

					// Step 3 - save the values to cache
					local.writeToFile(response.path, gson.toJson(postList));

				} else {
					// Step Alternative - get the message from the cache
					postList = gson.fromJson(local.readFromFile(response.path), JsonPostList.class);
				}
				
				// Step 4 - get the posts from the JSON
				if(postList != null && postList.PostList != null) {
					// Step 4a - initialize the post array
					posts = new ArrayList<Post> ();
					
					// Step 4a - convert each element in the JSON array to post and add it to the list 
					for(JsonPost jp : postList.PostList){
						posts.add(new Post (jp));
					}
				}
			}catch(Exception e){
				// If an error occurs while parsing the message, just stop and reply with what we've got.
				Loggen.e(this, "Error (" + e.toString() + ") while parsing " + response.message);
			}
		} else {
			Loggen.e(this, "We encountered an error onReadPostList: " + response.errorStatus.toString());
		}
		
		// send the data back to the listener
		if (forumlistener.get() != null){ forumlistener.get().onReadPostList(posts); }
		
	}

	@Override public void onCreatePost(RawResponse response) {
		Loggen.v(this, "Got a response onCreatePost: |" + response.message + "| ");

		// check if everything went smoothly
		boolean success = (response.errorStatus == RawResponse.Error.NONE && 
				(response.message == null || response.message.equals("")));

		if(JsonTools.isValidJSON(response.message)){
			try{
				// Step 1 - convert the message into a JSON object
				JsonElement je = new JsonParser().parse(response.message);
				JsonObject jobj = je.getAsJsonObject();
				
				// Step 2 - convert the JSON object into a boolean
				JsonElement s = jobj.get(Urls.jsonCreatePostOrNot);
				success = (s.getAsInt() != 0);

			}catch(Exception e){
				// If an error occurs while parsing the message, just stop and reply with what we've got.
				Loggen.e(this, "Error (" + e.toString() + ") while parsing " + response.message);
			}
		}
		
		// send the data back to the listener
		if (forumlistener.get() != null){ forumlistener.get().onCreatePost(success); }
	}

	@Override public void onCreateReply(RawResponse response) {
		Loggen.v(this, "Got a response onCreateReply: |" + response.message + "| ");

		// check if everything went smoothly
		boolean success = (response.errorStatus == RawResponse.Error.NONE && 
				(response.message == null || response.message.equals("") || JsonTools.isValidJSON(response.message)));
		
		// send the data back to the listener
		if (forumlistener.get() != null){ forumlistener.get().onCreateReply(success); }
	}

	@Override
	public void onCreateVote(RawResponse response) {
		// TODO: Validate response. send the data back to the listener
		if (forumlistener.get() != null){ forumlistener.get().onCreateVote(true); }
		
	}


	@Override public void onReadPost(RawResponse response) {
		Loggen.v(this, "Got a response onReadPost: " + response.message);
		
		// initial results
		ArrayList<JsonReply> replies = null;
		Post post = null;
		Gson gson = new GsonBuilder().serializeNulls().create();
		
		// convert the provided JSON string to a JSON object
		if(response.errorStatus == RawResponse.Error.NONE){
			try{
				JsonPostForumDetail newPost = null;
				if(JsonTools.isValidJSON(response.message)){
					newPost = gson.fromJson(response.message,JsonPostForumDetail.class);
				} else {
					newPost = gson.fromJson(local.readFromFile(response.path), JsonPostForumDetail.class);
				}
				
				// update the local file with the content we have now.
				if(response.page != Page.CURRENT){
				
					// read and update the existing post from cache. 
					JsonPostForumDetail oldPost = 
							gson.fromJson(local.readFromFile(response.path), JsonPostForumDetail.class);

					oldPost.updateWithNew(newPost, response.page == Page.PREVIOUS);
					newPost = oldPost;
				}

				// save the values to file
				local.writeToFile(response.path, gson.toJson(newPost));
				
				// TODO: Figure out when to get overlap?
				
				// get the post from the data
				post = new Post(newPost.PostDetail);
				post.setPostSponsor(new User(newPost.PostSponsor));
				replies = new ArrayList<JsonReply> ();

				
				// get the replies from the data
				List<JsonReply> jsonreplies = newPost.PostReply;
				for(JsonReply reply : jsonreplies){
					replies.add(new JsonReply(reply));
					Loggen.v(this, "added " + replies.get(replies.size() -1 ).replyId);
				}
				
				// get the replies to the replies
				JsonreReply reReply = newPost.reReply;
				if(reReply != null){
					Loggen.v(this, "re reply exists");
					List <List<JsonReply>> reReplyDetail = reReply.reReplyDetail;
					if(reReplyDetail != null){
						Loggen.v(this, "re reply details exists");
						// loop through the list of re replies
						for(List<JsonReply> rereplies : reReplyDetail){
							Loggen.v(this, "going through the loop");
							// pick the first re reply
							if(rereplies.size() > 0 ){
								int rootId = rereplies.get(0).rootReplyId;
								Loggen.v(this, "rerereply detail is: " + rootId);

								// loop through the list of replies
								for(int i = 0; i < replies.size(); i++){
									// once we found the reply that this belongs to, add the replies after it.
									if(replies.get(i).replyId == rootId){
										for(int j = rereplies.size() - 1; j >= 0; j--){
											// add the replies to the list
											replies.add(i + 1, new JsonReply(rereplies.get(j)));
											Loggen.v(this, "added " + replies.get(i).replyId);											
										}
										break;
									}
								}
							}
						}
					}
				}
				
			}catch(Exception e){
				// If an error occurs while parsing the message, just stop and reply with what we've got.
				Loggen.e(this, "Error (" + e.toString() + ") while parsing " + response.message);
			}
		}
		
		// return the information to the listener
		if (forumlistener.get() != null){ forumlistener.get().onReadPost(post, replies); }
	}

	@Override public void onReadAnnounceList(RawResponse response) {
		Loggen.v(this, "Got a response onReadPostList: " + response.message);
		
		ArrayList <Post> posts = null;
		
		if(response.errorStatus == RawResponse.Error.NONE){
			try{
				if (response.path != null && response.message != null) { }
				
				// Generic Gson instance used for conversion
				Gson gson = new Gson();
				JsonAnnounceList announceList = null;
				
				// Step 1 - convert the message into a JSON object
				if(JsonTools.isValidJSON(response.message)){
					announceList = gson.fromJson(response.message,JsonAnnounceList.class);
					
					// Step 2 - update the message with the cache content
					if(response.page != Page.CURRENT){
					
						// Step 2a - read the existing data from cache. 
						Loggen.i(this, "Start getting old post.");
						JsonAnnounceList cacheData = gson.fromJson(local.readFromFile(response.path), JsonAnnounceList.class);

						// Step 2b - read the existing data from cache. 
						cacheData.updateWithNew(announceList, response.page == Page.PREVIOUS);
						announceList = cacheData;
					}

					// Step 3 - save the values to cache
					local.writeToFile(response.path, gson.toJson(announceList));

				} else {
					// Step Alternative - get the message from the cache
					announceList = gson.fromJson(local.readFromFile(response.path), JsonAnnounceList.class);
				}
				
				// Step 4 - get the announcements from the JSON
				if(announceList != null && announceList.announceorguideList != null
						&& announceList.announceorguideList.content != null) {
					// Step 4a - initialize the post array
					posts = new ArrayList<Post> ();
					
					// Step 4a - convert each element in the JSON array to post and add it to the list 
					for(JsonAnnounceFAQ jp : announceList.announceorguideList.content){
						posts.add(new Post (jp));
					}
				}
				
				// Step 5 - get the votes from the JSON
				if(announceList != null && announceList.vote != null) {
					// Step 5a - initialize the post array
					if(posts == null) { posts = new ArrayList<Post> (); }
					
					// Step 4a - convert each element in the JSON array to post and add it to the list 
					for(JsonVote jv : announceList.vote){
						posts.add(new Post (jv));
					}
				}
				
				
			}catch(Exception e){
				// If an error occurs while parsing the message, just stop and reply with what we've got.
				Loggen.e(this, "Error (" + e.toString() + ") while parsing " + response.message);
			}
		} else {
			Loggen.e(this, "We encountered an error onReadPostList: " + response.errorStatus.toString());
		}
		
		// send the data back to the listener
		if (forumlistener.get() != null){ forumlistener.get().onReadPostList(posts); }
	}
	
	@Override public void onReadAnnounceFAQ(RawResponse response) {
		Loggen.v(this, "Got a response onReadAnnounceFAQ:" + response.message);

		Post post = null;
		
		// check if everything went smoothly
		if(response.errorStatus == RawResponse.Error.NONE && JsonTools.isValidJSON(response.message)){
			try{
				// Step 1 - convert the message into a JSON object
				JsonPostAnnounceDetail announcement = 
						new Gson().fromJson(response.message, JsonPostAnnounceDetail.class);

				// Step 2 - convert the announcement into a post
				post = new Post(announcement.PostAnnounceDetail != null ? 
						announcement.PostAnnounceDetail : new JsonAnnounceFAQ());
				
				// write to file
				local.writeToFile(ForumDAOlocal.getPostDetailFilename(post.getId(), post.getPostType().getServerType()), 
						new GsonBuilder().serializeNulls().create().toJson(announcement));

			}catch(Exception e){
				// If an error occurs while parsing the message, just stop and reply with what we've got.
				Loggen.e(this, "Error (" + e.toString() + ") while parsing " + response.message);
			}
		} else {
			Loggen.e(this, "We encountered an error onReadPostList: " + response.errorStatus.toString());
		}
		
		// send the data back to the listener
		if (forumlistener.get() != null){ forumlistener.get().onReadAnnounceFAQ(post); }
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
		void onReadPost(Post post, List <JsonReply> replies);
		void onReadAnnounceFAQ(Post post);
		void onSearchPostList(List <Post> posts);
	}

}
