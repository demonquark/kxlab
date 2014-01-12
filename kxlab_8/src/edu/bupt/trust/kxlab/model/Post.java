package edu.bupt.trust.kxlab.model;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;

public class Post implements Parcelable {
	
	private JsonItem jsonPost;
	private PostType postType;
	private User postSponsor;
	private boolean poll;
	
	public Post() {
		jsonPost		= new JsonPost();
		postType 		= PostType.FORUM;
		postSponsor 	= new User();
		poll 			= false;
	}
	
	public Post(Post post){
		this();
		setFromPost(post, false);
	}

	public Post(JsonItem post){
		this();
		setJsonPost(post, null, false);
		setPostSponsor(new User(this.getEmail()));
	}
	
	public Post(JsonItem post, User user){
		this();
		setJsonPost(post, null, false);
		setPostSponsor(user);
	}
	
	public boolean isPoll() {
		return poll;
	}

	public void setFromPost(Post that, boolean allowNull) {
		postSponsor 	= allowNull ? that.postSponsor : new User(that.postSponsor);
		setJsonPost(that.jsonPost, that.postType, allowNull);
	}
	
	public void setPostSponsor(User postSponsor) {
		// TODO: tie this in with the jsonPost email 
		if(postSponsor != null) { this.postSponsor = postSponsor; }
	}

	public boolean setJsonPost(JsonItem aThat, PostType type, boolean allowNull) {
		
		boolean isPost = true;
		
		if(aThat instanceof JsonPost){
			// convert this to a JSON post
			setFromJsonPost((JsonPost) aThat, allowNull);
			
			// determine the post type
			PostType thatType = PostType.fromServerType(((JsonPost) aThat).postType);
			if (type == PostType.FORUM || type == PostType.SUGGESTION){
				postType = type;	
			} else if(thatType == PostType.FORUM || thatType == PostType.SUGGESTION) {
				postType = thatType;
			} else {
				postType = PostType.FORUM;
			}

			// assign the post type and poll 
			((JsonPost) jsonPost).postType = postType.getServerType();
			poll = false;
			
		} else if(aThat instanceof JsonAnnounceFAQ){
			// convert this to a JSON post
			setFromJsonAnnounceFAQ((JsonAnnounceFAQ) aThat, allowNull);
			// determine the post type
			PostType thatType = PostType.fromServerType(((JsonAnnounceFAQ) aThat).agType);
			if (type == PostType.ANNOUNCE || type == PostType.FAQ){
				postType = type;	
			} else if(thatType == PostType.ANNOUNCE || thatType == PostType.FAQ) {
				postType = thatType;
			} else {
				postType = PostType.ANNOUNCE;
			}

			// assign the post type and poll 
			((JsonAnnounceFAQ) jsonPost).agType = postType.getServerType();
			poll = false;
			
		} else if(aThat instanceof JsonVote){
			setFromJsonVote((JsonVote) aThat, allowNull);
			postType = PostType.ANNOUNCE;
			poll = true;
		} else {
			// Something went horribly wrong.
			poll = false;
			isPost = false;
		}
		
		return isPost;
	}
	
	private void setFromJsonPost(JsonPost that, boolean allowNull){
		jsonPost = allowNull ? that : new JsonPost(that);
	}
	
	private void setFromJsonAnnounceFAQ(JsonAnnounceFAQ that, boolean allowNull){
		jsonPost = allowNull ? that : new JsonAnnounceFAQ(that);
	}
	
	private void setFromJsonVote(JsonVote that, boolean allowNull){
		jsonPost = allowNull ? that : new JsonVote(that);
	}

	public int getId() {
		return (jsonPost != null)  ? jsonPost.getId() : -1;
	}
	
	public String getPublishTimeString(){
		long date = 0; 
		
    	if(jsonPost instanceof JsonPost){
        	date = ((JsonPost) jsonPost).pdPublishTime;
    	} else if (jsonPost instanceof JsonAnnounceFAQ) {
        	date = ((JsonAnnounceFAQ) jsonPost).agPublishTime;
    	} else if (jsonPost instanceof JsonVote){
        	date = ((JsonVote) jsonPost).endTime;
    	} 
    	
		return new SimpleDateFormat("yyyy-mm-dd HH:mm:ss", Locale.US).format(date);
	}
	
    private Post(Parcel in) {
    	// Note: you need to read the items in the same order that you wrote them
		jsonPost		= in.readParcelable(getClass().getClassLoader());
    	postType 		= PostType.fromIndex(in.readInt());
		postSponsor 	= in.readParcelable(getClass().getClassLoader());
		poll			= (in.readByte() != 0);
    }

	// this is used to regenerate your object.
    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        public Post createFromParcel(Parcel in) { return new Post(in); }
        public Post[] newArray(int size) { return new Post[size]; }
    };

    @Override public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	// Note: you need to write the items in the same order that you intend to read them
    	
    	if(jsonPost instanceof JsonPost){
        	dest.writeParcelable((JsonPost) jsonPost, 0);
    	} else if (jsonPost instanceof JsonAnnounceFAQ) {
        	dest.writeParcelable((JsonAnnounceFAQ) jsonPost, 0);
    	} else if (jsonPost instanceof JsonVote){
        	dest.writeParcelable((JsonVote) jsonPost, 0);
    	} else {
        	dest.writeParcelable(new JsonPost(), 0);
    	}
    	
    	dest.writeInt(postType.getIndex());
    	dest.writeParcelable(postSponsor, 0);
    	dest.writeByte((byte) (poll ? 1 : 0));
    }
	
	@Override public boolean equals(Object aThat) {
		if ( this == aThat ) return true;

	    //use instanceof instead of getClass here for two reasons
	    if ( !(aThat instanceof Post) ) return false;

	    //cast to native object is now safe
	    Post that = (Post) aThat;
		
	    //now a proper field-by-field evaluation can be made
	    return ((jsonPost == null && that.jsonPost == null) ||
				(jsonPost instanceof JsonPost && that.jsonPost instanceof JsonPost 
							&& ((JsonPost) jsonPost).equals(that.jsonPost)) || 
				(jsonPost instanceof JsonAnnounceFAQ && that.jsonPost instanceof JsonAnnounceFAQ 
    						&& ((JsonAnnounceFAQ) jsonPost).equals(that.jsonPost)) || 
				(jsonPost instanceof JsonVote && that.jsonPost instanceof JsonVote 
    						&& ((JsonVote) jsonPost).equals(that.jsonPost))) &&
	    	((this.postType != null) ? this.postType == that.postType : that.postType == null) &&
	    	((this.postSponsor != null) ? this.postSponsor.equals(that.postSponsor) : that.postSponsor == null);
	}

	public JsonItem getJsonPost() {
		return jsonPost;
	}

	public PostType getPostType() {
		return postType;
	}

	public User getPostSponsor() {
		return postSponsor;
	}
	
	public String getPostTitle() {
    	if(jsonPost instanceof JsonPost){
    		return ((JsonPost) jsonPost).postTitle;
    	} else if (jsonPost instanceof JsonAnnounceFAQ) {
    		return ((JsonAnnounceFAQ) jsonPost).agTitle;
    	} else if (jsonPost instanceof JsonVote){
    		return ((JsonVote) jsonPost).voteUserEmail;
    	} else {
        	return "";
    	}
	}

	public String getPostDetail() {
    	if(jsonPost instanceof JsonPost){
    		return ((JsonPost) jsonPost).postDetail;
    	} else if (jsonPost instanceof JsonAnnounceFAQ) {
    		return ((JsonAnnounceFAQ) jsonPost).agContent;
    	} else if (jsonPost instanceof JsonVote){
    		return ((JsonVote) jsonPost).voteLastRate;
    	} else {
        	return "";
    	}
	}

	public long getPdPublishTime() {
    	if(jsonPost instanceof JsonPost){
    		return ((JsonPost) jsonPost).pdPublishTime;
    	} else if (jsonPost instanceof JsonAnnounceFAQ) {
    		return ((JsonAnnounceFAQ) jsonPost).agPublishTime;
    	} else if (jsonPost instanceof JsonVote){
    		return ((JsonVote) jsonPost).endTime;
    	} else {
        	return 0;
    	}
	}

	public long getPdLastEditTime() {
    	if(jsonPost instanceof JsonPost){
    		return ((JsonPost) jsonPost).pdLastEditTime;
    	} else if (jsonPost instanceof JsonAnnounceFAQ) {
    		return ((JsonAnnounceFAQ) jsonPost).agLastEditTime;
    	} else {
        	return 0;
    	}
	}

	public int getPdReplyCount() {
    	if(jsonPost instanceof JsonPost){
    		return ((JsonPost) jsonPost).pdReplyCount;
    	} else {
        	return 0;
    	}
	}

	public long getPdLastReplyTime() {
    	if(jsonPost instanceof JsonPost){
    		return ((JsonPost) jsonPost).pdLastReplyTime;
    	} else {
        	return 0;
    	}
	}

	public String getEmail() {
    	if(jsonPost instanceof JsonPost){
    		return ((JsonPost) jsonPost).email;
    	} else if (jsonPost instanceof JsonAnnounceFAQ) {
    		return ((JsonAnnounceFAQ) jsonPost).agPublishAuthor;
    	} else if (jsonPost instanceof JsonVote){
    		return ((JsonVote) jsonPost).voteUserEmail;
    	} else {
        	return "";
    	}
	}
	
	public void setId(int id) {
    	if(jsonPost instanceof JsonPost){
    		((JsonPost) jsonPost).pdId = id;
    	} else if (jsonPost instanceof JsonAnnounceFAQ) {
    		((JsonAnnounceFAQ) jsonPost).agId = String.valueOf(id);
    	} else if (jsonPost instanceof JsonVote){
    		((JsonVote) jsonPost).voteId = String.valueOf(id);
    	} 
	}

	public void setPostTitle(String postTitle) {
		if(postTitle != null){
	    	if(jsonPost instanceof JsonPost){
	    		((JsonPost) jsonPost).postTitle = postTitle;
	    	} else if (jsonPost instanceof JsonAnnounceFAQ) {
	    		((JsonAnnounceFAQ) jsonPost).agTitle = postTitle;
	    	} 
		}
	}

	public void setPostDetail(String postDetail) {
		if(postDetail != null){
	    	if(jsonPost instanceof JsonPost){
	    		((JsonPost) jsonPost).postDetail = postDetail;
	    	} else if (jsonPost instanceof JsonAnnounceFAQ) {
	    		((JsonAnnounceFAQ) jsonPost).agContent = postDetail;
	    	} 
		}
	}

	public void setPdPublishTime(long pdPublishTime) {
    	if(jsonPost instanceof JsonPost){
    		((JsonPost) jsonPost).pdPublishTime = pdPublishTime;
    	} else if (jsonPost instanceof JsonAnnounceFAQ) {
    		((JsonAnnounceFAQ) jsonPost).agPublishTime = pdPublishTime;
    	} else if (jsonPost instanceof JsonVote){
    		((JsonVote) jsonPost).endTime = pdPublishTime;
    	} 
	}

	public void setPdLastEditTime(long pdLastEditTime) {
    	if(jsonPost instanceof JsonPost){
    		((JsonPost) jsonPost).pdLastEditTime = pdLastEditTime;
    	} else if (jsonPost instanceof JsonAnnounceFAQ) {
    		((JsonAnnounceFAQ) jsonPost).agLastEditTime = pdLastEditTime;
    	} 
	}

	public void setPdReplyCount(int pdReplyCount) {
    	if(jsonPost instanceof JsonPost){
    		((JsonPost) jsonPost).pdReplyCount = pdReplyCount;
    	} 
	}

	public void setPdLastReplyTime(long pdLastReplyTime) {
    	if(jsonPost instanceof JsonPost){
    		((JsonPost) jsonPost).pdLastReplyTime = pdLastReplyTime;
    	} 
	}

	public void setEmail(String email) {
		if(email != null){
	    	if(jsonPost instanceof JsonPost){
	    		((JsonPost) jsonPost).email = email;
	    	} else if (jsonPost instanceof JsonAnnounceFAQ) {
	    		((JsonAnnounceFAQ) jsonPost).agPublishAuthor = email;
	    	} else if (jsonPost instanceof JsonVote){
	    		((JsonVote) jsonPost).voteUserEmail = email;
	    	} 
		}
	}
}
