package edu.bupt.trust.kxlab.model;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;

public class ActivityRecord implements Parcelable {
	private long date;
	private String type;
	private int score;
	
	public ActivityRecord(){
		date = 0;
		type = "";
		score = 0;
	}
	
	public ActivityRecord(long date, String type, int score){
		this.date = date;
		this.type = type;
		this.score = score;
	}


    private ActivityRecord(Parcel in) {
    	// Note: you need to read the items in the same order that you wrote them
    	date = in.readLong();
		type = in.readString();
		score = in.readInt();
    }

    // this is used to regenerate your object.
    public static final Parcelable.Creator<ActivityRecord> CREATOR = new Parcelable.Creator<ActivityRecord>() {
        public ActivityRecord createFromParcel(Parcel in) { return new ActivityRecord(in); }
        public ActivityRecord[] newArray(int size) { return new ActivityRecord[size]; }
    };

    @Override public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	// Note: you need to write the items in the same order that you intend to read them
    	dest.writeLong(date);
    	dest.writeString(type);
    	dest.writeInt(score);
    }
	
	public long getDate(){
		return date;
	}
	
	public String getType(){
		return type;
				
	}
	
	public int getScore(){
		return score;
	}
	
	public void setDate(long date){
		if(date > 0 ) { this.date = date; }
	}
	
	public void setType(String type){
		if(type != null) { this.type = type; }
	}
	
	public void setScore(int score){
		this.score = score;
	}
	
	public String getDateString(){
		return new SimpleDateFormat("MMM dd ''yy HH:mm:ss", Locale.US).format(date);
	}
}


