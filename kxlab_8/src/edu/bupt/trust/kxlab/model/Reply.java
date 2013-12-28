package edu.bupt.trust.kxlab.model;

import java.text.SimpleDateFormat;
import java.util.Locale;

import edu.bupt.trust.kxlab.jsonmodel.JsonPost;
import edu.bupt.trust.kxlab.jsonmodel.JsonReply;
import android.os.Parcel;
import android.os.Parcelable;

public class Reply implements Parcelable {

	private int replyId;
	private int reReplyId;
	private int rootReplyId;
	private int rPostId;
	private String reRAuthorEmail;
	private String rAuthorEmail;
	private String rContent;
	private long rTime;
	
	public Reply() {
		this.replyId = 0;
		this.reReplyId = 0;
		this.rootReplyId = 0;
		this.rPostId = 0;
		this.reRAuthorEmail = "";
		this.rAuthorEmail = "";
		this.rContent = "";
		this.rTime = 0;
	}

	public Reply(Reply otherReply){
		setFromReply(otherReply);
	}
	
	public Reply(JsonReply otherReply) {
		this.replyId = otherReply.getReplyId();
		this.reReplyId = otherReply.getReReplyId();
		this.rootReplyId = otherReply.getRootReplyId();
		this.rPostId = otherReply.getrPostId();
		this.reRAuthorEmail = otherReply.getReRAuthorEmail();
		this.rAuthorEmail = otherReply.getrAuthorEmail();
		this.rContent = otherReply.getrContent();
		this.rTime = otherReply.getrTime();
	}


    private Reply(Parcel in) {
    	// Note: you need to read the items in the same order that you wrote them
		this.replyId = in.readInt();
		this.reReplyId = in.readInt();
		this.rootReplyId = in.readInt();
		this.rPostId = in.readInt();
		this.reRAuthorEmail = in.readString();
		this.rAuthorEmail = in.readString();
		this.rContent = in.readString();
		this.rTime = in.readLong();
    }

    // this is used to regenerate your object.
    public static final Parcelable.Creator<Reply> CREATOR = new Parcelable.Creator<Reply>() {
        public Reply createFromParcel(Parcel in) { return new Reply(in); }
        public Reply[] newArray(int size) { return new Reply[size]; }
    };

	@Override public int describeContents() { return 0; }
	@Override public void writeToParcel(Parcel dest, int flags) {
    	dest.writeInt(replyId);
    	dest.writeInt(reReplyId);
    	dest.writeInt(rootReplyId);
    	dest.writeInt(rPostId);
		dest.writeString(reRAuthorEmail);
		dest.writeString(rAuthorEmail);
		dest.writeString(rContent);
		dest.writeLong(rTime);
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

	public void setFromReply(Reply otherReply) {
		this.replyId = otherReply.replyId;
		this.reReplyId = otherReply.reReplyId;
		this.rootReplyId = otherReply.rootReplyId;
		this.rPostId = otherReply.rPostId;
		this.reRAuthorEmail = otherReply.reRAuthorEmail;
		this.rAuthorEmail = otherReply.rAuthorEmail;
		this.rContent = otherReply.rContent;
		this.rTime = otherReply.rTime;
	}
	
	public JsonReply getJsonReply(){

		JsonReply reply = new JsonReply();

		reply.setReplyId(this.replyId);
		reply.setReReplyId(this.reReplyId);
		reply.setRootReplyId(this.rootReplyId);
		reply.setrPostId(this.rPostId);
		reply.setrAuthorEmail(rAuthorEmail);
		reply.setReRAuthorEmail(reRAuthorEmail);
		reply.setrContent(rContent);
		reply.setrTime(rTime);
		
		return reply;
	}

	
	public String getrTimeString(){
		return new SimpleDateFormat("yyyy-mm-dd HH:mm:ss", Locale.US).format(rTime);
	}

}
