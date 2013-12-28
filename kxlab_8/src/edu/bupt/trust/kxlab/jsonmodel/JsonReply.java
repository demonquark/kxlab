package edu.bupt.trust.kxlab.jsonmodel;

public class JsonReply {

	private int replyId;
	private int reReplyId;
	private int rootReplyId;
	private int rPostId;
	private String reRAuthorEmail;
	private String rAuthorEmail;
	private String rContent;
	private long rTime;
	
	public JsonReply() {
		this.replyId = 0;
		this.reReplyId = 0;
		this.rootReplyId = 0;
		this.rPostId = 0;
		this.reRAuthorEmail = "";
		this.rAuthorEmail = "";
		this.rContent = "";
		this.rTime = 0;
	}
	
	public int getReplyId() {
		return replyId;
	}
	public int getReReplyId() {
		return reReplyId;
	}
	public int getRootReplyId() {
		return rootReplyId;
	}
	public int getrPostId() {
		return rPostId;
	}
	public String getReRAuthorEmail() {
		return reRAuthorEmail;
	}
	public String getrAuthorEmail() {
		return rAuthorEmail;
	}
	public String getrContent() {
		return rContent;
	}
	public long getrTime() {
		return rTime;
	}
	public void setReplyId(int replyId) {
		this.replyId = replyId;
	}
	public void setReReplyId(int reReplyId) {
		this.reReplyId = reReplyId;
	}
	public void setRootReplyId(int rootReplyId) {
		this.rootReplyId = rootReplyId;
	}
	public void setrPostId(int rPostId) {
		this.rPostId = rPostId;
	}
	public void setReRAuthorEmail(String reRAuthorEmail) {
		if(reRAuthorEmail != null) { this.reRAuthorEmail = reRAuthorEmail; }
	}
	public void setrAuthorEmail(String rAuthorEmail) {
		if(rAuthorEmail != null) { this.rAuthorEmail = rAuthorEmail; }
	}
	public void setrContent(String rContent) {
		if(rContent != null) { this.rContent = rContent; }
	}
	public void setrTime(long rTime) {
		this.rTime = rTime;
	}
	
	public void setFromJsonReply(JsonReply otherReply) {
		this.replyId = otherReply.replyId;
		this.reReplyId = otherReply.reReplyId;
		this.rootReplyId = otherReply.rootReplyId;
		this.rPostId = otherReply.rPostId;
		this.reRAuthorEmail = otherReply.reRAuthorEmail;
		this.rAuthorEmail = otherReply.rAuthorEmail;
		this.rContent = otherReply.rContent;
		this.rTime = otherReply.rTime;
	}
	
	public void updateFromJsonReply(JsonReply other){
		
		replyId = other.replyId;
		reReplyId = other.reReplyId;
		rootReplyId = other.rootReplyId;
		rPostId = other.rPostId;
		rTime = other.rTime;
		
		reRAuthorEmail = replace(reRAuthorEmail, other.reRAuthorEmail);
		rAuthorEmail = replace(rAuthorEmail, other.rAuthorEmail);
		rContent = replace(rContent, other.rContent);
	}
	
	private String replace(String old, String replacement){
		if(replacement != null && !replacement.equals("") && !replacement.equals("null")) { old = replacement; }
		return old;
	}


}
