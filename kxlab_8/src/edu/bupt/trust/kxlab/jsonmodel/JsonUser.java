package edu.bupt.trust.kxlab.jsonmodel;

public class JsonUser {
	
	private int id;
	private String name;
	private String password;
	private int type;
	private String sex;
	private String photo;
	private String email;
	private String phonenumber;
	private String jointime;
	private String lastLoginTime;
	private int activityScore;
	private int roleId;
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getPassword() {
		return password;
	}
	public int getType() {
		return type;
	}
	public String getSex() {
		return sex;
	}
	public String getPhoto() {
		return photo;
	}
	public String getEmail() {
		return email;
	}
	public String getPhonenumber() {
		return phonenumber;
	}
	public String getJointime() {
		return jointime;
	}
	public String getLastLoginTime() {
		return lastLoginTime;
	}
	public int getActivityScore() {
		return activityScore;
	}
	public int getRoleId() {
		return roleId;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setType(int type) {
		this.type = type;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	public void setJointime(String jointime) {
		this.jointime = jointime;
	}
	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public void setActivityScore(int activityScore) {
		this.activityScore = activityScore;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	
	public void updateFromJsonUser(JsonUser other){

		id = other.id;
		type = other.type;
		activityScore = other.activityScore;
		roleId = other.roleId;
		name = replace(name, other.name);
		password = replace(password, other.password);
		sex = replace(sex, other.sex);
		photo = replace(photo, other.photo);
		email = replace(email, other.email);
		phonenumber = replace(phonenumber, other.phonenumber);
		jointime = replace(jointime, other.jointime);
		lastLoginTime = replace(lastLoginTime, other.lastLoginTime);
	}
	
	private String replace(String old, String replacement){
		if(replacement != null && !replacement.equals("") && !replacement.equals("null")) { old = replacement; }
		return old;
	}
}
