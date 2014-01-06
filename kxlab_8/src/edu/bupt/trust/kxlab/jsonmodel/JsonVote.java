package edu.bupt.trust.kxlab.jsonmodel;

public class JsonVote {
	
	String voteId;
	String voteUserEmail;
	String voteLastRate;	
	long endTime;
	
	public JsonVote(){
		voteId = "0";
		voteUserEmail = "";
		voteLastRate = "0";
		endTime = 0;
	}
	
	public String getVoteId() {
		return voteId;
	}
	public String getVoteUserEmail() {
		return voteUserEmail;
	}
	public long getEndTime() {
		return endTime;
	}
	public String getVoteLastRate() {
		return voteLastRate;
	}
	public void setVoteId(String voteId) {
		this.voteId = voteId;
	}
	public void setVoteUserEmail(String voteUserEmail) {
		this.voteUserEmail = voteUserEmail;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public void setVoteLastRate(String voteLastRate) {
		this.voteLastRate = voteLastRate;
	}
	
	public void updateFromJsonVote(JsonVote other) {
		voteId = replace(voteId, other.voteId);
		voteUserEmail = replace(voteUserEmail, other.voteUserEmail);
		voteLastRate = replace(voteLastRate, other.voteLastRate);	
		endTime = other.endTime;
	}
	
	private String replace(String old, String replacement){
		if(replacement != null && !replacement.equals("") && !replacement.equals("null")) { old = replacement; }
		return old;
	}

}
