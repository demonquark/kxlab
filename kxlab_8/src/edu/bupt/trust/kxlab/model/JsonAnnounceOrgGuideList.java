package edu.bupt.trust.kxlab.model;

import java.util.ArrayList;
import java.util.List;

import edu.bupt.trust.kxlab.utils.JsonTools;
import edu.bupt.trust.kxlab.utils.Loggen;

public class JsonAnnounceOrgGuideList extends JsonItem {
	
	public List<JsonAnnounceFAQ> content;
	public List<JsonSortFeatures> sort;
	public int totalElements;
	public int numberOfElements;
	public int totalPages;
	public boolean firstPage;
	public boolean lastPage;
	public int size;
	public int number;
	
	public JsonAnnounceOrgGuideList() {
		content = new ArrayList<JsonAnnounceFAQ> ();
		sort = new ArrayList<JsonSortFeatures> ();
		totalElements = 0;
		numberOfElements = 0;
		totalPages = 0;
		firstPage = true;
		lastPage = true;
		size = 0;
		number = 0;
	}
	
	public JsonAnnounceOrgGuideList(JsonAnnounceOrgGuideList announceorguideList) {
		this();
		this.setFromJsonItem(announceorguideList, false);
	}
	
	public JsonAnnounceOrgGuideList(ArrayList<JsonAnnounceFAQ> announcements) {
		this();
		if(announcements != null){ content = announcements; }
	}
	
	@Override public JsonItem getJsonItem() {
		JsonAnnounceOrgGuideList newInstance = new JsonAnnounceOrgGuideList();
		newInstance.setFromJsonItem(this, false);
		return newInstance;
	}
	
	@Override public boolean setFromJsonItem(JsonItem aThat, boolean allowNull) {
		boolean isAnnounce = (aThat instanceof JsonAnnounceOrgGuideList);
		
		if(isAnnounce){
			JsonAnnounceOrgGuideList that = (JsonAnnounceOrgGuideList) aThat;
			content 			= allowNull ? that.content : copyAnnounce(that.content);
			sort 				= allowNull ? that.sort : copySortFeatures(that.sort);
			totalElements 		= that.totalElements;
			numberOfElements 	= that.numberOfElements;
			totalPages 			= that.totalPages;
			firstPage		 	= that.firstPage;
			lastPage 			= that.lastPage;
			size 				= that.size;
			number 				= that.number;
		}
		return isAnnounce;
	}
	@Override public int getId() {
		return -1;
	}

	public void updateWithNew(JsonAnnounceOrgGuideList b, boolean pushToEnd) {
		
		if(b != null){
			// Step 0 - make sure we have content
			if(content == null) { content = new ArrayList<JsonAnnounceFAQ> (); }
			
			// Step 1 - copy the new records
			List<JsonAnnounceFAQ> newRecords = copyAnnounce(b.content);
					
			// Step 2 - update the old records with the new records
			JsonTools.replaceListOverlap(content, newRecords);
			
			// Step 3 - add the new records to the old records
			if(pushToEnd) { 
				content.addAll(newRecords);
			} else {
				content.addAll(0, newRecords);
			}
			
			// TODO: Repeat for the sort Features (We can't really update sort features, they have no id) 
			sort = copySortFeatures(b.sort);
			
			// Step 4 - update the rest
			totalElements 		= b.totalElements;
			numberOfElements 	= b.numberOfElements;
			totalPages 			= b.totalPages;
			firstPage		 	= b.firstPage;
			lastPage 			= b.lastPage;
			size 				= b.size;
			number 				= b.number;
		} else {
			Loggen.e(this, "Cannot update with null");
		}
	}
	
	private List <JsonSortFeatures> copySortFeatures(List <JsonSortFeatures> toCopy){
		List<JsonSortFeatures> newCopy = new ArrayList<JsonSortFeatures> ();
		if(toCopy != null){
			for(JsonSortFeatures item : toCopy){
				newCopy.add(new JsonSortFeatures(item)); 
			}
		}
		return newCopy;
	}
	
	private List <JsonAnnounceFAQ> copyAnnounce(List <JsonAnnounceFAQ> toCopy){
		List<JsonAnnounceFAQ> newCopy = new ArrayList<JsonAnnounceFAQ> ();
		if(toCopy != null){
			for(JsonAnnounceFAQ item : toCopy){
				newCopy.add(new JsonAnnounceFAQ(item)); 
			}
		}
		return newCopy;
	}
}
