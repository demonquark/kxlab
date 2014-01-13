package edu.bupt.trust.kxlab.model;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;

public class JsonComment extends JsonItem implements Parcelable  {

	public int commentid;
	public int serviceid;
	public String useremail;
	public String reuseremail;
	public String commentscore;
	public String commentdetail;
	public long commenttime;
	public int recommentid;
	public int rootcommentid;
	
    public JsonComment() {
    	commentid 		= -1;
    	serviceid 		= -1;
    	useremail 		= "";
    	reuseremail 	= "";
    	commentscore 	= "";
    	commentdetail 	= "";
    	commenttime 	= 0;
    	recommentid 	= -1;
    	rootcommentid 	= -1;
    }
    
    public JsonComment(JsonComment item) {
		this();
		setFromJsonItem(item, false);
	}

	@Override public JsonItem getJsonItem() {
		JsonComment newInstance = new JsonComment();
		newInstance.setFromJsonItem(this, false);
		return newInstance;
	}

	@Override public boolean setFromJsonItem(JsonItem aThat, boolean allowNull) {
		boolean isRecord = (aThat instanceof JsonComment);
		
		if(isRecord){
			JsonComment that = (JsonComment) aThat;
	    	commentid 		= that.commentid;
	    	serviceid 		= that.serviceid;
	    	useremail 		= allowNull ? that.useremail : replace(useremail, that.useremail);
	    	reuseremail 	= allowNull ? that.reuseremail : replace(reuseremail, that.reuseremail);
	    	commentscore 	= allowNull ? that.commentscore : replace(commentscore, that.commentscore);
	    	commentdetail 	= allowNull ? that.commentdetail : replace(commentdetail, that.commentdetail);
	    	commenttime 	= that.commenttime;
	    	recommentid 	= that.recommentid;
	    	rootcommentid 	= that.rootcommentid;
		}
		
		return isRecord;
	}

	@Override public int getId() {
		return commentid;
	}

	public String getCommenttimeString(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(commenttime);
	}
	
	private JsonComment(Parcel in) {
    	// Note: you need to read the items in the same order that you wrote them
		commentid = in.readInt();
		serviceid = in.readInt();
		useremail = in.readString();
		reuseremail = in.readString();
		commentscore = in.readString();
		commentdetail = in.readString();
		commenttime = in.readLong();
		recommentid = in.readInt();
		rootcommentid = in.readInt();
    }
    
	// this is used to regenerate your object.
    public static final Parcelable.Creator<JsonComment> CREATOR = new Parcelable.Creator<JsonComment>() {
        public JsonComment createFromParcel(Parcel in) { return new JsonComment(in); }
        public JsonComment[] newArray(int size) { return new JsonComment[size]; }
    };

    @Override public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	// Note: you need to write the items in the same order that you intend to read them
    	dest.writeInt(commentid);
    	dest.writeInt(serviceid);
    	dest.writeString(useremail);
    	dest.writeString(reuseremail);
    	dest.writeString(commentscore);
    	dest.writeString(commentdetail);
    	dest.writeLong(commenttime);
    	dest.writeInt(recommentid);
    	dest.writeInt(rootcommentid);    	
    }
    
}
