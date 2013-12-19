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
	
	public int getFinalGrade(){
		return finalgrade;
	}
	
	public List <ActivityRecord> getRecords(){
		return records;
	}
	
	public void addRecord(ActivityRecord record){
		if(record != null) { records.add(record); }
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
}
