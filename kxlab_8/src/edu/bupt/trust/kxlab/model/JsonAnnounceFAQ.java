package edu.bupt.trust.kxlab.model;

import android.os.Parcel;
import android.os.Parcelable;
import edu.bupt.trust.kxlab.utils.Loggen;

public class JsonAnnounceFAQ extends JsonItem implements Parcelable {
	
	public String agId;
	public String agType;
	public String agTitle;
	public String agContent;
	public String agPublishAuthor;
	public long agPublishTime;
	public long agLastEditTime;
	
	public JsonAnnounceFAQ(){
		agId = "-1";
		agType = "";
		agTitle = "";
		agContent = "";
		agPublishAuthor = "";
		agPublishTime = 0;
		agLastEditTime = 0;
	}
	
	public JsonAnnounceFAQ(JsonAnnounceFAQ post){
		this();
		setFromJsonItem(post, false);
	}
	
	@Override public JsonItem getJsonItem() {
		JsonAnnounceFAQ newInstance = new JsonAnnounceFAQ();
		newInstance.setFromJsonItem(this, false);
		return newInstance;
	}
	@Override public boolean setFromJsonItem(JsonItem aThat, boolean allowNull) {
		boolean isAnnounce = (aThat instanceof JsonAnnounceFAQ);
		
		if(isAnnounce){
			JsonAnnounceFAQ that = (JsonAnnounceFAQ) aThat;
			agId 			= allowNull ? that.agId : replace(agId, that.agId);
			agType 			= allowNull ? that.agType : replace(agType, that.agType);
			agTitle 		= allowNull ? that.agTitle : replace(agTitle, that.agTitle);
			agContent 		= allowNull ? that.agContent : replace(agContent, that.agContent);
			agPublishAuthor = allowNull ? that.agPublishAuthor : replace(agPublishAuthor, that.agPublishAuthor);
			agPublishTime 	= that.agPublishTime; 
			agLastEditTime 	= that.agLastEditTime;
		}
		
		return isAnnounce;
	}
	
	@Override public int getId() { 
		int id = -1;
		try{
			id = Integer.parseInt(agId);
		}catch(NumberFormatException e){
			Loggen.e(this, "NumberFormatException while getting id");
		}
		return id; 
	}
	
    private JsonAnnounceFAQ(Parcel in) {
    	// Note: you need to read the items in the same order that you wrote them
    	agId 			= in.readString();
    	agType 			= in.readString();
    	agTitle 		= in.readString();
    	agContent 		= in.readString();
    	agPublishAuthor	= in.readString();
    	agPublishTime 	= in.readLong();
    	agLastEditTime	= in.readLong();
    }

	// this is used to regenerate your object.
    public static final Parcelable.Creator<JsonAnnounceFAQ> CREATOR = new Parcelable.Creator<JsonAnnounceFAQ>() {
        public JsonAnnounceFAQ createFromParcel(Parcel in) { return new JsonAnnounceFAQ(in); }
        public JsonAnnounceFAQ[] newArray(int size) { return new JsonAnnounceFAQ[size]; }
    };

    @Override public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	// Note: you need to write the items in the same order that you intend to read them
    	dest.writeString(agId);
		dest.writeString(agType);
		dest.writeString(agTitle);
		dest.writeString(agContent);
		dest.writeString(agPublishAuthor);
    	dest.writeLong(agPublishTime);
    	dest.writeLong(agLastEditTime);
    }	
	
	@Override public boolean equals(Object aThat) {
		if ( this == aThat ) return true;

	    //use instanceof instead of getClass here for two reasons
	    if ( !(aThat instanceof JsonAnnounceFAQ) ) return false;

	    //cast to native object is now safe
	    JsonAnnounceFAQ that = (JsonAnnounceFAQ) aThat;

	    //now a proper field-by-field evaluation can be made
	    return
	    	((this.agId != null) ? this.agId.equals(that.agId) : that.agId == null) &&
	    	((this.agType != null) ? this.agType.equals(that.agType) : that.agType == null) &&
	    	((this.agTitle != null) ? this.agTitle.equals(that.agTitle) : that.agTitle == null) &&
	    	((this.agContent != null) ? this.agContent.equals(that.agContent) : that.agContent == null) &&
	    	((this.agPublishAuthor != null) ? this.agPublishAuthor.equals(that.agPublishAuthor) : that.agPublishAuthor == null) &&
	    	(this.agPublishTime == that.agPublishTime) &&
	    	(this.agLastEditTime == that.agLastEditTime) ;
	}

}
