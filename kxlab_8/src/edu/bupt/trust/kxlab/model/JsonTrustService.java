package edu.bupt.trust.kxlab.model;

import edu.bupt.trust.kxlab.model.JsonItem;
import android.os.Parcel;
import android.os.Parcelable;

public class JsonTrustService extends JsonItem implements Parcelable  {

	public int serviceid;
	public int servicetype;
	public String servicetitle;
	public String servicedetail;
	public long servicecreatetime;
	public long servicelastedittime;
	public String credibilityScore;
	public String servicephoto;
	public String useremail;
	public int servicestatus;
	
	public String localPhoto;
	
    public JsonTrustService() {
		serviceid 			= -1;
		servicetype 		= -1;
		servicetitle 		= "";
		servicedetail 		= "";
		servicecreatetime 	= 0;
		servicelastedittime = 0;
		credibilityScore 	= "";
		servicephoto 		= "";
		useremail 			= "";
		servicestatus 		= -1;
		localPhoto			= "";
    }
	
    public JsonTrustService(JsonTrustService otherService) {
    	this();
    	setFromJsonItem(otherService, false);
    }
	
	@Override public JsonItem getJsonItem() {
		JsonTrustService newInstance = new JsonTrustService();
		newInstance.setFromJsonItem(this, false);
		return newInstance;
	}

	@Override public boolean setFromJsonItem(JsonItem aThat, boolean allowNull) {
		boolean isService = (aThat instanceof JsonTrustService);
		
		if(isService){
			JsonTrustService that = (JsonTrustService) aThat;
			serviceid 			= that.serviceid;
			servicetype 		= that.servicetype;
			servicetitle 		= allowNull ? that.servicetitle : replace(servicetitle, that.servicetitle);
			servicedetail 		= allowNull ? that.servicedetail : replace(servicedetail, that.servicedetail);
			servicecreatetime 	= that.servicecreatetime;
			servicelastedittime = that.servicelastedittime;
			credibilityScore 	= allowNull ? that.credibilityScore : replace(credibilityScore, that.credibilityScore);
			servicephoto 		= allowNull ? that.servicephoto : replace(servicephoto, that.servicephoto);
			useremail 			= allowNull ? that.useremail : replace(useremail, that.useremail);
			servicestatus 		= that.servicestatus;
			localPhoto 			= allowNull ? that.localPhoto : replace(localPhoto, that.localPhoto);
		}
		
		return isService;
	}

	@Override public int getId() {
		return serviceid;
	}

	private JsonTrustService(Parcel in) {
    	// Note: you need to read the items in the same order that you wrote them
    	serviceid = in.readInt();
    	servicetype = in.readInt();
		servicetitle = in.readString();
		servicedetail = in.readString();
		servicecreatetime = in.readLong();
		servicelastedittime = in.readLong();
		credibilityScore = in.readString();
		servicephoto = in.readString();
		useremail = in.readString();
		servicestatus = in.readInt();
		localPhoto = in.readString();
    }

    // this is used to regenerate your object.
    public static final Parcelable.Creator<JsonTrustService> CREATOR = new Parcelable.Creator<JsonTrustService>() {
        public JsonTrustService createFromParcel(Parcel in) { return new JsonTrustService(in); }
        public JsonTrustService[] newArray(int size) { return new JsonTrustService[size]; }
    };

	@Override public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	// Note: you need to write the items in the same order that you intend to read them
    	dest.writeInt(serviceid);
    	dest.writeInt(servicetype);
    	dest.writeString(servicetitle);
    	dest.writeString(servicedetail);
    	dest.writeLong(servicecreatetime);
    	dest.writeLong(servicelastedittime);
    	dest.writeString(credibilityScore);
    	dest.writeString(servicephoto);
    	dest.writeString(useremail);
    	dest.writeInt(servicestatus);
    	dest.writeString(localPhoto);    	
    }

	public static Parcelable.Creator<JsonTrustService> getCreator() {
		return CREATOR;
	}
	
	@Override public boolean equals(Object aThat) {
		if ( this == aThat ) return true;

	    //use instanceof instead of getClass here for two reasons
	    if ( !(aThat instanceof JsonTrustService) ) return false;

	    //cast to native object is now safe
	    JsonTrustService that = (JsonTrustService)aThat;

	    //now a proper field-by-field evaluation can be made
	    return
	    	(this.serviceid == that.serviceid) &&
	    	(this.servicetype == that.servicetype) &&
	    	(this.servicecreatetime == that.servicecreatetime) &&
	    	(this.servicelastedittime == that.servicelastedittime) &&
	    	((this.servicetitle != null) ? this.servicetitle.equals(that.servicetitle) : that.servicetitle == null) &&
	    	((this.servicedetail != null) ? this.servicedetail.equals(that.servicedetail) : that.servicedetail == null) &&
	    	((this.credibilityScore != null) ? this.credibilityScore.equals(that.credibilityScore) : that.credibilityScore == null) &&
	    	((this.servicephoto != null) ? this.servicephoto.equals(that.servicephoto) : that.servicephoto == null) &&
	    	((this.useremail != null) ? this.useremail.equals(that.useremail) : that.useremail == null);
	}
}
