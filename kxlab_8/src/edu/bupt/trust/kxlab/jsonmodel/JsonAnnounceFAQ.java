package edu.bupt.trust.kxlab.jsonmodel;

public class JsonAnnounceFAQ {
	
	String agId;
	String agType;
	String agTitle;
	String agContent;
	String agPublishAuthor;
	long agPublishTime;
	long agLastEditTime;
	
	public String getAgId() {
		return agId;
	}
	public String getAgType() {
		return agType;
	}
	public String getAgTitle() {
		return agTitle;
	}
	public String getAgContent() {
		return agContent;
	}
	public String getAgPublishAuthor() {
		return agPublishAuthor;
	}
	public long getAgPublishTime() {
		return agPublishTime;
	}
	public long getAgLastEditTime() {
		return agLastEditTime;
	}
	public void setAgId(String agId) {
		this.agId = agId;
	}
	public void setAgType(String agType) {
		this.agType = agType;
	}
	public void setAgTitle(String agTitle) {
		this.agTitle = agTitle;
	}
	public void setAgContent(String agContent) {
		this.agContent = agContent;
	}
	public void setAgPublishAuthor(String agPublishAuthor) {
		this.agPublishAuthor = agPublishAuthor;
	}
	public void setAgPublishTime(long agPublishTime) {
		this.agPublishTime = agPublishTime;
	}
	public void setAgLastEditTime(long agLastEditTime) {
		this.agLastEditTime = agLastEditTime;
	}
	
	public void updateFromJsonAnnounceFAQ(JsonAnnounceFAQ other) {
		agId = replace(agId, other.agId);
		agType = replace(agType, other.agType);
		agTitle = replace(agTitle, other.agTitle);
		agContent = replace(agContent, other.agContent);
		agPublishAuthor = replace(agPublishAuthor, other.agPublishAuthor);
		agPublishTime = other.agPublishTime; 
		agLastEditTime = other.agLastEditTime;
	}
	
	private String replace(String old, String replacement){
		if(replacement != null && !replacement.equals("") && !replacement.equals("null")) { old = replacement; }
		return old;
	}

	
}
