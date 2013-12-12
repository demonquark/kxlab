package edu.bupt.trust.kxlab.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserInformation implements Parcelable {
	private String photoImage;
	private String userName;
	private String userEmail;
	private String phoneNumber;
	private String timeEnter;
	private String activityScore;
	private String Source;

	public UserInformation() {
		photoImage = "";
		userName = "";
		userEmail = "";
		phoneNumber = "";
		timeEnter = "";
		activityScore = "";
		Source = "";
	}
	
	public UserInformation(String email) {
		photoImage = "";
		userName = "";
		userEmail = (email != null) ? email : "";
		phoneNumber = "";
		timeEnter = "";
		activityScore = "";
		Source = "";
	}
	
    private UserInformation(Parcel in) {
    	// Note: you need to read the items in the same order that you wrote them
		photoImage = in.readString();
		userName = in.readString();
		userEmail = in.readString();
		phoneNumber = in.readString();
		timeEnter = in.readString();
		activityScore = in.readString();
		Source = in.readString();
    }

    // this is used to regenerate your object.
    public static final Parcelable.Creator<UserInformation> CREATOR = new Parcelable.Creator<UserInformation>() {
        public UserInformation createFromParcel(Parcel in) { return new UserInformation(in); }
        public UserInformation[] newArray(int size) { return new UserInformation[size]; }
    };

    @Override public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	// Note: you need to write the items in the same order that you intend to read them
    	dest.writeString(photoImage);
    	dest.writeString(userName);
    	dest.writeString(userEmail);
    	dest.writeString(phoneNumber);
    	dest.writeString(timeEnter);
    	dest.writeString(activityScore);
    	dest.writeString(Source);
    }

	// Getter and Setters 
	
	public String getPhotoImage() {
		return photoImage;
	}

	public void setPhotoImage(String photoImage) {
		this.photoImage = (photoImage != null) ? photoImage : "";
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = (userName != null) ? userName : "";
	}
	
	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String email) {
		this.userEmail = (email != null) ? email : "";
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = (phoneNumber != null) ? phoneNumber : "";
	}

	public String getTimeEnter() {
		return timeEnter;
	}

	public void setTimeEnter(String timeEnter) {
		this.timeEnter = (timeEnter != null) ? timeEnter : "";
	}

	public String getActivityScore() {
		return activityScore;
	}

	public void setActivityScore(String activityScore) {
		this.activityScore = (activityScore != null) ? activityScore : "";
	}

	public String getSource() {
		return Source;
	}

	public void setSource(String source) {
		Source = source;
	}
	
	
}
