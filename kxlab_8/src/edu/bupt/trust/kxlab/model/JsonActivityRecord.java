package edu.bupt.trust.kxlab.model;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;

public class JsonActivityRecord extends JsonItem implements Parcelable {
	public int ahId;
	public long activityTime;
	public String whatDo;
	public String email;
	public int score;
	
	public JsonActivityRecord(){
		ahId 			= -1;
		activityTime 	= 0;
		whatDo 			= "";
		email 			= "";
		score 			= 0;
	}

	public JsonActivityRecord(JsonActivityRecord record){
		this();
		this.setFromJsonItem(record, false);
	}

	@Override public JsonItem getJsonItem() {
		JsonActivityRecord newInstance = new JsonActivityRecord();
		newInstance.setFromJsonItem(this, false);
		return newInstance;
	}

	@Override public boolean setFromJsonItem(JsonItem aThat, boolean allowNull) {
		boolean isRecord = (aThat instanceof JsonActivityRecord);
		
		if(isRecord){
			JsonActivityRecord that = (JsonActivityRecord) aThat;

			ahId 			= that.ahId;
			activityTime 	= that.activityTime;
			whatDo 			= allowNull ? that.whatDo : replace(whatDo, that.whatDo);
			email 			= allowNull ? that.email : replace(email, that.email);
			score 			= that.score;
		}
		
		return isRecord;
	}

	@Override public int getId() {
		return ahId;
	}

	public String getDateString(){
		return new SimpleDateFormat("MMM dd ''yy HH:mm:ss", Locale.US).format(activityTime);
	}

	private JsonActivityRecord(Parcel in) {
    	// Note: you need to read the items in the same order that you wrote them
    	ahId = in.readInt();
    	activityTime = in.readLong();
    	whatDo = in.readString();
    	email = in.readString();
		score = in.readInt();
    }

    // this is used to regenerate your object.
    public static final Parcelable.Creator<JsonActivityRecord> CREATOR = new Parcelable.Creator<JsonActivityRecord>() {
        public JsonActivityRecord createFromParcel(Parcel in) { return new JsonActivityRecord(in); }
        public JsonActivityRecord[] newArray(int size) { return new JsonActivityRecord[size]; }
    };

    @Override public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	// Note: you need to write the items in the same order that you intend to read them
    	dest.writeInt(ahId);
    	dest.writeLong(activityTime);
    	dest.writeString(whatDo);
    	dest.writeString(email);
    	dest.writeInt(score);
    }
    
	@Override public boolean equals(Object aThat) {
		if ( this == aThat ) return true;

	    //use instanceof instead of getClass here for two reasons
	    if ( !(aThat instanceof JsonActivityRecord) ) return false;

	    //cast to native object is now safe
	    JsonActivityRecord that = (JsonActivityRecord) aThat;

	    //now a proper field-by-field evaluation can be made
	    return
	    	(this.ahId == that.ahId) &&
	    	(this.activityTime == that.activityTime) &&
	    	((this.whatDo != null) ? this.whatDo.equals(that.whatDo) : that.whatDo == null) &&
	    	((this.email != null) ? this.email.equals(that.email) : that.email == null) &&
	    	(this.score == that.score) ;
	}
}