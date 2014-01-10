package edu.bupt.trust.kxlab.model;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;

public class ActivityRecord implements Parcelable {
	private int ahId;
	private long activityTime;
	private String whatDo;
	private String email;
	private int score;
	
	public ActivityRecord(){
		ahId = -1;
		activityTime = 0;
		whatDo = "";
		email = "";
		score = 0;
	}
	
	public ActivityRecord(long date, String whatDo, String email, int score){
		this.ahId = -1;
		this.activityTime = date;
		this.whatDo = whatDo;
		this.email = email;
		this.score = score;
	}

    private ActivityRecord(Parcel in) {
    	// Note: you need to read the items in the same order that you wrote them
    	ahId = in.readInt();
    	activityTime = in.readLong();
    	whatDo = in.readString();
    	email = in.readString();
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
    	dest.writeInt(ahId);
    	dest.writeLong(activityTime);
    	dest.writeString(whatDo);
    	dest.writeString(email);
    	dest.writeInt(score);
    }
	
	public int getAhId() {
		return ahId;
	}

	public long getActivityTime() {
		return activityTime;
	}

	public String getWhatDo() {
		return whatDo;
	}

	public String getEmail() {
		return email;
	}

	public void setAhId(int ahId) {
		this.ahId = ahId;
	}

	public void setActivityTime(long activityTime) {
		this.activityTime = activityTime;
	}

	public void setWhatDo(String whatDo) {
		this.whatDo = whatDo;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getScore(){
		return score;
	}
	
	public void setScore(int score){
		this.score = score;
	}
	
	public String getDateString(){
		return new SimpleDateFormat("MMM dd ''yy HH:mm:ss", Locale.US).format(activityTime);
	}
}


