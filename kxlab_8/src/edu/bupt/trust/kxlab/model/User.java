package edu.bupt.trust.kxlab.model;

public class User {
	private String email;
	private String userName;
	private String password;
	private String photoLocation;
	private String gender;
	private String timeEnter;
	private String activityScore;
	private String Source;
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
		isLogin=false;
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
	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}
	
}
