package edu.bupt.trust.kxlab.jsonmodel;

public class JsonPost {
	
	private int pdId;
	private String postType;
	private String postTitle;
	private String postDetail;
	private long pdPublishTime;
	private long pdLastEditTime;
	private int pdReplyCount;
	private long pdLastReplyTime;
	private String email;
	
	public JsonPost() {
		pdId 			= -1;
		postType 		= "";
		postTitle 		= "";
		postDetail 		= "";
		pdReplyCount 	= 0;
		pdPublishTime 	= 0;
		pdLastEditTime 	= 0;
		pdLastReplyTime = 0;
		email 			= "";
	}
	
	
	
	/* Getter and Setters */
	public void setPdId(int pdId){
		this.pdId = pdId;
	}
	public void setPostType(String postType){
		if(postType != null){ this.postType = postType; }
	}
	public void setPostTitle(String postTitle){ 
		if(postTitle != null){ this.postTitle = postTitle; }
	}
	public void setPostDetail(String postDetail){ 
		if(postDetail != null){ this.postDetail = postDetail; }
	}
	public void setPdPublishTime(long pdPublishTime){ 
		this.pdPublishTime = pdPublishTime;
	}
	public void setPdLastEditTime(long pdLastEditTime){ 
		this.pdLastEditTime = pdLastEditTime;
	}
	public void setPdReplyCount(int pdReplyCount){
		this.pdReplyCount = pdReplyCount;
	}
	public void setPdLastReplyTime(long pdLastReplyTime){ 
		this.pdLastReplyTime = pdLastReplyTime;
	}
	public void setEmail(String email){
		if(email != null){ this.email = email; }
	}
	
	public int getPdId(){ 				return this.pdId; }
	public String getPostType(){ 		return this.postType; }
	public String getPostTitle(){ 		return this.postTitle; }
	public String getPostDetail(){ 		return this.postDetail; }
	public long getPdLastEditTime(){	return this.pdPublishTime; }
	public int getPdReplyCount(){ 		return this.pdReplyCount; }
	public long getPdLastReplyTime(){	return this.pdLastReplyTime; }
	public String getEmail(){ 			return this.email; }
	public long getPdPublishTime() {	return pdPublishTime; }
	
	
	public void setFromJsonPost(JsonPost otherPost){
		pdId 			= otherPost.pdId;
		postType 		= otherPost.postType;
		postTitle 		= otherPost.postTitle;
		postDetail 		= otherPost.postDetail;
		pdReplyCount 	= otherPost.pdReplyCount;
		pdPublishTime 	= otherPost.pdPublishTime;
		pdLastEditTime 	= otherPost.pdLastEditTime;
		pdLastReplyTime = otherPost.pdLastReplyTime;
		email 			= otherPost.email;
	}
}
