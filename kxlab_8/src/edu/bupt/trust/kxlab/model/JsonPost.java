package edu.bupt.trust.kxlab.model;

import android.os.Parcel;
import android.os.Parcelable;

public class JsonPost extends JsonItem implements Parcelable {
	
	public int pdId;
	public String postType;
	public String postTitle;
	public String postDetail;
	public long pdPublishTime;
	public long pdLastEditTime;
	public int pdReplyCount;
	public long pdLastReplyTime;
	public String email;
	
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
	
	public JsonPost(JsonPost post){
		this();
		setFromJsonItem(post, false);
	}
	
	@Override public JsonItem getJsonItem() {
		JsonPost newInstance = new JsonPost();
		newInstance.setFromJsonItem(this, false);
		return newInstance;
	}

	@Override public boolean setFromJsonItem(JsonItem aThat, boolean allowNull) {
		boolean isPost = aThat instanceof JsonPost;
		
		if(isPost){
			JsonPost that = (JsonPost) aThat;
			pdId 			= that.pdId;
			postType 		= allowNull ? that.postType : replace(postType, that.postType);
			postTitle 		= allowNull ? that.postTitle : replace(postTitle, that.postTitle);
			postDetail 		= allowNull ? that.postDetail : replace(postDetail, that.postDetail);
			pdReplyCount 	= that.pdReplyCount;
			pdPublishTime 	= that.pdPublishTime;
			pdLastEditTime 	= that.pdLastEditTime;
			pdLastReplyTime = that.pdLastReplyTime;
			email 			= allowNull ? that.email : replace(email, that.email);

		}
		return isPost;
	}

	@Override public int getId() { return pdId; }

    private JsonPost(Parcel in) {
    	// Note: you need to read the items in the same order that you wrote them
		pdId 			= in.readInt();
		postType 		= in.readString();
		postTitle 		= in.readString();
		postDetail 		= in.readString();
		pdReplyCount 	= in.readInt();
		pdPublishTime 	= in.readLong();
		pdLastEditTime 	= in.readLong();
		pdLastReplyTime = in.readLong();
		email			= in.readString();
    }

	// this is used to regenerate your object.
    public static final Parcelable.Creator<JsonPost> CREATOR = new Parcelable.Creator<JsonPost>() {
        public JsonPost createFromParcel(Parcel in) { return new JsonPost(in); }
        public JsonPost[] newArray(int size) { return new JsonPost[size]; }
    };

    @Override public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	// Note: you need to write the items in the same order that you intend to read them
    	dest.writeInt(pdId);
    	dest.writeString(postType);
		dest.writeString(postTitle);
		dest.writeString(postDetail);
    	dest.writeInt(pdReplyCount);
    	dest.writeLong(pdPublishTime);
    	dest.writeLong(pdLastEditTime);
    	dest.writeLong(pdLastReplyTime);
    	dest.writeString(email);
    }	
    
	@Override public boolean equals(Object aThat) {
		if ( this == aThat ) return true;

	    //use instanceof instead of getClass here for two reasons
	    if ( !(aThat instanceof JsonPost) ) return false;

	    //cast to native object is now safe
	    JsonPost that = (JsonPost) aThat;

	    //now a proper field-by-field evaluation can be made
	    return
	    	(this.pdId == that.pdId) &&
	    	((this.postType != null) ? this.postType.equals(that.postType) : that.postType == null) &&
	    	((this.postTitle != null) ? this.postTitle.equals(that.postTitle) : that.postTitle == null) &&
	    	((this.postDetail != null) ? this.postDetail.equals(that.postDetail) : that.postDetail == null) &&
	    	(this.pdReplyCount == that.pdReplyCount) &&
	    	(this.pdPublishTime == that.pdPublishTime) &&
	    	(this.pdLastEditTime == that.pdLastEditTime) &&
	    	(this.pdLastReplyTime == that.pdLastReplyTime) &&
	    	((this.email != null) ? this.email.equals(that.email) : that.email == null);
	}
}
