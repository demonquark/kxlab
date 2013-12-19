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
	private String phoneNumber;
	private boolean isLogin;
	
	public User() {
		email = "";
		userName = "";
		password = "";
		photoLocation = "";
		gender = "";
		timeEnter = "";
		activityScore = "";
		Source = "";
		phoneNumber = "";
		isLogin=false;
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
		phoneNumber = "";
		isLogin=false;
	}
	
	public User(UserInformation userinfo){
		email = userinfo.getUserEmail();
		userName = userinfo.getUserName();
		password = "";
		photoLocation = "";
		gender = "";
		timeEnter = userinfo.getTimeEnter();
		activityScore = userinfo.getActivityScore();
		Source = userinfo.getSource();
		phoneNumber = userinfo.getPhoneNumber();
		isLogin=false;
	}

	public User(User newUser){
		setFromUser(newUser);
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
		phoneNumber = in.readString();
		isLogin = (in.readInt() != 0);
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
    	dest.writeString(phoneNumber);
    	dest.writeInt(isLogin ? 1 : 0);
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
	
	public String getPhoneNumber(){
		return this.phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber){
		if(phoneNumber != null) { this.phoneNumber = phoneNumber; }
	}
	
	public boolean isLogin() {
		return isLogin;
	}
	
	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}
	
	public void setFromUser(User otherUser){
		email = otherUser.getEmail();
		userName = otherUser.getUserName();
		password = otherUser.getPassword();
		photoLocation = otherUser.getPhotoLocation();
		gender = otherUser.getGender();
		timeEnter = otherUser.getTimeEnter();
		activityScore = otherUser.getActivityScore();
		Source = otherUser.getSource();
		phoneNumber = otherUser.getPhoneNumber();
		isLogin= otherUser.isLogin;
	}
	
	@Override 
	public boolean equals(Object aThat) {
		if ( this == aThat ) return true;

	    //use instanceof instead of getClass here for two reasons
	    if ( !(aThat instanceof User) ) return false;

	    //cast to native object is now safe
	    User that = (User)aThat;

	    //now a proper field-by-field evaluation can be made
	    return
	    	((this.email != null) ? this.email.equals(that.email) : that.email == null) &&
	    	((this.userName != null) ? this.userName.equals(that.userName) : that.userName == null) &&
	    	((this.password != null) ? this.password.equals(that.password) : that.password == null) &&
	    	((this.photoLocation != null) ? this.photoLocation.equals(that.photoLocation) : that.photoLocation == null) &&
	    	((this.gender != null) ? this.gender.equals(that.gender) : that.gender == null) &&
	    	((this.timeEnter != null) ? this.timeEnter.equals(that.timeEnter) : that.timeEnter == null) &&
	    	((this.activityScore != null) ? this.activityScore.equals(that.activityScore) : that.activityScore == null) &&
	    	((this.Source != null) ? this.Source.equals(that.Source) : that.Source == null) &&
	    	((this.phoneNumber != null) ? this.phoneNumber.equals(that.phoneNumber) : that.phoneNumber == null) &&
	    	(this.isLogin == that.isLogin);
	}


}
