package edu.bupt.trust.kxlab.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class ActivityHistory implements Parcelable {
	
	private int finalgrade;
	private List<ActivityRecord> records;
	
	public ActivityHistory(){
		records = new ArrayList <ActivityRecord>();
	}
	
    private ActivityHistory(Parcel in) {
    	// Note: you need to read the items in the same order that you wrote them
    	finalgrade = in.readInt();
    	records = new ArrayList <ActivityRecord> ();
    	in.readList(records, null);
    }

    // this is used to regenerate your object.
    public static final Parcelable.Creator<ActivityHistory> CREATOR = new Parcelable.Creator<ActivityHistory>() {
        public ActivityHistory createFromParcel(Parcel in) { return new ActivityHistory(in); }
        public ActivityHistory[] newArray(int size) { return new ActivityHistory[size]; }
    };

    @Override public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	// Note: you need to write the items in the same order that you intend to read them
    	dest.writeInt(finalgrade);
    	dest.writeList(records);
    }

	
	public int getFinalGrade(){
		return finalgrade;
	}
	
	public List <ActivityRecord> getRecords(){
		return records;
	}
	
	public void addRecord(ActivityRecord record){
		if(record != null) { records.add(record); }
	}
}
