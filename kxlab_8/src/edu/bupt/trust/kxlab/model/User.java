package edu.bupt.trust.kxlab.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
	private String email;
	private String userName;
	private String password;
	private String photoLocation;
	private String gender;
	private String timeEnter;
	private String activityScore;
	private String Source;

	public User() {
		email = "";
		userName = "";
		password = "";
		photoLocation = "";
		gender = "";
		timeEnter = "";
		activityScore = "";
		Source = "";
	}
	
	public User(String email) {
		this.email = email;
		userName = "";
		password = "";
		photoLocation = "";
		gender = "";
		timeEnter = "";
		activityScore = "";
		Source = "";
	}

    private User(Parcel in) {
    	// Note: you need to read the items in the same order that you wrote them
    	email = in.readString();
		userName = in.readString();
		password = in.readString();
		photoLocation = in.readString();
		gender = in.readString();
		timeEnter = in.readString();
		activityScore = in.readString();
		Source = in.readString();
    }

    // this is used to regenerate your object.
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) { return new User(in); }
        public User[] newArray(int size) { return new User[size]; }
    };

    @Override public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	// Note: you need to write the items in the same order that you intend to read them
    	dest.writeString(email);
    	dest.writeString(userName);
    	dest.writeString(password);
    	dest.writeString(photoLocation);
    	dest.writeString(gender);
    	dest.writeString(timeEnter);
    	dest.writeString(activityScore);
    	dest.writeString(Source);
    }

	
	/**
	 * Getter and Setters
	 * @return
	 */
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhotoLocation() {
		return photoLocation;
	}

	public void setPhotoLocation(String photoLocation) {
		this.photoLocation = photoLocation;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getTimeEnter() {
		return timeEnter;
	}

	public void setTimeEnter(String timeEnter) {
		this.timeEnter = timeEnter;
	}

	public String getActivityScore() {
		return activityScore;
	}

	public void setActivityScore(String activityScore) {
		this.activityScore = activityScore;
	}

	public String getSource() {
		return Source;
	}

	public void setSource(String source) {
		Source = source;
	}
	
	
}
