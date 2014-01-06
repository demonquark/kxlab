package edu.bupt.trust.kxlab.data;

import edu.bupt.trust.kxlab.data.RawResponse.Page;

/**
 * <p> The ForumDAOabstract class shows all the public methods that we expect a Post data access object to provide.
 * All User data access objects must extend this class, 
 * to ensure that we get the same functionality regardless of the source. </p>
 * <p> The list of methods is loosely based on the web service interfaces provided by the trust community server. </p>
 * 
 * @author Krishna
 *
 */
abstract class ForumDAOabstract extends DAOabstract {

	// Class variables 
	protected OnForumRawDataReceivedListener listener;
	
	// Methods to be implemented by the children
	protected abstract void createPost(String email, String type, String title, String content);		// method for "/forum/createPost"
	protected abstract void createReply(String email, String type, int postId, String content); 		// method for "/forum/replyPost"
	protected abstract void createVote(String email, int voteId, int voteScore); 		// method for "/forum/replyPost"
	protected abstract void readPostList(String postType, int currentSize, Page page);		// method for "/forum/postForumDetail"
	protected abstract void readPost(String postType, int currentSize, Page page, int postId);			// method for "/forum/postList"
	protected abstract void readAnnounceList(String type, int currentSize, final Page page);	// method for "/forum/postAnnounceDetail"
	protected abstract void readAnnounceFAQ(String type, int postId);	// method for "/forum/postAnnounceDetail"
	protected abstract void searchPostList(String path);	// method for "/service/postSearchList"
	
	interface OnForumRawDataReceivedListener {
		void onCreatePost(RawResponse response);
		void onCreateReply(RawResponse response);
		void onCreateVote(RawResponse response);
		void onReadPostList(RawResponse response);
		void onReadPost(RawResponse response);
		void onReadAnnounceFAQ(RawResponse response);
		void onReadAnnounceList(RawResponse response);
		void onSearchPostList(RawResponse response);
	}


}
