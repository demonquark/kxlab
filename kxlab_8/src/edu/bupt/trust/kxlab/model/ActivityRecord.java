package edu.bupt.trust.kxlab.model;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ActivityRecord {
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


