package edu.bupt.trust.kxlab.model;

import java.text.SimpleDateFormat;
import java.util.Locale;

import edu.bupt.trust.kxlab.jsonmodel.JsonUser;
import android.os.Parcel;
import android.os.Parcelable;

// TODO: FIX USER TO MATCH JSON
public class User implements Parcelable {
	private String email;
	private String userName;
	private String password;
	private String photoLocation;
	private String gender;
	private String timeEnter;
	private String activityScore;
	private int Source;
	private String lastLoginTime;
	private String phoneNumber;
	private String webPhoto;
	private boolean isLogin;
	public int id;
	private int roleId;
	
	public User() {
		email = "";
		userName = "";
		password = "";
		photoLocation = "";
		gender = "";
		timeEnter = "";
		activityScore = "";
		Source = 1;
		phoneNumber = "";
		isLogin=false;
		roleId= 1;
	}
	
	public User(String email) {
		this.email = email;
		userName = "";
		password = "";
		photoLocation = "";
		gender = "";
		timeEnter = "";
		activityScore = "";
		Source = 1;
		phoneNumber = "";
		isLogin=false;
		roleId = 1;
	}
	
	public User(JsonUser userinfo){
		this();
		if(userinfo != null){
			id = userinfo.getId();
			email = userinfo.getEmail();
			userName = userinfo.getName();
			password = userinfo.getPassword();
			photoLocation = userinfo.getPhoto();
			gender = userinfo.getSex();
			timeEnter = userinfo.getJointime();
			activityScore = String.valueOf(userinfo.getActivityScore());
			Source = userinfo.getType();
			phoneNumber = userinfo.getPhonenumber();
			isLogin = false;
			roleId = userinfo.getRoleId();
		}
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
		Source = in.readInt();
		phoneNumber = in.readString();
		isLogin = (in.readInt() != 0);
		roleId = in.readInt();
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
    	dest.writeInt(Source);
    	dest.writeString(phoneNumber);
    	dest.writeInt(isLogin ? 1 : 0);
    	dest.writeInt(roleId);
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

	public int getSource() {
		return Source;
	}

	public void setSource(int source) {
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
		roleId= otherUser.roleId;
	}
	
	
	public JsonUser getJsonUser(){

		JsonUser userinfo = new JsonUser();
		userinfo.setEmail(email);
		userinfo.setName(userName);
		userinfo.setPassword(password);
		userinfo.setSex(gender);
		userinfo.setPhoto(webPhoto);
		userinfo.setPhonenumber(phoneNumber);
		userinfo.setJointime(timeEnter);
		userinfo.setLastLoginTime(lastLoginTime);
		userinfo.setPhoto(photoLocation);
		try{ userinfo.setActivityScore(Integer.parseInt(activityScore));
		}catch(NumberFormatException e){ userinfo.setActivityScore(0); }
		userinfo.setRoleId(roleId);
		userinfo.setType(Source);
		userinfo.setId(id);
		
		return userinfo;
	}

	public String getTimeEnterString(){

		long date = 0;
		try{
			date = Long.parseLong(timeEnter);
		} catch(Exception e){ }
		
		return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date);
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
	    	(this.Source == that.Source) &&
	    	((this.phoneNumber != null) ? this.phoneNumber.equals(that.phoneNumber) : that.phoneNumber == null) &&
	    	(this.isLogin == that.isLogin);
	}


}
