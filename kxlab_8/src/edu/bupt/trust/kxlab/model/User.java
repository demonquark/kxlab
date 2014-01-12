package edu.bupt.trust.kxlab.model;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;
public class User implements Parcelable {
	
	private JsonUser jsonUser;
	private boolean isLogin;
	
	public User() {
		jsonUser		= new JsonUser();
		isLogin			= false;
	}
	
	public User(User newUser){
		this();
		setFromUser(newUser);
	}

	public User(JsonUser user){
		this();
		this.setJsonUser(user);
	}
	
	public User (String email){
		this(new JsonUser(email));
	}
	
	public void setFromUser(User that){
		if(that != null) { jsonUser		= that.jsonUser; }
		isLogin			= that.isLogin;
	}

	public String getTimeEnterString(){
		long date = (jsonUser != null) ? jsonUser.jointime : 0;
		return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date);
	}
	
    private User(Parcel in) {
    	// Note: you need to read the items in the same order that you wrote them
    	jsonUser		= in.readParcelable(getClass().getClassLoader());
		isLogin 		= (in.readByte() != 0);
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
    	dest.writeParcelable(jsonUser, 0);
    	dest.writeByte((byte) (isLogin ? 1 : 0));
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
	    	((this.jsonUser != null) ? this.jsonUser.equals(that.jsonUser) : that.jsonUser == null) &&
	    	(this.isLogin == that.isLogin);
	}

	public boolean isLogin() {
		return isLogin;
	}
	
	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

	public JsonUser getJsonUser() {
		return jsonUser;
	}

	public String getLocalPhoto() {
		return jsonUser.localPhoto;
	}

	public void setJsonUser(JsonUser jsonUser) {
		if(jsonUser != null) { this.jsonUser = jsonUser; }
	}

	public void setLocalPhoto(String localPhoto) {
		// TODO: Tie this in with jsonUser photo ???
		if(localPhoto != null) { this.jsonUser.localPhoto = localPhoto; }
	}
	
	public int getId() {
		return jsonUser.id;
	}

	public String getName() {
		return jsonUser.name;
	}

	public String getPassword() {
		return jsonUser.password;
	}

	public int getType() {
		return jsonUser.type;
	}

	public String getSex() {
		return jsonUser.sex;
	}

	public String getEmail() {
		return jsonUser.email;
	}

	public String getPhonenumber() {
		return jsonUser.phonenumber;
	}

	public long getJointime() {
		return jsonUser.jointime;
	}

	public long getLastLoginTime() {
		return jsonUser.lastLoginTime;
	}

	public int getActivityScore() {
		return jsonUser.activityScore;
	}

	public int getRoleId() {
		return jsonUser.roleId;
	}

	public void setId(int id) {
		this.jsonUser.id = id;
	}

	public void setName(String name) {
		this.jsonUser.name = name;
	}

	public void setPassword(String password) {
		this.jsonUser.password = password;
	}

	public void setType(int type) {
		this.jsonUser.type = type;
	}

	public void setSex(String sex) {
		this.jsonUser.sex = sex;
	}

	public void setEmail(String email) {
		this.jsonUser.email = email;
	}

	public void setPhonenumber(String phonenumber) {
		this.jsonUser.phonenumber = phonenumber;
	}

	public void setJointime(long jointime) {
		this.jsonUser.jointime = jointime;
	}

	public void setLastLoginTime(long lastLoginTime) {
		this.jsonUser.lastLoginTime = lastLoginTime;
	}

	public void setActivityScore(int activityScore) {
		this.jsonUser.activityScore = activityScore;
	}

	public void setRoleId(int roleId) {
		this.jsonUser.roleId = roleId;
	}

}
