package edu.bupt.trust.kxlab.model;

import android.os.Parcel;
import android.os.Parcelable;
import edu.bupt.trust.kxlab.utils.Loggen;

public class JsonVote extends JsonItem implements Parcelable {
	
	public String voteId;
	public String voteUserEmail;
	public String voteLastRate;	
	public long endTime;
	
	public JsonVote(){
		voteId = "-1";
		voteUserEmail = "";
		voteLastRate = "0";
		endTime = 0;
	}
	
	public JsonVote(JsonVote vote){
		this();
		setFromJsonItem(vote, false);
	}
	
	@Override public JsonItem getJsonItem() {
		JsonVote newInstance = new JsonVote();
		newInstance.setFromJsonItem(this, false);
		return newInstance;
	}

	@Override public boolean setFromJsonItem(JsonItem aThat, boolean allowNull) {
		boolean isVote = (aThat instanceof JsonVote);
		
		if(isVote){
			JsonVote that = (JsonVote) aThat;
			voteId 			= allowNull ? that.voteId : replace(voteId, that.voteId);
			voteUserEmail 	= allowNull ? that.voteUserEmail : replace(voteUserEmail, that.voteUserEmail);
			voteLastRate 	= allowNull ? that.voteLastRate : replace(voteLastRate, that.voteLastRate);
			endTime 		= that.endTime;
		}
		
		return isVote;
	}

	@Override public int getId() {
		int id = -1;
		try{
			id = Integer.parseInt(voteId);
		}catch(NumberFormatException e){
			Loggen.e(this, "NumberFormatException while getting id");
		}
		return id; 
	}
	
    private JsonVote(Parcel in) {
    	// Note: you need to read the items in the same order that you wrote them
    	voteId 			= in.readString();
    	voteUserEmail 	= in.readString();
    	voteLastRate 	= in.readString();
    	endTime 		= in.readLong();
    }

	// this is used to regenerate your object.
    public static final Parcelable.Creator<JsonVote> CREATOR = new Parcelable.Creator<JsonVote>() {
        public JsonVote createFromParcel(Parcel in) { return new JsonVote(in); }
        public JsonVote[] newArray(int size) { return new JsonVote[size]; }
    };

    @Override public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	// Note: you need to write the items in the same order that you intend to read them
    	dest.writeString(voteId);
		dest.writeString(voteUserEmail);
		dest.writeString(voteLastRate);
    	dest.writeLong(endTime);
    }	
	
	@Override public boolean equals(Object aThat) {
		if ( this == aThat ) return true;

	    //use instanceof instead of getClass here for two reasons
	    if ( !(aThat instanceof JsonVote) ) return false;

	    //cast to native object is now safe
	    JsonVote that = (JsonVote) aThat;

	    //now a proper field-by-field evaluation can be made
	    return
	    	((this.voteId != null) ? this.voteId.equals(that.voteId) : that.voteId == null) &&
	    	((this.voteUserEmail != null) ? this.voteUserEmail.equals(that.voteUserEmail) : that.voteUserEmail == null) &&
	    	((this.voteLastRate != null) ? this.voteLastRate.equals(that.voteLastRate) : that.voteLastRate == null) &&
	    	(this.endTime == that.endTime);
	}


}
