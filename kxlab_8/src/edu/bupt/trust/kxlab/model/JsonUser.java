package edu.bupt.trust.kxlab.model;

import android.os.Parcel;
import android.os.Parcelable;

public class JsonUser extends JsonItem implements Parcelable{
	
	public int id;
	public String name;
	public String password;
	public int type;
	public String sex;
	public String photo;
	public String email;
	public String phonenumber;
	public long jointime;
	public long lastLoginTime;
	public int activityScore;
	public int roleId;
	
	public String localPhoto;
	
	public JsonUser() {
		id				= -1;
		name			= "";
		password		= "";
		type			= -1;
		sex				= "";
		photo			= "";
		email			= "";
		phonenumber		= "";
		jointime		= 0;
		lastLoginTime	= 0;
		activityScore	= 0;
		roleId			= 0;
		
		localPhoto 		= "";
	}

	public JsonUser(JsonUser postSponsor) {
		this();
		setFromJsonItem(postSponsor, false);
	}
	
	public JsonUser(String email){
		this();
		if(email != null) { this.email = email; }
	}
	
	@Override public JsonItem getJsonItem() {
		JsonUser newInstance = new JsonUser();
		newInstance.setFromJsonItem(this, false);
		return newInstance;
	}
	
	@Override public boolean setFromJsonItem(JsonItem aThat, boolean allowNull) {
		boolean isUser = (aThat instanceof JsonUser);
		
		if(isUser){
			JsonUser that 	= (JsonUser) aThat;
			id				= that.id;
			name			= allowNull ? that.name : replace(name, that.name);
			password		= allowNull ? that.password : replace(password, that.password);
			type			= that.type;
			sex				= allowNull ? that.sex : replace(sex, that.sex);
			photo			= allowNull ? that.photo : replace(photo, that.photo);
			email			= allowNull ? that.email : replace(email, that.email);
			phonenumber		= allowNull ? that.phonenumber : replace(phonenumber, that.phonenumber);
			jointime		= that.jointime;
			lastLoginTime	= that.lastLoginTime;
			activityScore	= that.activityScore;
			roleId			= that.roleId;
			localPhoto 		= allowNull ? that.localPhoto : replace(localPhoto, that.localPhoto);
		}
		
		return isUser;
	}

	@Override public int getId() {
		return id;
	}
	
    private JsonUser(Parcel in) {
    	// Note: you need to read the items in the same order that you wrote them
		id				= in.readInt();
		name			= in.readString();
		password		= in.readString();
		type			= in.readInt();
		sex				= in.readString();
		photo			= in.readString();
		email			= in.readString();
		phonenumber		= in.readString();
		jointime		= in.readLong();
		lastLoginTime	= in.readLong();
		activityScore	= in.readInt();
		roleId			= in.readInt();
		localPhoto		= in.readString();
    }

    // this is used to regenerate your object.
    public static final Parcelable.Creator<JsonUser> CREATOR = new Parcelable.Creator<JsonUser>() {
        public JsonUser createFromParcel(Parcel in) { return new JsonUser(in); }
        public JsonUser[] newArray(int size) { return new JsonUser[size]; }
    };

    @Override public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	// Note: you need to write the items in the same order that you intend to read them
		dest.writeInt(id);
    	dest.writeString(name);
    	dest.writeString(password);
		dest.writeInt(type);
    	dest.writeString(sex);
    	dest.writeString(photo);
    	dest.writeString(email);
    	dest.writeString(phonenumber);
    	dest.writeLong(jointime);
    	dest.writeLong(lastLoginTime);
    	dest.writeInt(activityScore);
    	dest.writeInt(roleId);
    	dest.writeString(localPhoto);
    }

	
	@Override public boolean equals(Object aThat) {
		if ( this == aThat ) return true;

	    //use instanceof instead of getClass here for two reasons
	    if ( !(aThat instanceof JsonUser) ) return false;

	    //cast to native object is now safe
	    JsonUser that = (JsonUser) aThat;

	    //now a proper field-by-field evaluation can be made
	    return
	    	(this.id == that.id) &&
	    	((this.name != null) ? this.name.equals(that.name) : that.name == null) &&
	    	((this.password != null) ? this.password.equals(that.password) : that.password == null) &&
	    	(this.type == that.type) &&
	    	((this.sex != null) ? this.sex.equals(that.sex) : that.sex == null) &&
	    	((this.photo != null) ? this.photo.equals(that.photo) : that.photo == null) &&
	    	((this.email != null) ? this.email.equals(that.email) : that.email == null) &&
	    	((this.phonenumber != null) ? this.phonenumber.equals(that.phonenumber) : that.phonenumber == null) &&
	    	(this.jointime == that.jointime) &&
	    	(this.lastLoginTime == that.lastLoginTime) &&
	    	(this.activityScore == that.activityScore) &&
	    	(this.roleId == that.roleId);
	}
}
