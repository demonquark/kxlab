package edu.bupt.trust.kxlab.model;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;

public class JsonReply extends JsonItem implements Parcelable{

	public int replyId;
	public int reReplyId;
	public int rootReplyId;
	public int rPostId;
	public String reRAuthorEmail;
	public String rAuthorEmail;
	public String rContent;
	public long rTime;
	
	public JsonReply() {
		replyId = -1;
		reReplyId = -1;
		rootReplyId = -1;
		rPostId = -1;
		reRAuthorEmail = "";
		rAuthorEmail = "";
		rContent = "";
		rTime = 0;
	}
	
	public JsonReply(JsonReply reply) {
		this();
		setFromJsonItem(reply, false);
	}

	@Override public JsonItem getJsonItem() {
		JsonReply newInstance = new JsonReply();
		newInstance.setFromJsonItem(this, false);
		return newInstance;
	}

	@Override public boolean setFromJsonItem(JsonItem aThat, boolean allowNull) {
		boolean isReply = (aThat instanceof JsonReply);
		
		if(isReply){
			JsonReply that 	= (JsonReply) aThat;
			replyId 		= that.replyId;
			reReplyId 		= that.reReplyId;
			rootReplyId 	= that.rootReplyId;
			rPostId 		= that.rPostId;
			reRAuthorEmail 	= allowNull ? that.reRAuthorEmail : replace(reRAuthorEmail, that.reRAuthorEmail);
			rAuthorEmail 	= allowNull ? that.rAuthorEmail : replace(rAuthorEmail, that.rAuthorEmail);
			rContent 		= allowNull ? that.rContent : replace(rContent, that.rContent);
			rTime 			= that.rTime;
		}
		
		return isReply;
	}

	@Override public int getId() {
		return replyId;
	}

	public String getrTimeString(){
		return new SimpleDateFormat("yyyy-mm-dd HH:mm:ss", Locale.US).format(rTime);
	}
	
    private JsonReply(Parcel in) {
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
    public static final Parcelable.Creator<JsonReply> CREATOR = new Parcelable.Creator<JsonReply>() {
        public JsonReply createFromParcel(Parcel in) { return new JsonReply(in); }
        public JsonReply[] newArray(int size) { return new JsonReply[size]; }
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
}
