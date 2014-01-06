package edu.bupt.trust.kxlab.model;

import java.text.SimpleDateFormat;
import java.util.Locale;

import edu.bupt.trust.kxlab.jsonmodel.JsonAnnounceFAQ;
import edu.bupt.trust.kxlab.jsonmodel.JsonPost;
import edu.bupt.trust.kxlab.jsonmodel.JsonVote;
import android.os.Parcel;
import android.os.Parcelable;

public class Post implements Parcelable {
	
	private int pdId;
	private PostType postType;
	private String postTitle;
	private String postDetail;
	private int pdReplyCount;
	private long pdPublishTime;
	private long pdLastEditTime;
	private long pdLastReplyTime;
	private User postSponsor;
	private boolean poll;
	
	public Post() {
		pdId 			= -1;
		postType 		= PostType.FORUM;
		postTitle 		= "";
		postDetail 		= "";
		pdReplyCount 	= 0;
		pdPublishTime 	= 0;
		pdLastEditTime 	= 0;
		pdLastReplyTime = 0;
		postSponsor 	= new User();
		poll 			= false;
	}
	
	public Post(JsonPost post){
		pdId 			= post.getPdId();
		postTitle 		= post.getPostTitle();
		postDetail 		= post.getPostDetail();
		pdReplyCount 	= post.getPdReplyCount();
		pdPublishTime 	= post.getPdPublishTime();
		pdLastEditTime 	= post.getPdLastEditTime();
		pdLastReplyTime = post.getPdLastReplyTime();
		postSponsor 	= new User(post.getEmail());
		postType 		= PostType.fromServerType(post.getPostType());
		poll			= false;
	}

	public Post(Post otherPost){
		setFromOtherPost(otherPost);
	}

    private Post(Parcel in) {
    	// Note: you need to read the items in the same order that you wrote them
		pdId 			= in.readInt();
		postType 		= PostType.fromIndex(in.readInt());
		postTitle 		= in.readString();
		postDetail 		= in.readString();
		pdReplyCount 	= in.readInt();
		pdPublishTime 	= in.readLong();
		pdLastEditTime 	= in.readLong();
		pdLastReplyTime = in.readLong();
		postSponsor 	= in.readParcelable(getClass().getClassLoader());
		poll			= (in.readByte() != 0);
    }

    public Post(JsonAnnounceFAQ jp) {
		pdId 			= Integer.valueOf(jp.getAgId());
		postType 		= PostType.fromServerType(jp.getAgType());
		postTitle 		= jp.getAgTitle();
		postDetail 		= jp.getAgContent();
		pdReplyCount 	= 0;
		pdPublishTime 	= jp.getAgPublishTime();
		pdLastEditTime 	= jp.getAgLastEditTime();
		pdLastReplyTime = 0;
		postSponsor 	= new User(jp.getAgPublishAuthor());
		poll			= false;
	}

	public Post(JsonVote jv) {
		pdId 			= Integer.valueOf(jv.getVoteId());
		postType 		= PostType.ANNOUNCE;
		postTitle 		= jv.getVoteUserEmail();
		postDetail 		= jv.getVoteLastRate();
		pdReplyCount 	= 0;
		pdPublishTime 	= 0;
		pdLastEditTime 	= 0;
		pdLastReplyTime = 0;
		postSponsor 	= new User(jv.getVoteUserEmail());
		poll			= true;
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
    	dest.writeInt(pdId);
    	dest.writeInt(postType.getIndex());
		dest.writeString(postTitle);
		dest.writeString(postDetail);
    	dest.writeInt(pdReplyCount);
    	dest.writeLong(pdPublishTime);
    	dest.writeLong(pdLastEditTime);
    	dest.writeLong(pdLastReplyTime);
    	dest.writeParcelable(postSponsor, 0);
    	dest.writeByte((byte) (poll ? 1 : 0));
    }
	
	public boolean isPoll() {
		return poll;
	}

	public void setPoll(boolean poll) {
		this.poll = poll;
	}

	public int getPdId() {
		return pdId;
	}

	public void setPdId(int pdId) {
		this.pdId = pdId;
	}

	public PostType getPostType() {
		return postType;
	}

	public void setPostType(PostType postType) {
		if(postType != null) { this.postType = postType; }
	}

	public String getPostTitle() {
		return postTitle;
	}

	public void setPostTitle(String postTitle) {
		if(postTitle != null) { this.postTitle = postTitle; }
	}

	public String getPostDetail() {
		return postDetail;
	}

	public void setPostDetail(String postDetail) {
		if(postDetail != null) { this.postDetail = postDetail; }
	}

	public int getPdReplyCount() {
		return pdReplyCount;
	}

	public void setPdReplyCount(int pdReplyCount) {
		this.pdReplyCount = pdReplyCount;
	}

	public long getPdPublishTime() {
		return pdPublishTime;
	}

	public void setPdPublishTime(long pdPublishTime) {
		this.pdPublishTime = pdPublishTime;
	}

	public long getPdLastEditTime() {
		return pdLastEditTime;
	}

	public void setPdLastEditTime(long pdLastEditTime) {
		this.pdLastEditTime = pdLastEditTime;
	}

	public long getPdLastReplyTime() {
		return pdLastReplyTime;
	}

	public void setPdLastReplyTime(long pdLastReplyTime) {
		this.pdLastReplyTime = pdLastReplyTime;
	}

	public User getPostSponsor() {
		return postSponsor;
	}

	public void setPostSponsor(User postSponsor) {
		if(postSponsor != null) { this.postSponsor = postSponsor; }
	}
	
	public String getShortTitle(int max){
		return cutString(postTitle, max);
	}

	public String getShortDetail(int max){
		return cutString(postDetail, max);
	}
	
	private String cutString(String longString, int max){
		String shortString = "";
		int len = longString.length();
		if(max >= len || max < 0){
			shortString = longString; 
		} else if(max < 5){
			shortString = longString.substring(0, max);
		} else {
			shortString = longString.substring(0, max -3) + "...";
		}
		
		return shortString;
	}
	
	public void setFromOtherPost(Post otherPost){
		pdId 			= otherPost.getPdId();
		postType 		= otherPost.getPostType();
		postTitle 		= otherPost.getPostTitle();
		postDetail 		= otherPost.getPostDetail();
		pdReplyCount 	= otherPost.getPdReplyCount();
		pdPublishTime 	= otherPost.getPdPublishTime();
		pdLastEditTime 	= otherPost.getPdLastEditTime();
		pdLastReplyTime = otherPost.getPdLastReplyTime();
		postSponsor 	= otherPost.getPostSponsor();
		poll			= otherPost.isPoll();
	}
	
	public JsonPost getJsonPost(){

		JsonPost post = new JsonPost();

		post.setPdId(pdId);
		post.setPostType(getPostType().getServerType());
		post.setPostTitle(this.getPostTitle());
		post.setPostDetail(this.getPostDetail());
		post.setPdLastEditTime(this.getPdLastEditTime());
		post.setPdLastReplyTime(this.getPdLastReplyTime());
		post.setPdReplyCount(this.getPdReplyCount());
		post.setPdPublishTime(this.getPdPublishTime());
		post.setEmail(this.postSponsor.getEmail());
		
		return post;
	}
	
	public String getPublishTimeString(){
		return new SimpleDateFormat("yyyy-mm-dd HH:mm:ss", Locale.US).format(pdPublishTime);
	}
	

	
	@Override 
	public boolean equals(Object aThat) {
		if ( this == aThat ) return true;

	    //use instanceof instead of getClass here for two reasons
	    if ( !(aThat instanceof Post) ) return false;

	    //cast to native object is now safe
	    Post that = (Post) aThat;

	    //now a proper field-by-field evaluation can be made
	    return
	    	(this.pdId == that.pdId) &&
	    	(this.pdReplyCount == that.pdReplyCount) &&
	    	(this.pdPublishTime == that.pdPublishTime) &&
	    	(this.pdLastEditTime == that.pdLastEditTime) &&
	    	(this.pdLastReplyTime == that.pdLastReplyTime) &&
	    	((this.postType != null) ? this.postType == that.postType : that.postType == null) &&
	    	((this.postTitle != null) ? this.postTitle.equals(that.postTitle) : that.postTitle == null) &&
	    	((this.postDetail != null) ? this.postDetail.equals(that.postDetail) : that.postDetail == null) &&
	    	((this.postSponsor != null) ? this.postSponsor.equals(that.postSponsor) : that.postSponsor == null);
	}
}
